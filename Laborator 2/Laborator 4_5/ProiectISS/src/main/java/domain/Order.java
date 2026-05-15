package domain;

import java.util.List;

public class Order {
    private String id;
    private String date;
    private List<Book> books; // Legătura directă cu obiectele Book
    private double totalPrice;
    private String address;

    public Order(String id, String date, List<Book> books, double totalPrice, String address) {
        this.id = id;
        this.date = date;
        this.books = books;
        this.totalPrice = totalPrice;
        this.address = address;
    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getDate() { return date; }
    public List<Book> getBooks() { return books; }
    public double getTotalPrice() { return totalPrice; }
    public String getAddress() { return address; }

    // --- SETTERS ---
    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setBooks(List<Book> books) { this.books = books; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setAddress(String address) { this.address = address; }
}