package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminHomePage {
    private final DatabaseHelper databaseHelper;

    public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Button to open the User List Page
        Button listUsersButton = new Button("List All Users");
        listUsersButton.setOnAction(e -> new UserListPage(databaseHelper).show(primaryStage));

        // Button to open Role Assignment Page
        Button assignRoleButton = new Button("Assign User Role");
        assignRoleButton.setOnAction(e -> new AssignRolesPage(databaseHelper).show(primaryStage));

        //////////////delete Button and action added by jace let me know if issues arise////
        Button deleteButton = new Button("Delete Users");
        deleteButton.setOnAction(a -> {
            new adminDeletePage(databaseHelper).show(primaryStage);
        });
        //////////////////////////////
        
        layout.getChildren().addAll(adminLabel, listUsersButton, assignRoleButton, deleteButton);
        Scene adminScene = new Scene(layout, 800, 400);

        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
        primaryStage.show();
    }
}
