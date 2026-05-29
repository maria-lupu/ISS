package gui;

import domain.Order;
import domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import service.Service;

import java.util.ArrayList;
import java.util.List;

public class ClientWindow extends VBox {
    private Service service;
    private MainWindow mainApp;
    private User user;

    // Sfintele noastre culori și stiluri
    private final String PINK_DARKER = "#DB7093", TEXT_COLOR = "#5D4037";

    public ClientWindow(Service service, MainWindow mainApp, User user) {
        this.service = service;
        this.mainApp = mainApp;
        this.user = user;
    }

    public void show() {
        this.setPadding(new Insets(30));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: transparent;"); // Ia fundalul IVORY din MainWindow

        // --- HEADER (Titlu + Buton Logout aliniate la capete) ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label pTitlu = new Label("Profilul Meu");
        pTitlu.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        pTitlu.setTextFill(Color.web(TEXT_COLOR));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 7 15; -fx-background-radius: 5; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> {
            service.handleLogout();
            mainApp.setLoggedUser(null);
            mainApp.show(); // Reîncarcă MainWindow pe curat pentru vizitator anonim
        });
        header.getChildren().addAll(pTitlu, spacer, btnLogout);

        // --- INFORMAȚII CLIENT ---
        VBox info = new VBox(8);
        info.getChildren().addAll(
                new HBox(5, createBoldLabel("👤 Nume: "), new Label(user.getUsername())),
                new HBox(5, createBoldLabel("📧 Email: "), new Label(user.getEmail())),
                new HBox(5, createBoldLabel("📞 Telefon: "), new Label(user.getTelephone()))
        );

        this.getChildren().addAll(header, info);

        showOrderHistory();
    }

    private void showOrderHistory() {
        // --- SECȚIUNEA ISTORIC COMENZI ---
        Label hTitlu = new Label("Istoric Comenzi");
        hTitlu.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        hTitlu.setTextFill(Color.web(TEXT_COLOR));

        VBox hBox = new VBox(12);
        hBox.setStyle("-fx-background-color: transparent;");

        // Preluăm istoricul real salvat în baza de date pentru acest user din Service
        List<Order> orderHistory = service.getLoggedUserOrderHistory();

        if (orderHistory.isEmpty()) {
            Label emptyMsg = new Label("Încă nu ai efectuat nicio comandă.");
            emptyMsg.setFont(Font.font("System", FontPosture.ITALIC, 14));
            emptyMsg.setTextFill(Color.web(TEXT_COLOR));
            hBox.getChildren().add(emptyMsg);
        } else {
            for (Order o : orderHistory) {
                VBox orderCard = new VBox(6);
                orderCard.setPadding(new Insets(15));
                // Card alb frumos cu colțuri rotunjite și umbră fină
                orderCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);");

                Label d = new Label("📅 Data: " + o.getDate());
                d.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_COLOR + ";");

                // Mărunțim mapa de produse din comandă și scoatem numele lor + cantitatea
                List<String> bookTitles = new ArrayList<>();
                if (o.getItems() != null) {
                    o.getItems().forEach((book, qty) -> bookTitles.add(book.getTitle() + " (x" + qty + ")"));
                }

                Label b = new Label("📚 Cărți: " + String.join(", ", bookTitles));
                b.setWrapText(true);
                b.setTextFill(Color.web(TEXT_COLOR));

                Label a = new Label("🏠 Livrare: " + o.getDeliveryAddress());
                a.setTextFill(Color.web(TEXT_COLOR));

                Label p = new Label("💰 Total: " + o.getTotalPrice() + " RON");
                p.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PINK_DARKER + ";");

                orderCard.getChildren().addAll(d, b, a, p);
                hBox.getChildren().add(orderCard);
            }
        }

        // Adăugăm un ScrollPane pentru comenzi, ca să nu se spargă designul dacă ai multe rânduri
        ScrollPane scroll = new ScrollPane(hBox);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS); // Îi dăm voie să se întindă pe toată înălțimea rămasă
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        // Adăugăm toate elementele în acest VBox (ClientWindow)
        this.getChildren().addAll(new Separator(), hTitlu, scroll);

        // La final, se așează singur în centrul aplicației
        mainApp.setCenter(this);
    }

    private Label createBoldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_COLOR + ";");
        return l;
    }
}