package service;

import domain.Book;
import domain.Cart;
import domain.Order;
import domain.User;
import org.mindrot.jbcrypt.BCrypt;
import repo.BookRepository;
import repo.OrderRepository;
import repo.UserRepository;
import repo.CartRepository; // Am importat noul repo de coș

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {
    private UserRepository userRepo;
    private BookRepository bookRepo;
    private CartRepository cartRepo;
    private OrderRepository orderRepo;
    private Cart currentCart;

    public Service(UserRepository userRepo, BookRepository bookRepo, CartRepository cartRepo, OrderRepository orderRepo) {
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.cartRepo = cartRepo;
        this.orderRepo = orderRepo;
        this.currentCart = new Cart(null);
    }

    public void placeOrder(String name, String phone, String email, String address) throws Exception {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            throw new Exception("Nu poți plasa o comandă cu coșul gol!");
        }

        User loggedUser = currentCart.getUser();
        double total = currentCart.getItems().entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();

        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Order order = new Order(0, loggedUser, total, currentDate, name, phone, email, address);

        Map<Book, Integer> orderItems = new HashMap<>(currentCart.getItems());
        order.setItems(orderItems);
        orderRepo.save(order);

        for (Map.Entry<Book, Integer> entry : order.getItems().entrySet()) {
            Book bookInCart = entry.getKey();
            int qtyBought = entry.getValue();
            bookInCart.setQuantity(bookInCart.getQuantity() - qtyBought);
        }

        currentCart.getItems().clear();

        if (loggedUser != null) {
            cartRepo.save(currentCart);
        }


    }


    public List<Order> getLoggedUserOrderHistory() {
        if (currentCart == null || currentCart.getUser() == null) {
            return new ArrayList<>();
        }
        return orderRepo.findByInregistrareUser(currentCart.getUser());
    }

    public User handleLogin(String email, String password) throws Exception {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new Exception("Email-ul nu a fost găsit în sistem!");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new Exception("Parola introdusă este greșită!");
        }

       this.currentCart = cartRepo.findByInregistrareUser(user);

        return user;
    }

    public void handleLogout() {
       if (currentCart != null && currentCart.getUser() != null) {
            cartRepo.save(currentCart);
        }
       this.currentCart = new Cart(null);
    }

    public void addToCart(Book book) {
        if (currentCart == null) return;

        // Vedem câte bucăți din această carte sunt deja în coș momentan
        int qtyInCart = currentCart.getItems().getOrDefault(book, 0);

        // Verificăm dacă mai avem destule pe stoc în baza de date / obiect
        if (qtyInCart + 1 <= book.getQuantity()) {
            currentCart.addBook(book, 1);
        } else {
            // Opțional: poți lăsa așa sau să prinzi o eroare, dar interfața o să blocheze oricum butonul
            System.out.println("Nu mai poți adăuga! Stoc maxim atins pentru: " + book.getTitle());
        }
    }

    public void removeFromCart(Book book) {
        if (currentCart != null && currentCart.getItems().containsKey(book)) {
            int currentQty = currentCart.getItems().get(book);
            if (currentQty > 1) {
                currentCart.updateQuantity(book, currentQty - 1);
            } else {
                currentCart.removeBook(book);
            }
        }
    }

    public int getCartTotalItems() {
        if (currentCart == null) return 0;
        return currentCart.getTotalItemsCount();
   }

    public Map<Book, Integer> getCartItems() {
        if (currentCart == null) return new HashMap<>();
        return currentCart.getItems();
   }


    public List<Book> searchBooks(String title) {
        return bookRepo.filterByTitle(title);
   }

    public List<Book> filterByGenre(String genre) {
        return bookRepo.getBooksByGenre(genre);
    }

    public List<Book> findAll() {
        return bookRepo.findAll();
    }

    public User getLoggedUser() {
        return currentCart != null ? currentCart.getUser() : null;
    }
}