package service;

import domain.Book;
import domain.User;
import org.mindrot.jbcrypt.BCrypt;
import repo.BookRepository;
import repo.UserRepository;
import java.util.List;

public class Service {
    private UserRepository userRepo;
    private BookRepository bookRepo;

    public Service(UserRepository userRepo, BookRepository bookRepo) {
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
    }

    public User handleLogin(String email, String password) throws Exception {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new Exception("Email-ul nu a fost găsit în sistem!");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new Exception("Parola introdusă este greșită!");
        }

        return user;
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
}