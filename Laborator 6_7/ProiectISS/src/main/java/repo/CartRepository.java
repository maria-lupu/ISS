package repo;

import domain.Book;
import domain.Cart;
import domain.User;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CartRepository {
    private ConnectDB dbUtils;
    private BookRepository bookRepository;

    public CartRepository(ConnectDB dbUtils, BookRepository bookRepository) {
        this.dbUtils = dbUtils;
        this.bookRepository = bookRepository;
    }

    public Cart findByInregistrareUser(User user) {
        Cart cart = new Cart(user);
        Map<Book, Integer> items = new HashMap<>();

        // O mapă temporară doar ca să salvăm ID-urile și cantitățile din DB rapid
        Map<Integer, Integer> rawData = new HashMap<>();

        String sql = "SELECT book_id, quantity FROM cart WHERE user_id = ?";

        // PASUL 1: Citim rapid doar numerele și închidem conexiunea imediat
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement(sql)) {

            preStmt.setInt(1, user.getId());
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    rawData.put(rs.getInt("book_id"), rs.getInt("quantity"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citirea ID-urilor din DB: " + e.getMessage());
        }

        // PASUL 2: Acum că baza de date e liberă, căutăm cărțile complet relaxați
        for (Map.Entry<Integer, Integer> entry : rawData.entrySet()) {
            int bookId = entry.getKey();
            int quantity = entry.getValue();

            Book book = bookRepository.findOne(bookId);
            if (book != null) {
                items.put(book, quantity);
            }
        }

        cart.setItems(items);
        return cart;
    }

    /**
     * Salvează masiv coșul utilizatorului la Logout (Șterge tot ce era vechi și pune varianta proaspătă).
     */
    public void save(Cart cart) {
        if (cart.getUser() == null) return;

        String deleteSql = "DELETE FROM cart WHERE user_id = ?";
        String insertSql = "INSERT INTO cart (user_id, book_id, quantity) VALUES (?, ?, ?)";

        try (Connection con = dbUtils.getConnection()) {
            con.setAutoCommit(false);

            // 1. Ștergem înregistrările vechi
            try (PreparedStatement delStmt = con.prepareStatement(deleteSql)) {
                delStmt.setInt(1, cart.getUser().getId());
                delStmt.executeUpdate();
            }

            // 2. Inserăm fiecare carte din dicționar
            try (PreparedStatement insStmt = con.prepareStatement(insertSql)) {
                for (Map.Entry<Book, Integer> entry : cart.getItems().entrySet()) {
                    insStmt.setInt(1, cart.getUser().getId());
                    insStmt.setInt(2, entry.getKey().getId());
                    insStmt.setInt(3, entry.getValue());
                    insStmt.addBatch(); // Folosim batch pentru viteză
                }
                insStmt.executeBatch();
            }

            con.commit();
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea coșului la logout: " + e.getMessage());
        }
    }
}