package gui;

import domain.User;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import service.Service;

public class LoginWindow extends VBox {
    private Service service;
    private MainWindow mainApp;
    private TextField emailIn = new TextField();
    private PasswordField passIn = new PasswordField();

    public LoginWindow(Service service, MainWindow mainApp) {
        this.service = service;
        this.mainApp = mainApp;
    }

    public void show() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setStyle("-fx-padding: 50;");

        Label t = new Label("LOGARE");
        t.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #5D4037;");

        emailIn.setPromptText("Email"); emailIn.setMaxWidth(250);
        passIn.setPromptText("Parola"); passIn.setMaxWidth(250);

        Button btn = new Button("Intră în cont");
        btn.setStyle("-fx-background-color: #DB7093; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setOnAction(e -> clickLogin());

        this.getChildren().addAll(t, emailIn, passIn, btn);
        mainApp.setCenter(this);
    }

    public void clickLogin() {
        try {
            User u = service.handleLogin(emailIn.getText(), passIn.getText());
            if (u != null) {
                mainApp.setLoggedUser(u);
                hide();
                new ClientWindow(service, mainApp, u).show();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void hide() { this.getChildren().clear(); }
}
