package domain;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private User user;
    private Map<Book, Integer> items;

    public Cart(User user) {
        this.user = user;
        this.items = new HashMap<>();
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Map<Book, Integer> getItems() { return items; }
    public void setItems(Map<Book, Integer> items) { this.items = items; }

    public void addBook(Book book, int quantity) {
        items.put(book, items.getOrDefault(book, 0) + quantity);
    }

    public void removeBook(Book book) {
        items.remove(book);
    }

    public void updateQuantity(Book book, int quantity) {
        if (quantity <= 0) {
            items.remove(book);
        } else {
            items.put(book, quantity);
        }
    }

    public int getTotalItemsCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }
}