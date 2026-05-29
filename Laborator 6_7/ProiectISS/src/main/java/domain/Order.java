package domain;

import java.util.Map;

public class Order {
    private int id;
    private User user;
    private double totalPrice;
    private String date;
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryEmail;
    private String deliveryAddress;
    private Map<Book, Integer> items;

    // Constructor complet
    public Order(int id, User user, double totalPrice, String date,
                 String deliveryName, String deliveryPhone, String deliveryEmail, String deliveryAddress) {
        this.id = id;
        this.user = user;
        this.totalPrice = totalPrice;
        this.date = date;
        this.deliveryName = deliveryName;
        this.deliveryPhone = deliveryPhone;
        this.deliveryEmail = deliveryEmail;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters și Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDeliveryName() { return deliveryName; }
    public void setDeliveryName(String deliveryName) { this.deliveryName = deliveryName; }

    public String getDeliveryPhone() { return deliveryPhone; }
    public void setDeliveryPhone(String deliveryPhone) { this.deliveryPhone = deliveryPhone; }

    public String getDeliveryEmail() { return deliveryEmail; }
    public void setDeliveryEmail(String deliveryEmail) { this.deliveryEmail = deliveryEmail; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public Map<Book, Integer> getItems() { return items; }
    public void setItems(Map<Book, Integer> items) { this.items = items; }
}