package photos.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos.app.Photos;
import photos.model.User;
import photos.model.UserDataManager;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private void handleLoginAction() {
        String username = usernameField.getText().trim();

        if(username.isEmpty()) {
            System.out.println("Please enter a username.");
            return;
        }

        try {
            if (username.equalsIgnoreCase("admin")) {
                // Load the admin dashboard
                loadAdminPage();
            } else if (UserDataManager.isValidUser(username)) {
                User loggedInUser = UserDataManager.loadUserData(username);
                
                if (loggedInUser != null) {
                    System.out.println("Login successful for user: " + username);
                    Photos.showMainScreen(loggedInUser);
                } else {
                    System.out.println("Login failed to load user data for: " + username);
                }
            } else {
                System.out.println("Login failed for user: " + username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAdminPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/admin.fxml")); // Ensure this is the correct path to your FXML file
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
