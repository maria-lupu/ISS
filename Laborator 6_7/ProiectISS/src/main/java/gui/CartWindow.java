package gui;

import domain.Book;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import service.Service;
import java.util.Map;

public class CartWindow extends VBox {
    private Service service;
    private MainWindow mainApp;

    // Păstrăm cu sfințenie culorile tale
    private final String PINK_LIGHT = "#FFB6C1", PINK_DARKER = "#DB7093", IVORY = "#FFFFF0", TEXT_COLOR = "#5D4037";

    public CartWindow(Service service, MainWindow mainApp) {
        this.service = service;
        this.mainApp = mainApp;
    }

    public void show() {
        this.getChildren().clear(); // Curățăm tot la refresh
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: " + IVORY + ";");

        Label title = new Label("Coșul tău");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(TEXT_COLOR));

        Map<Book, Integer> cartItems = service.getCartItems();

        if (cartItems.isEmpty()) {
            // --- MESAJUL TĂU DRĂGUȚ PENTRU COȘ GOL ---
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50, 0, 50, 0));

            Label emptyIcon = new Label("🛒✨");
            emptyIcon.setFont(Font.font("System", 48));

            Label emptyTitle = new Label("Coșul tău este gol momentan");
            emptyTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
            emptyTitle.setTextFill(Color.web(TEXT_COLOR));

            Label emptySub = new Label("Abia așteptăm să adaugi primele cărți în el și să înceapă magia lecturii!");
            emptySub.setFont(Font.font("System", FontPosture.ITALIC, 13));
            emptySub.setTextFill(Color.GRAY);

            emptyBox.getChildren().addAll(emptyIcon, emptyTitle, emptySub);
            this.getChildren().addAll(title, emptyBox);
        } else {
            VBox itemsBox = new VBox(12); // Distanța ta de 12 între rânduri
            itemsBox.setStyle("-fx-background-color: transparent;");

            double totalFinal = 0;

            for (Map.Entry<Book, Integer> entry : cartItems.entrySet()) {
                Book b = entry.getKey();
                int qty = entry.getValue();
                double totalItem = b.getPrice() * qty;
                totalFinal += totalItem;

                // --- CARTONAȘUL PENTRU FIECARE CARTE ---
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10, 15, 10, 15)); // Am micșorat puțin padding-ul de sus/jos ca să arate bine X-ul în colț
                row.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);");

                Label name = new Label(b.getTitle());
                name.setPrefWidth(220);
                name.setFont(Font.font("System", FontWeight.BOLD, 15));
                name.setTextFill(Color.web(TEXT_COLOR));
                name.setWrapText(true);

                // Butonul Minus
                Button minus = createCartBtn("-");
                minus.setOnAction(e -> {
                    service.removeFromCart(b);
                    mainApp.show(); // Update la badge-ul de la sidebar
                    show(); // Refresh la coș
                });

                Label qLab = new Label(String.valueOf(qty));
                qLab.setMinWidth(30);
                qLab.setAlignment(Pos.CENTER);
                qLab.setFont(Font.font("System", FontWeight.BOLD, 16));
                qLab.setTextFill(Color.web(TEXT_COLOR));

                // Butonul Plus
                Button plus = createCartBtn("+");
                if (qty >= b.getQuantity()) {
                    plus.setDisable(true);
                    plus.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-background-radius: 5;"); // Îl facem gri ca să fie clar
                }
                plus.setOnAction(e -> {
                    service.addToCart(b);
                    mainApp.show();
                    show();
                });

                // --- ELEMENTUL NOU: CONTROLUL CANTITĂȚII ÎN FUNCȚIE DE STOC ---
                HBox qtyControls = new HBox(5);
                qtyControls.setAlignment(Pos.CENTER_LEFT);

                if (b.getQuantity() == 0) {
                    minus.setDisable(true); // Înghețăm minusul
                    plus.setDisable(true);  // Înghețăm plusul

                    Label lblStocEpuizat = new Label("⚠️ Produs indisponibil (Stoc 0)");
                    lblStocEpuizat.setFont(Font.font("System", FontPosture.ITALIC, 12));
                    lblStocEpuizat.setTextFill(Color.RED);

                    qtyControls.getChildren().addAll(minus, qLab, plus, lblStocEpuizat);
                } else {
                    qtyControls.getChildren().addAll(minus, qLab, plus);
                }

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // --- ZONA DIN DREAPTA: PREȚUL ȘI X-UL DIN COLȚ ---
                VBox rightSideBox = new VBox();
                rightSideBox.setAlignment(Pos.TOP_RIGHT); // Îl forțăm să alinieze totul la dreapta-sus
                rightSideBox.setSpacing(15); // Oferă spațiu între X și preț pe verticală
                rightSideBox.setPrefWidth(100);

                // X-ul tău mic în colț
                Button btnDelete = new Button("×"); // Folosim caracterul special de înmulțire '×' pentru un look mai finuț decât 'X'
                btnDelete.setFont(Font.font("System", FontWeight.BOLD, 14));
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-padding: 0; -fx-cursor: hand;");

                // Efect subtil de hover: se face roz închis când pui mouse-ul pe el
                btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PINK_DARKER + "; -fx-font-weight: bold;"));
                btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;"));

                btnDelete.setOnAction(e -> {
                    service.getCartItems().remove(b);
                    mainApp.show(); // Reset la badge sidebar
                    show(); // Refresh ecran
                });

                Label price = new Label(totalItem + " RON");
                price.setFont(Font.font("System", FontWeight.BOLD, 15));
                price.setTextFill(Color.web(PINK_DARKER));

                // Adăugăm mai întâi X-ul (va fi sus) și apoi prețul (va fi jos)
                rightSideBox.getChildren().addAll(btnDelete, price);

                // Adăugăm în rând numele cărții, controalele de cantitate, spacer-ul și cutia din dreapta cu X-ul și prețul
                row.getChildren().addAll(name, qtyControls, spacer, rightSideBox);
                itemsBox.getChildren().add(row);
            }

            ScrollPane scroll = new ScrollPane(itemsBox);
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
            scroll.setPrefHeight(400);

            Label fin = new Label("TOTAL PLATĂ: " + totalFinal + " RON");
            fin.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            fin.setTextFill(Color.web(TEXT_COLOR));

            Button btnCheckout = new Button("Plasează Comanda ✨");
            btnCheckout.setMinWidth(250);
            btnCheckout.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
            btnCheckout.setOnAction(e -> clickOrder());

            this.getChildren().addAll(title, scroll, new Separator(), fin, btnCheckout);
        }

        mainApp.setCenter(this);
    }

    public void clickOrder() {
        new OrderFormWindow(service, mainApp).show();
    }

    private Button createCartBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(30, 30);
        btn.setStyle("-fx-background-color: " + PINK_LIGHT + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + PINK_LIGHT + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold; -fx-background-radius: 5;"));
        return btn;
    }
}