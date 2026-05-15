

import gui.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repo.BookRepository;
import repo.ConnectDB;
import repo.UserRepository;
import service.Service;

import java.io.IOException;
import java.util.Properties;

public class MainClass extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties bdProps = new Properties();
        try {
            bdProps.load(MainClass.class.getResourceAsStream("/bd.config"));
            System.out.println("Proprietățile pentru bd au fost încărcate.");
            bdProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Nu am putut găsi bd.config: " + e);
            return;
        }

        ConnectDB connectionProvider = new ConnectDB(bdProps);

        BookRepository bookRepository = new BookRepository(connectionProvider);
        UserRepository userRepository = new UserRepository(connectionProvider);
        Service service = new Service(userRepository, bookRepository);

        MainWindow mw = new MainWindow(service);

        Scene scene = new Scene(mw, 850, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}