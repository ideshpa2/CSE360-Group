package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

import databasePart1.DatabaseHelper;

public class UserListPage {
	private final DatabaseHelper databaseHelper;

    public UserListPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox userLayout = new VBox(10);
        userLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("User List");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Container to display users
        VBox userDisplayContainer = new VBox(5);

        // Fetch users and display as labels
        ArrayList<String> users = databaseHelper.getAllUsers();
        for (String user : users) {
            userDisplayContainer.getChildren().add(new Label(user));
        }

        // "Back" button to return to Admin Page
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new AdminHomePage(databaseHelper).show(primaryStage)); // Return to Admin Page

        userLayout.getChildren().addAll(titleLabel, userDisplayContainer, backButton);
        Scene userScene = new Scene(userLayout, 800, 400);
        
        primaryStage.setScene(userScene);
    }
}
