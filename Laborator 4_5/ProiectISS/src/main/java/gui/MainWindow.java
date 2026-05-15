package gui;

import domain.Book;
import domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import service.Service;
import java.util.List;

public class MainWindow extends BorderPane {
    private Service service;
    private User loggedUser = null;

    private final String PINK_LIGHT = "#FFB6C1", PINK_DARKER = "#DB7093", IVORY = "#FFFFF0", TEXT_COLOR = "#5D4037";

    public MainWindow(Service service) {
        this.service = service;
        this.setStyle("-fx-background-color: " + IVORY + ";");
        show();
    }

    public void show() {
        buildSidebar();
        loadData(service.findAll());
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

        Button btnCont = new Button("👤 Contul Meu");
        btnCont.setMaxWidth(Double.MAX_VALUE);
        btnCont.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 8;");
        btnCont.setOnAction(e -> clickMyAccount());

        sidebar.getChildren().addAll(logo, new Separator(), accordion, btnCont);
        this.setLeft(sidebar);
    }

    public void selectGenre(String genre) {
        List<Book> books;

        if (genre.equals("Toate")) {
           books = service.findAll();
        } else {
            books = service.filterByGenre(genre);
        }

        loadData(books);
    }

    public void loadData(List<Book> books) {
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
        loadData(service.searchBooks(title));
    }

    public void clickMyAccount() {
        if (loggedUser != null) new ClientWindow(service, this, loggedUser).show();
        else new LoginWindow(service, this).show();
    }

    private VBox createBookCard(Book b) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label t = new Label(b.getTitle()); t.setWrapText(true); t.setFont(Font.font("System", FontWeight.BOLD, 15));
        Label p = new Label(b.getPrice() + " RON"); p.setTextFill(Color.web(PINK_DARKER));
        Button add = new Button("Adaugă");
        add.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-text-fill: white;");

        card.getChildren().addAll(t, p, add);
        return card;
    }

    public void setLoggedUser(User u) { this.loggedUser = u; }
}

