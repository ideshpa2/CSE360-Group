/*added by jace please let me know if any issues arise*/

package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


 

public class adminDeletePage 
{
    private final DatabaseHelper databaseHelper;
	
   
    public adminDeletePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	
    public void show(Stage primaryStage) 
    {
    	Label errorLabel = new Label();
	errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
		
  	Label userDeleted = new Label();
  	userDeleted.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
		
    	VBox layout = new VBox();
	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the admin delete page
	Label adminLabel = new Label("Admin delete page");
	adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	layout.getChildren().add(adminLabel);

	TextField userNameToDelete = new TextField();
        userNameToDelete.setPromptText("Enter username of user to delete");
        userNameToDelete.setMaxWidth(250);
        
        Button deleteButton = new Button("Delete");
	Button confirmDelete = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this user?");
	    
        deleteButton.setOnAction(a -> {
		//resource I used to learn this https://stackoverflow.com/questions/44101426/javafx-alert-box-on-button-click
		Optional<ButtonType> result = confirmDelete.showAndWait();
		if(result.get() == ButtonType.OK)
		{
	        	String userName = userNameToDelete.getText();
	        	String error = databaseHelper.deleteUser(userName);
	      	    	if(!error.equals(""))
	      	    	{
	      			errorLabel.setText(error);
	      		}else {
	      			System.out.println("User " + userName + " deleted successfully.");
	      			userDeleted.setText("User " + userName + " deleted successfully.");
	      			new AdminHomePage(databaseHelper).show(primaryStage);
	      		}
		}
        
        });
        
        layout.getChildren().addAll(userNameToDelete,deleteButton,errorLabel,userDeleted);
        Scene adminScene = new Scene(layout,800,400);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Delete Page");
        
    }
}
