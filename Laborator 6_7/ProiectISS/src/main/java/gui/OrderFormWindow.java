package gui;

import domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.Service;

public class OrderFormWindow {
    private Service service;
    private MainWindow mainWindow;
    private final String TEXT_COLOR = "#5D4037";

    TextField numeIn = new TextField();
    TextField telIn = new TextField();
    TextField emailIn = new TextField();
    TextField adresaIn = new TextField();
    Stage stage;


    public OrderFormWindow(Service service, MainWindow mainWindow) {
        this.service = service;
        this.mainWindow = mainWindow;
    }

    public void show() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Blochează ferestrele din spate până se completează
        stage.setTitle("Date de Livrare");

        VBox form = new VBox(12);
        form.setPadding(new Insets(30));
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-background-color: #fff0f5;"); // Culoarea ta IVORY

        Label t = new Label("Date de Livrare");
        t.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        t.setTextFill(Color.web(TEXT_COLOR));

        numeIn.setPromptText("Nume complet");
        telIn.setPromptText("Număr de telefon");
        emailIn.setPromptText("Email");
        adresaIn.setPromptText("Adresa de livrare");


        // Precompletare automată din obiectul User real dacă este logat cineva
        User loggedUser = service.getLoggedUser();
        if (loggedUser != null) {
            numeIn.setText(loggedUser.getUsername());
            telIn.setText(loggedUser.getTelephone());
            emailIn.setText(loggedUser.getEmail());
            adresaIn.setText("Cluj-Napoca, Str. Observatorului");
        }

        numeIn.setMaxWidth(350); telIn.setMaxWidth(350); emailIn.setMaxWidth(350); adresaIn.setMaxWidth(350);

        // Calculăm suma totală din elementele coșului pentru afișare pe buton
        double suma = service.getCartItems().entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();

        Button confirm = new Button("Confirmă Comanda (" + suma + " RON)");
        confirm.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");

        confirm.setOnAction(e -> clickConfirm());

        Button back = new Button("Înapoi");
        back.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 5; -fx-cursor: hand;");
        back.setOnAction(e -> stage.close());

        form.getChildren().addAll(t, numeIn, telIn, emailIn, adresaIn, confirm, back);

        Scene scene = new Scene(form, 450, 400);
        stage.setScene(scene);
        stage.show();
    }

    public void clickConfirm() {
        if (numeIn.getText().isEmpty() || telIn.getText().isEmpty() || emailIn.getText().isEmpty() || adresaIn.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Te rog completează toate câmpurile pentru livrare!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            // Trimitem datele culese spre salvat în baza de date
            service.placeOrder(numeIn.getText(), telIn.getText(), emailIn.getText(), adresaIn.getText());
            mainWindow.updateCartBadge();
            stage.close(); // Închidem formularul curent

            new OrderSuccessWindow(service, mainWindow).show();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }
}