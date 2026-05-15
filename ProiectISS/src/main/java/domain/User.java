package domain;

public class User {
    private int id;
    private String username;
    private String password;
    private String address;
    private String email;
    private String telephone;

    public User(int id, String username, String password, String address, String email, String telephone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
        this.email = email;
        this.telephone = telephone;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
