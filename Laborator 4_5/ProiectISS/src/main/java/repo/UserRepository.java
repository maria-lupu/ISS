package repo;

import domain.User;
import java.sql.*;

public class UserRepository {
    private ConnectDB dbUtils;

    public UserRepository(ConnectDB dbUtils) {
        this.dbUtils = dbUtils;
    }

    /**
     * Caută un utilizator în baza de date după adresa de email.
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement(sql)) {

            preStmt.setString(1, email);

            try (ResultSet rs = preStmt.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la căutarea utilizatorului după email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Metodă pentru extragerea unui obiect User dintr-un ResultSet.
     */
    private User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"), // Aici vine hash-ul BCrypt stocat în DB
                rs.getString("address"),
                rs.getString("email"),
                rs.getString("telephone")
        );
    }
}