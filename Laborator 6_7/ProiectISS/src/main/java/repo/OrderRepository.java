package repo;

import domain.Book;
import domain.Order;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {
    private ConnectDB dbUtils;
    private BookRepository bookRepository;

    public OrderRepository(ConnectDB dbUtils, BookRepository bookRepository) {
        this.dbUtils = dbUtils;
        this.bookRepository = bookRepository;
    }

    /**
     * Salvează o comandă în baza de date și scade stocurile cărților.
     * Totul se execută ca o TRANZACȚIE (dacă ceva crapă, nu se salvează nimic).
     */
    public void save(Order order) {
        String insertOrderSql = "INSERT INTO orders (user_id, total_price, date, delivery_name, delivery_phone, delivery_email, delivery_address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO order_item (order_id, book_id, quantity) VALUES (?, ?, ?)";
        String updateStockSql = "UPDATE books SET quantity = quantity - ? WHERE id = ?";

        Connection con = null;
        try {
            con = dbUtils.getConnection();
            con.setAutoCommit(false); // Dezactivăm commit-ul automat ca să activăm Tranzacția!

            // 1. Inserăm comanda principală
            int orderId = -1;
            try (PreparedStatement orderStmt = con.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                if (order.getUser() != null) {
                    orderStmt.setInt(1, order.getUser().getId());
                } else {
                    orderStmt.setNull(1, Types.INTEGER); // NULL în DB pentru anonimi
                }
                orderStmt.setDouble(2, order.getTotalPrice());
                orderStmt.setString(3, order.getDate());
                orderStmt.setString(4, order.getDeliveryName());
                orderStmt.setString(5, order.getDeliveryPhone());
                orderStmt.setString(6, order.getDeliveryEmail());
                orderStmt.setString(7, order.getDeliveryAddress());

                orderStmt.executeUpdate();

                // Luăm ID-ul generat automat de SQLite pentru comanda asta
                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    }
                }
            }

            if (orderId == -1) {
                throw new SQLException("Eroare: Nu s-a putut genera ID-ul pentru comandă.");
            }

            // 2. Inserăm produsele și scădem stocul din tabela 'books'
            try (PreparedStatement itemStmt = con.prepareStatement(insertItemSql);
                 PreparedStatement stockStmt = con.prepareStatement(updateStockSql)) {

                for (Map.Entry<Book, Integer> entry : order.getItems().entrySet()) {
                    Book book = entry.getKey();
                    int qtyBought = entry.getValue();

                    // Adăugăm în order_item
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, book.getId());
                    itemStmt.setInt(3, qtyBought);
                    itemStmt.addBatch();

                    // Actualizăm stocul cărții direct în baza de date
                    stockStmt.setInt(1, qtyBought);
                    stockStmt.setInt(2, book.getId());
                    stockStmt.addBatch();
                }

                itemStmt.executeBatch(); // Executăm toate inserările de produse deodată
                stockStmt.executeBatch(); // Executăm toate scăderile de stoc deodată
            }

            con.commit(); // Dacă totul a mers brici, salvăm definitiv în DB!
            order.setId(orderId); // Îi punem ID-ul generat și obiectului din Java

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // Dacă a dat vreo eroare, dăm înapoi ca să nu stricăm baza de date!
                    System.err.println("Tranzacția a eșuat, s-a dat Rollback: " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("Eroare la Rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Eroare la închiderea conexiunii: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Aduce istoricul de comenzi pentru un anumit utilizator logat.
     * Folosește tehnica separării citirilor ca să nu blocăm SQLite-ul!
     */
    public List<Order> findByInregistrareUser(User user) {
        List<Order> history = new ArrayList<>();
        String selectOrdersSql = "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC";

        // Pasul 1: Citim toate comenzile de bază ale utilizatorului și le punem în listă
        try (Connection con = dbUtils.getConnection();
             PreparedStatement orderStmt = con.prepareStatement(selectOrdersSql)) {

            orderStmt.setInt(1, user.getId());
            try (ResultSet rsOrders = orderStmt.executeQuery()) {
                while (rsOrders.next()) {
                    Order order = new Order(
                            rsOrders.getInt("id"),
                            user,
                            rsOrders.getDouble("total_price"),
                            rsOrders.getString("date"),
                            rsOrders.getString("delivery_name"),
                            rsOrders.getString("delivery_phone"),
                            rsOrders.getString("delivery_email"),
                            rsOrders.getString("delivery_address")
                    );
                    order.setItems(new HashMap<>()); // Inițializăm mapa de produse goală
                    history.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citirea comenzilor de bază: " + e.getMessage());
            return history;
        }

        // Pasul 2: Citim absolut TOATE legăturile dintre comenzi și cărți din 'order_item' pentru acest user
        // Folosim un INNER JOIN ca să luăm doar produsele ce aparțin de comenzile acestui utilizator
        String selectAllItemsSql = "SELECT oi.order_id, oi.book_id, oi.quantity " +
                "FROM order_item oi " +
                "INNER JOIN orders o ON oi.order_id = o.id " +
                "WHERE o.user_id = ?";

        // O structură în care adunăm temporar toate datele brute din DB
        // Cheia e ID-ul comenzii, iar valoarea e o listă de perechi (ID_Carte -> Cantitate)
        Map<Integer, Map<Integer, Integer>> rawItemsMap = new HashMap<>();

        try (Connection con = dbUtils.getConnection();
             PreparedStatement itemStmt = con.prepareStatement(selectAllItemsSql)) {

            itemStmt.setInt(1, user.getId());
            try (ResultSet rsItems = itemStmt.executeQuery()) {
                while (rsItems.next()) {
                    int orderId = rsItems.getInt("order_id");
                    int bookId = rsItems.getInt("book_id");
                    int quantity = rsItems.getInt("quantity");

                    rawItemsMap.putIfAbsent(orderId, new HashMap<>());
                    rawItemsMap.get(orderId).put(bookId, quantity);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citirea tuturor produselor din istoric: " + e.getMessage());
        }

        // Pasul 3: Acum că DB-ul e complet închis și liber, asamblăm obiectele din Java în siguranță
        for (Order order : history) {
            Map<Integer, Integer> booksForThisOrder = rawItemsMap.get(order.getId());

            if (booksForThisOrder != null) {
                Map<Book, Integer> finalItems = new HashMap<>();
                for (Map.Entry<Integer, Integer> entry : booksForThisOrder.entrySet()) {
                    int bookId = entry.getKey();
                    int quantity = entry.getValue();

                    // Căutăm cartea completă prin BookRepository
                    Book book = bookRepository.findOne(bookId);
                    if (book != null) {
                        finalItems.put(book, quantity);
                    }
                }
                order.setItems(finalItems);
            }
        }

        return history;
    }
}