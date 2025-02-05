package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import application.User;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
	    String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255) UNIQUE, "
	            + "password VARCHAR(255), "
	            // Added email column
	            + "email VARCHAR(255), " 
	            + "role VARCHAR(20))";
	    statement.execute(userTable);
	
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	public void updateUserEmail(String userName, String email) throws SQLException {
	    String query = "UPDATE cse360users SET email = ? WHERE userName = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, userName);
	        int rowsUpdated = pstmt.executeUpdate();

	        if (rowsUpdated == 0) {
	            throw new SQLException("No user found with the provided username.");
	        }
	    }
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	public void updateUserRole(String userName, String role) throws SQLException {
	    // Ensure only valid roles can be assigned
	    if (!role.equals("Admin") && !role.equals("Student") &&
	        !role.equals("Instructor") && !role.equals("Staff") &&
	        !role.equals("Reviewer")) {
	        throw new SQLException("Invalid role assignment.");
	    }

	    String query = "UPDATE cse360users SET role = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, role);
	        pstmt.setString(2, userName);
	        int rowsUpdated = pstmt.executeUpdate();

	        if (rowsUpdated == 0) {
	            throw new SQLException("No user found with the provided username.");
	        }
	    }
	}
	
	// Retrieves all recorded userNames
	public ArrayList<String> getAllUsernames() {
	    ArrayList<String> usernames = new ArrayList<>();
	    String query = "SELECT userName FROM cse360users";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            usernames.add(rs.getString("userName"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return usernames;
	}


	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Retrieves userName, password, and role in list form
	public ArrayList<String> getAllUsers() {
	    ArrayList<String> users = new ArrayList<>();
	    String query = "SELECT userName, password, email, role FROM cse360users"; // Added email field

	    try {
	        if (connection == null || connection.isClosed()) { // Ensure connection is initialized
	            connectToDatabase();
	        }

	        try (PreparedStatement pstmt = connection.prepareStatement(query);
	             ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {
	                String userName = rs.getString("userName");
	                String password = rs.getString("password");
	                String email = rs.getString("email");  // Fetching email
	                String role = rs.getString("role");

	                // Format user details into a single string
	                String userDetails = "Username: " + userName + 
	                                     " | Password: " + password + 
	                                     " | Email: " + email + 
	                                     " | Role: " + role;
	                users.add(userDetails);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	/////////////deleteUser() added by jace if any issues let me know////////////
	public String deleteUser(String username)
	{
		String errorMessage = "";
        
		if(!doesUserExist(username))
		{
			return "Username not associated with any known account.";
		}
		
		if(getUserRole(username).equals("admin"))
		{
			return "Admins cannot delete their own account.";
		}
		
		
		int userID = -1;
		
		String query = "SELECT id FROM cse360users WHERE userName = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query))
		{
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				userID = rs.getInt("id");
			} else {
				throw new SQLException("User not found");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return "ERROR";
		}
	
		String deleteQuery = "DELETE FROM cse360users WHERE id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(deleteQuery))
		{
			pstmt.setInt(1, userID);
			int changed = pstmt.executeUpdate();
			
			if(changed > 0)
			{
				return "";
			}else {
				return "Failed to delete user.";
			}
		}catch (SQLException e )
		{
			e.printStackTrace();
			return "ERROR";
		}
	}
	/////////////////////////////////////////////////////////////////////////////
	
	
	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
