package gui;

import domain.Book;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.Service;

import java.util.List;

public class OrderSuccessWindow {
    private Service service;
    private MainWindow mainWindow;
    Stage stage;

    private final String PINK_DARKER = "#DB7093", TEXT_COLOR = "#5D4037";

    public OrderSuccessWindow(Service service, MainWindow mainWindow) {
        this.service = service;
        this.mainWindow = mainWindow;
    }

    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Comandă Finalizată!");

        VBox view = new VBox(20);
        view.setPadding(new Insets(35));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #fff0f5;");

        Label iconLabel = new Label("🎉");
        iconLabel.setFont(Font.font("System", 55));

        Label msgTitle = new Label("Comandă plasată cu succes!");
        msgTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        msgTitle.setTextFill(Color.web(TEXT_COLOR));

        Label msgSub = new Label("Îți mulțumim pentru cumpărături! Cărțile pornesc spre tine.");
        msgSub.setFont(Font.font("System", 13));
        msgSub.setTextFill(Color.web(TEXT_COLOR));
        msgSub.setWrapText(true);
        msgSub.setAlignment(Pos.CENTER);

        // Butonul roz drăguț cerut de tine
        Button btnHome = new Button("Mergi la pagina principală");
        btnHome.setStyle("-fx-background-color: " + PINK_DARKER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13; -fx-padding: 12 25; -fx-background-radius: 20; -fx-cursor: hand;");

        btnHome.setOnAction(e -> clickHome());

        view.getChildren().addAll(iconLabel, msgTitle, msgSub, btnHome);

        Scene scene = new Scene(view, 450, 320);
        stage.setScene(scene);
        stage.show();
    }

    public void clickHome() {
        stage.close();
        List<Book> books = service.findAll();
        mainWindow.loadData(books);
        mainWindow.selectGenre("Toate");
    }
}