package domain;

public class Book {
    private int id;
    private String title;
    private String author;
    private double price;
    private String genre;
    private int quantity;

    public Book(int id, String title, String author, double price, String genre,  int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.genre = genre;
        this.quantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
    public String getGenre() { return genre; }
    public int getQuantity() { return quantity; }

    // Setters (pentru când vrei să modifici datele)
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(double price) { this.price = price; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        // Folosește java.util.Objects pentru generare simplă
        return java.util.Objects.hash(id);
    }
}