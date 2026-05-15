package gui;

import domain.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import service.Service;

public class ClientWindow extends VBox {
    private Service service;
    private MainWindow mainApp;
    private User user;

    public ClientWindow(Service service, MainWindow mainApp, User user) {
        this.service = service;
        this.mainApp = mainApp;
        this.user = user;
    }

    public void show() {
        this.setPadding(new Insets(30));
        this.setSpacing(20);

        Label titlu = new Label("Profilul Meu");
        titlu.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #5D4037;");

        VBox info = new VBox(10);
        info.getChildren().addAll(
                new Label("👤 Nume: " + user.getUsername()),
                new Label("📧 Email: " + user.getEmail())
        );

        Button logout = new Button("Logout");
        logout.setStyle("-fx-background-color: #DB7093; -fx-text-fill: white;");
        logout.setOnAction(e -> { mainApp.setLoggedUser(null); mainApp.show(); });

        this.getChildren().addAll(titlu, new Separator(), info, logout);
        mainApp.setCenter(this);
    }
}

