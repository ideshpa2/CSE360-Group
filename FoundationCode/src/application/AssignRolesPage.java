package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class AssignRolesPage {
    private final DatabaseHelper databaseHelper;

    public AssignRolesPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("Assign User Role");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Dropdown to select user
        ComboBox<String> userDropdown = new ComboBox<>();
        ArrayList<String> users = databaseHelper.getAllUsernames();
        userDropdown.getItems().addAll(users);

        // Dropdown to select role
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("Admin", "Student", "Instructor", "Staff", "Reviewer");

        // Button to assign role
        Button assignRoleButton = new Button("Assign Role");
        Label resultLabel = new Label();

        assignRoleButton.setOnAction(e -> {
            String selectedUser = userDropdown.getValue();
            String selectedRole = roleDropdown.getValue();

            if (selectedUser == null || selectedRole == null) {
                resultLabel.setText("Please select a user and a role.");
                return;
            }

            try {
                databaseHelper.updateUserRole(selectedUser, selectedRole);
                resultLabel.setText("Role assigned successfully!");
            } catch (SQLException ex) {
                resultLabel.setText("Error assigning role.");
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(titleLabel, userDropdown, roleDropdown, assignRoleButton, resultLabel);
        Scene assignRoleScene = new Scene(layout, 800, 400);
        primaryStage.setScene(assignRoleScene);
        primaryStage.setTitle("Assign User Role");
    }
}
