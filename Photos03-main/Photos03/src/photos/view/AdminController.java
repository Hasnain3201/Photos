package photos.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import photos.model.UserDataManager;
import photos.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class AdminController {

    @FXML
    private ListView<String> userListView;
    @FXML
    private Button addUser;
    @FXML
    private Button deleteUser;

    // Initialization method
    @FXML
    public void initialize() {
        loadUserList(); // Load the user list into the ListView
        deleteUser.setDisable(true);
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            deleteUser.setDisable(newVal == null);
        });
    }


    private void loadUserList() {
        List<String> users = getUserList(); // Assume this method fetches the user list
        userListView.setItems(FXCollections.observableArrayList(users));
    }


    @FXML
    private void handleAddUser() {
        createUser();
        loadUserList();
    }


    @FXML
    private void handleDeleteUser() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Delete User", "No user selected!", Alert.AlertType.ERROR);
            return;
        }

        if ("admin".equals(selectedUser)) {
            showAlert("Delete User", "Cannot delete the admin user!", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the user: " + selectedUser + "? This will remove all their albums and photos.", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                deleteUserAndData(selectedUser);
                loadUserList();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Delete User", "Error deleting user data!", Alert.AlertType.ERROR);
            }
        }
    }

    private void deleteUserAndData(String username) throws IOException {
        // Call the method in UserDataManager to delete the user data
        UserDataManager.deleteUserData(username);
    }

    private List<String> getUserList() {
        try {
            return UserDataManager.loadAllUsernames(); // Directly call the newly created method
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list on error
        }
    }

    @FXML
    private void createUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New User");
        dialog.setHeaderText("Add a New User");
        dialog.setContentText("Please enter the username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            if (username.trim().isEmpty()) {
                showAlert("Create User", "Username cannot be empty!", Alert.AlertType.ERROR);
            } else if (userExists(username.trim())) {
                showAlert("Create User", "User already exists!", Alert.AlertType.ERROR);
            } else {
                User newUser = new User(username.trim());
                try {
                    UserDataManager.saveUserData(newUser);
                    loadUserList(); // Refresh the user list view after adding a new user
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Create User", "Error saving user data!", Alert.AlertType.ERROR);
                }
            }
        });
        loadUserList();
    }


    private boolean userExists(String username) {
        try {
            return UserDataManager.loadUserData(username) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleLogoutAction(ActionEvent event) {
        try {
            System.out.println("Admin logging out...");

            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/login1.fxml")); // Ensure the path is correct
            Parent root = loader.load();

            // Get the current stage using any component in the scene, not from the MenuItem
            Stage stage = (Stage) userListView.getScene().getWindow(); // Assuming userListView is part of the current scene

            // Set the scene to the login screen
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Show the login screen and close the current window
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (perhaps show an error alert)
        }
    }
    
}
