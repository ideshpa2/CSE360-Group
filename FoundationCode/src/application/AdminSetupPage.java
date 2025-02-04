package application;

import javafx.scene.Scene; 
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        Label userNameErrorLabel = new Label();
        
        Label passwordErrorLabel = new Label();

        passwordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        userNameErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            
            String userNameError = UserNameRecognizer.checkForValidUserName(userName);
            
            String passwordError = PasswordEvaluator.evaluatePassword(password);

            boolean hasErrors = false;

            // Handle username validation
            if (!userNameError.isEmpty()) {
                userNameErrorLabel.setText("Username Error: " + userNameError);
                hasErrors = true;
            } else {
                userNameErrorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                userNameErrorLabel.setText("Username is valid.");
            }

            // Handle password validation
            if (!passwordError.isEmpty()) {
                passwordErrorLabel.setText("Password Error: " + passwordError);
                hasErrors = true;
            } else {
                passwordErrorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                passwordErrorLabel.setText("Password is valid.");
            }

            // Stop further processing if there are errors
            if (hasErrors) {
                return;
            }

          
            try {
            	// Create a new User object with admin role and register in the database
            	User user=new User(userName, password, "admin");
                databaseHelper.register(user);
                System.out.println("Administrator setup completed.");
                
                // Navigate to the Welcome Login Page
                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, setupButton, userNameErrorLabel, passwordErrorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
