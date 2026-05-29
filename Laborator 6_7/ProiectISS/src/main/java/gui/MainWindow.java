package gui;

import domain.Book;
import domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import service.Service;
import java.util.List;
import java.util.Map;

public class MainWindow extends BorderPane {
    private Service service;
    private User loggedUser = null;

    // Păstrăm sfintele tale culori
    private final String PINK_LIGHT = "#FFB6C1", PINK_DARKER = "#DB7093", IVORY = "#FFFFF0", TEXT_COLOR = "#5D4037";

    // Păstrăm o referință la ultima listă de cărți afișată ca să putem da refresh instant la click pe [+] sau [-]
    private List<Book> currentDisplayedBooks;

    // Referință la containerul de la badge pentru a-i face update la număr rapid
    private StackPane cartBadgeContainer;

    public MainWindow(Service service) {
        this.service = service;
        this.setStyle("-fx-background-color: " + IVORY + ";");
        show();
    }

    public void show() {
        buildSidebar();
        // Salvăm lista inițială și o încărcăm
        this.currentDisplayedBooks = service.findAll();
        loadData(this.currentDisplayedBooks);
    }

    private void buildSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: " + PINK_LIGHT + ";");
        sidebar.setPrefWidth(220);
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label logo = new Label("BOOKSTORE");
        logo.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        logo.setTextFill(Color.web(TEXT_COLOR));

        Accordion accordion = new Accordion();
        VBox genuriBox = new VBox(2);
        genuriBox.setStyle("-fx-background-color: " + PINK_LIGHT + ";");

        String[] genuri = {"Toate", "Romance", "Horror", "Autobiografie", "Istorie", "SF", "Thriller", "Psihologie", "Religie"};
        for (String g : genuri) {
            Button gb = new Button(g);
            gb.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_COLOR + "; -fx-cursor: hand;");
            gb.setMaxWidth(Double.MAX_VALUE);
            gb.setAlignment(Pos.CENTER_LEFT);
            gb.setOnAction(e -> selectGenre(g)); // Metoda din UML
            genuriBox.getChildren().add(gb);
        }

        TitledPane catPane = new TitledPane("📚 Categorii", genuriBox);
        catPane.setStyle("-fx-base: " + PINK_DARKER + "; -fx-text-fill: white; -fx-font-weight: bold;");
        accordion.getPanes().add(catPane);

        // --- BUTONUL DE COȘ STILIZAT CU CERCULEȚ ROȘU (BADGE) ÎN INTERIOR ---
        cartBadgeContainer = new StackPane();
        cartBadgeContainer.setMaxWidth(Double.MAX_VALUE);
        updateCartBadge(); // Desenăm prima dată badge-ul conform stării coșului

        Button btnCont = new Button("👤 Contul Meu");
        btnCont.setMaxWidth(Double.MAX_VALUE);
        btnCont.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 8;");
        btnCont.setOnAction(e -> clickMyAccount());

        sidebar.getChildren().addAll(logo, new Separator(), accordion, cartBadgeContainer, btnCont);
        this.setLeft(sidebar);
    }

    /**
     * Metodă care desenează sau actualizează cerculețul roșu de notificări din interiorul butonului de coș
     */
    public void updateCartBadge() {
        cartBadgeContainer.getChildren().clear();

        HBox buttonContent = new HBox(8);
        buttonContent.setAlignment(Pos.CENTER);

        Label lblText = new Label("🛒 Coș");
        lblText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        buttonContent.getChildren().add(lblText);

        int totalItems = service.getCartTotalItems();
        if (totalItems > 0) {
            Circle circle = new Circle(9, Color.RED);
            Label lblCount = new Label(String.valueOf(totalItems));
            lblCount.setTextFill(Color.WHITE);
            lblCount.setFont(Font.font("System", FontWeight.BOLD, 10));

            StackPane badge = new StackPane(circle, lblCount);
            badge.setMouseTransparent(true);
            StackPane.setAlignment(badge, Pos.CENTER_RIGHT);
            StackPane.setMargin(badge, new Insets(0, -5, 0, 0)); // Îl distanțăm puțin de marginea din dreapta

            buttonContent.getChildren().add(badge);
        }

        Button btnCos = new Button();
        btnCos.setGraphic(buttonContent);
        btnCos.setMaxWidth(Double.MAX_VALUE);
        btnCos.setAlignment(Pos.CENTER);
        btnCos.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-padding: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        btnCos.setOnAction(e -> clickCart());

        cartBadgeContainer.getChildren().add(btnCos);
    }

    public void clickCart(){
        new CartWindow(service, this).show();
    }

    public void selectGenre(String genre) {
        if (genre.equals("Toate")) {
            currentDisplayedBooks = service.findAll();
        } else {
            currentDisplayedBooks = service.filterByGenre(genre);
        }
        loadData(currentDisplayedBooks);
    }

    public void loadData(List<Book> books) {
        this.currentDisplayedBooks = books;

        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        // Titlul dinamic
        Label title = new Label("Catalog Cărți");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#5D4037")); // TEXT_COLOR

        // --- BARA DE CĂUTARE (Top) ---
        HBox searchBar = new HBox(10);
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Caută titlu...");
        Button sBtn = new Button("Caută");
        sBtn.setStyle("-fx-background-color: #DB7093; -fx-text-fill: white;");
        sBtn.setOnAction(e -> clickSearch(txtSearch.getText()));
        searchBar.getChildren().addAll(txtSearch, sBtn);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #FFFFF0; -fx-border-color: #FFFFF0;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(10));

        int col = 0, row = 0;
        for (Book b : books) {
            grid.add(createBookCard(b), col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

        scroll.setContent(grid);
        container.getChildren().addAll(title, searchBar, scroll);

        this.setCenter(container);
    }

    public void clickSearch(String title) {
        currentDisplayedBooks = service.searchBooks(title);
        loadData(currentDisplayedBooks);
    }

    public void clickMyAccount() {
        if (loggedUser != null) {
           new ClientWindow(service, this, loggedUser).show();
        }
        else new LoginWindow(service, this).show();
    }

    private VBox createBookCard(Book b) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(180);
        card.setPrefHeight(200); // Înălțime fixă ca să rămână aliniate
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label t = new Label(b.getTitle()); t.setWrapText(true); t.setFont(Font.font("System", FontWeight.BOLD, 15));
        t.setAlignment(Pos.CENTER); t.setTextAlignment(TextAlignment.CENTER);

        Label p = new Label(b.getPrice() + " RON"); p.setTextFill(Color.web(PINK_DARKER));

        card.getChildren().addAll(t, p);

        // --- LOGICA DE STOC (URGENTĂ / EPUIZAT) ȘI BUTOANE ---
        if (b.getQuantity() == 0) {
            // VARIANCE B: Stoc epuizat complet -> Blocăm tot pe catalog
            Label lblEpuizat = new Label("Stoc epuizat ❌");
            lblEpuizat.setFont(Font.font("System", FontPosture.ITALIC, 11));
            lblEpuizat.setTextFill(Color.RED);

            Button addDisabled = new Button("Adaugă");
            addDisabled.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-background-radius: 5;");
            addDisabled.setDisable(true); // Buton înghețat

            card.getChildren().addAll(lblEpuizat, addDisabled);
        } else {
            // Dacă stocul e mai mic de 5 (dar mai mare de 0), punem mesajul de marketing
            if (b.getQuantity() < 5) {
                Label lblUrgente = new Label("Grăbiți-vă! Ultimele bucăți!");
                lblUrgente.setFont(Font.font("System", FontPosture.ITALIC, 11));
                lblUrgente.setTextFill(Color.RED);
                card.getChildren().add(lblUrgente);
            }

            // Logica normală pentru butoane dacă avem stoc
            Map<Book, Integer> cartItems = service.getCartItems();

            if (cartItems.containsKey(b)) {
                int qtyInCart = cartItems.get(b);
                HBox qtyBox = new HBox(10);
                qtyBox.setAlignment(Pos.CENTER);

                Button minusBtn = createCardCartBtn("-");
                minusBtn.setOnAction(e -> { service.removeFromCart(b); refreshCatalogAndSidebar(); });

                Label lblQty = new Label(String.valueOf(qtyInCart));
                lblQty.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblQty.setTextFill(Color.web(TEXT_COLOR));
                lblQty.setMinWidth(20);
                lblQty.setAlignment(Pos.CENTER);

                Button plusBtn = createCardCartBtn("+");
                if (qtyInCart >= b.getQuantity()) {
                    plusBtn.setDisable(true);
                    plusBtn.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-background-radius: 5;"); // Îl facem gri
                }
                plusBtn.setOnAction(e -> { service.addToCart(b); refreshCatalogAndSidebar(); });

                qtyBox.getChildren().addAll(minusBtn, lblQty, plusBtn);
                card.getChildren().add(qtyBox);
            } else {
                Button add = new Button("Adaugă");
                add.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                add.setOnAction(e -> { service.addToCart(b); refreshCatalogAndSidebar(); });
                card.getChildren().add(add);
            }
        }

        return card;
    }

    /**
     * Generator de butonașe mici pentru carduri, fix cu efectul de hover roz pe care l-ai definit
     */
    private Button createCardCartBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(25, 25);
        btn.setStyle("-fx-background-color: " + PINK_LIGHT + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + PINK_LIGHT + "; -fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold; -fx-background-radius: 5;"));
        return btn;
    }

    /**
     * Actualizează rapid bara din stânga (badge-ul) și catalogul curent, fără timpi mari de așteptare
     */
    private void refreshCatalogAndSidebar() {
        updateCartBadge();
        loadData(currentDisplayedBooks);
    }

    public void setLoggedUser(User u) {
        this.loggedUser = u;
        this.currentDisplayedBooks = service.findAll();
        loadData(this.currentDisplayedBooks);
        updateCartBadge();
    }
}

