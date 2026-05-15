package repo;

import domain.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private ConnectDB dbUtils;

    public BookRepository(ConnectDB dbUtils) {
        this.dbUtils = dbUtils;
    }

    /**
     * Găsește toate cărțile din baza de date.
     * Folosește metoda extractBook pentru a evita duplicarea codului.
     */
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement(sql);
             ResultSet rs = preStmt.executeQuery()) {

            while (rs.next()) {
                books.add(extractBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error findAll: " + e.getMessage());
        }
        return books;
    }

    /**
     * Filtrează cărțile după titlu (folosind LIKE pentru căutare parțială).
     */
    public List<Book> filterByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";

        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement(sql)) {

            preStmt.setString(1, "%" + title + "%");
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filterByTitle: " + e.getMessage());
        }
        return books;
    }

    /**
     * Filtrează cărțile după gen.
     */
    public List<Book> getBooksByGenre(String genre) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE genre = ?";

        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement(sql)) {

            preStmt.setString(1, genre);
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getBooksByGenre: " + e.getMessage());
        }
        return books;
    }

    /**
     * Metodă privată pentru extragerea unui obiect Book dintr-un ResultSet.
     * Asigură-te că numele coloanelor corespund cu baza ta de date SQLite.
     */
    private Book extractBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getDouble("price"),
                rs.getString("genre"),
                rs.getInt("quantity")
        );
    }
}