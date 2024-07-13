package photos.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.control.ChoiceDialog;
import photos.model.Album;
import photos.model.Photo;
import photos.model.Tag;
import photos.model.User;
import photos.model.UserDataManager;
import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import photos.app.Photos;
import photos.model.AdvancedSearchParameters;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.text.Font;


public class PhotosController {

    // Menu items
    @FXML private MenuItem logout;
    @FXML private MenuItem exit;
    @FXML private MenuItem importPhoto;
    @FXML private MenuItem addAlbum;
    @FXML private MenuItem renameAlbum;
    @FXML private MenuItem deleteAlbum;
    @FXML private MenuItem editPhoto;
    @FXML private MenuItem copyPhotoToAlbum;
    @FXML private MenuItem movePhotoToAlbum;
    @FXML private MenuItem openAlbum;
    @FXML private MenuItem addPhotoToAlbum;
    @FXML private MenuItem removePhotoFromAlbum;
    @FXML private MenuItem listUsers;
    @FXML private MenuItem createUser;
    @FXML private MenuItem deleteUser;

    // Search bar and buttons
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private Button advancedSearchButton;

    // This will store the current user
    private User currentUser;

    // Album and photo views
    @FXML private ListView<String> listView;
    @FXML private TilePane tilePane; 

    @FXML
    private Label photoCaption;

    private List<Photo> searchResults = new ArrayList<>();
    @FXML
    private Button compileResultsButton;

    @FXML private Button searchByTagPairs;
    @FXML private Button searchByDates;

    @FXML private TextFlow textFlowFieldEarly;
    @FXML private TextFlow textFlowFieldLate;



    public void initialize() {
    
        searchByTagPairs.setOnAction(this::onSearchByTagPairs);
        searchByDates.setOnAction(this::onSearchByDates);
        searchButton.setOnAction(this::onSearch); // Assuming you have or will create an onSearchByTag method
    
        // If compileResultsButton is managed in FXML, don't create a new instance in the code
        // If it's managed in code, ensure it's added to a layout container like tilePane
        if (compileResultsButton != null) {
            compileResultsButton.setOnAction(this::compileResultsIntoAlbum);
            compileResultsButton.setVisible(false); // Initially hidden
        }
    }
    
    

    public void userLoggedIn(User user) {
        this.currentUser = user; // Set the current user
        
        if (currentUser.getUsername().equalsIgnoreCase(Photos.getStockUser())) {
            Photos.initializeStockPhotos(user); // Initialize stock photos only for the stock user
        }

        loadAlbumsForUser(); // Load the albums for this user
        setupAlbumSelectionListener(); // Set up the listener for album selection
    }

    // Load albums for the current user
    private void loadAlbumsForUser() {
        System.out.println("Loading albums for user: " + currentUser.getUsername());
        for (Album album : currentUser.getAlbumsList()) {
            System.out.println("Album: " + album.getName() + " with " + album.getPhotos().size() + " photos");
        }

        if(currentUser != null) {
            List<String> albumNames = currentUser.getAlbumsList().stream()
                                                 .map(Album::getName)
                                                 .collect(Collectors.toList());
            System.out.println("Loading albums for user: " + currentUser.getUsername());
            albumNames.forEach(albumName -> System.out.println("Album: " + albumName + " with " + currentUser.getAlbum(albumName).getPhotos().size() + " photos"));
            listView.setItems(FXCollections.observableArrayList(albumNames));
        }
    }

    // Set up listener for album selection in the ListView
    private void setupAlbumSelectionListener() {
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clearSearchResults();
                displayPhotosForAlbum(newSelection);
            }
        });
    }

    private void clearSearchResults() {
        if (!searchResults.isEmpty()) {
            searchResults.clear();
            if (compileResultsButton != null) {
                compileResultsButton.setVisible(false); // Hide the button when leaving search results
            }
            tilePane.getChildren().clear(); // Clear the photo display area
        }
    }

    private Photo selectedPhoto; // Track the selected photo
    private Label itemCountLabel = new Label();


    private void displayPhotosForAlbum(String albumName) {
        clearSearchResults();
        Album selectedAlbum = currentUser.getAlbum(albumName);
        if (selectedAlbum == null) {
            showAlert("Album Not Found", "The selected album was not found.", Alert.AlertType.ERROR);
            return;
        }
    
        List<Photo> photos = selectedAlbum.getPhotos();
        itemCountLabel.setText("Items in Album: " + photos.size());
    
        LocalDateTime earliestDate = photos.stream()
                                           .map(Photo::getLastModified)
                                           .min(LocalDateTime::compareTo)
                                           .orElse(null);
    
        LocalDateTime latestDate = photos.stream()
                                         .map(Photo::getLastModified)
                                         .max(LocalDateTime::compareTo)
                                         .orElse(null);
    
        // Create a container for the item count and date labels
        VBox infoBox = new VBox(5);
        Label dateInfoLabel = new Label();
        if (earliestDate != null && latestDate != null) {
            dateInfoLabel.setText(String.format("Earliest: %s, Latest: %s",
                    earliestDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    latestDate.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        } else {
            dateInfoLabel.setText("No date information available");
        }
        infoBox.getChildren().addAll(itemCountLabel, dateInfoLabel);
    
        // Clear previous children and add the info box at the top
        tilePane.getChildren().clear();
        tilePane.getChildren().add(infoBox);
    
        for (Photo photo : photos) {
            VBox photoBox = new VBox(5);
            ImageView imageView = new ImageView(new Image(new File(photo.getPath()).toURI().toString()));
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            Label captionLabel = new Label(photo.getCaption());
            photoBox.getChildren().addAll(imageView, captionLabel);
    
            photoBox.setOnMouseClicked(event -> {
                selectedPhoto = photo;
    
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    openPhotoViewer(photos, photos.indexOf(photo));
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    showContextMenu(event, imageView);
                }
            });
    
            tilePane.getChildren().add(photoBox);
        }
    }
    
    
    

    // Method to find a photo by path across all albums
private Photo findPhotoByPath(String path) {
    for (Album album : currentUser.getAlbumsList()) {
        for (Photo photo : album.getPhotos()) {
            if (photo.getPath().equals(path)) {
                return photo;
            }
        }
    }
    return null; // Return null if the photo is not found in any album
}

@FXML
private void onAddPhotoToAlbum() {
    String selectedAlbumName = listView.getSelectionModel().getSelectedItem();
    Album selectedAlbum = selectedAlbumName == null ? null : currentUser.getAlbum(selectedAlbumName);
    if (selectedAlbum == null) {
        showAlert("No Album Selected", "Please select an album first.", Alert.AlertType.WARNING);
        return;
    }

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Photo");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
    File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile == null) return;

    String filePath = selectedFile.getAbsolutePath();
    Photo existingPhoto = findPhotoByPath(filePath);
    if (existingPhoto != null && selectedAlbum.getPhotos().contains(existingPhoto)) {
        showAlert("Duplicate Photo", "This photo already exists in the album.", Alert.AlertType.WARNING);
    } else {
        Photo newPhoto = existingPhoto != null ? existingPhoto : new Photo(selectedFile.getName(), filePath);
        selectedAlbum.addPhoto(newPhoto);
        saveUserData();
        displayPhotosForAlbum(selectedAlbumName);
    }
}

@FXML
public void onCopyPhotoToAnotherAlbum() {
    if (selectedPhoto == null) {
        showAlert("Copy Photo", "No photo selected to copy.", Alert.AlertType.ERROR);
        return;
    }

    List<String> albumNames = currentUser.getAlbumsList().stream().map(Album::getName).collect(Collectors.toList());
    ChoiceDialog<String> dialog = new ChoiceDialog<>(null, albumNames);
    dialog.setTitle("Copy Photo");
    dialog.setHeaderText("Select the target album to copy the photo to:");
    dialog.setContentText("Choose album:");
    dialog.showAndWait().ifPresent(targetAlbumName -> {
        Album targetAlbum = currentUser.getAlbum(targetAlbumName);
        if (targetAlbum != null && !targetAlbum.getPhotos().contains(selectedPhoto)) {
            targetAlbum.addPhoto(selectedPhoto);
            saveUserData();
            displayPhotosForAlbum(targetAlbumName);
        } else {
            showAlert("Copy Photo", "Photo already exists in the target album or album not found.", Alert.AlertType.ERROR);
        }
    });
}

@FXML
public void onMovePhotoToAnotherAlbum() {
    if (selectedPhoto == null) {
        showAlert("Move Photo", "No photo selected to move.", Alert.AlertType.ERROR);
        return;
    }

    String sourceAlbumName = listView.getSelectionModel().getSelectedItem();
    Album sourceAlbum = currentUser.getAlbum(sourceAlbumName);
    if (sourceAlbum == null) {
        showAlert("Move Photo", "Source album not found.", Alert.AlertType.ERROR);
        return;
    }

    List<String> albumNames = currentUser.getAlbumsList().stream().map(Album::getName).collect(Collectors.toList());
    albumNames.remove(sourceAlbumName);
    ChoiceDialog<String> dialog = new ChoiceDialog<>(null, albumNames);
    dialog.setTitle("Move Photo");
    dialog.setHeaderText("Select the target album to move the photo to:");
    dialog.setContentText("Choose album:");
    dialog.showAndWait().ifPresent(targetAlbumName -> {
        Album targetAlbum = currentUser.getAlbum(targetAlbumName);
        if (targetAlbum != null && !targetAlbum.getPhotos().contains(selectedPhoto)) {
            sourceAlbum.getPhotos().remove(selectedPhoto);
            targetAlbum.addPhoto(selectedPhoto);
            saveUserData();
            displayPhotosForAlbum(sourceAlbumName);
        } else {
            showAlert("Move Photo", "Photo already exists in the target album or album not found.", Alert.AlertType.ERROR);
        }
    });
}
    
    private void showContextMenu(MouseEvent event, ImageView imageView) {
        ContextMenu contextMenu = new ContextMenu();
    
        MenuItem addEditCaptionItem = new MenuItem("Add/Edit Caption");
        addEditCaptionItem.setOnAction(e -> handleAddCaptionAction());
    
        MenuItem addTagItem = new MenuItem("Add Tag");
        addTagItem.setOnAction(e -> handleAddTagAction());
    
        MenuItem deleteTagItem = new MenuItem("Delete Tag");
        deleteTagItem.setOnAction(e -> handleDeleteTagAction());
    
        contextMenu.getItems().addAll(addEditCaptionItem, addTagItem, deleteTagItem);
        contextMenu.show(imageView, event.getScreenX(), event.getScreenY());
    }

    @FXML
    private void handleAddCaptionAction() {
        if (selectedPhoto == null) {
            showAlert("No Photo Selected", "Please select a photo to add a caption.", Alert.AlertType.INFORMATION);
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedPhoto.getCaption());
        dialog.setTitle("Add Caption");
        dialog.setHeaderText("Add a caption for the photo:");
        dialog.setContentText("Caption:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(caption -> {
            selectedPhoto.setCaption(caption);
            displayPhotosForAlbum(listView.getSelectionModel().getSelectedItem());
            saveUserData();
        });
    }

    private void openPhotoViewer(List<Photo> photos, int startIndex) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/PhotoViewer.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Photo Viewer");

            PhotoViewerController controller = loader.getController();
            controller.setPhotos(photos, startIndex);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRemovePhotoFromAlbum() {
        if (selectedPhoto == null) {
            System.out.println("No photo selected!");
            return;
        }

        String selectedAlbumName = listView.getSelectionModel().getSelectedItem();
        Album selectedAlbum = currentUser.getAlbum(selectedAlbumName);

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove the selected photo?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                selectedAlbum.getPhotos().remove(selectedPhoto);
                displayPhotosForAlbum(selectedAlbumName); // Refresh the TilePane

                try {
                    UserDataManager.saveUserData(currentUser); // Save the updated user data
                } catch (IOException e) {
                    e.printStackTrace();
                }

                selectedPhoto = null; // Reset the selected photo
                System.out.println("Photo removed from album");
            }
        });
    }

    @FXML
    private void handleRenameAlbumAction() {
        String selectedAlbumName = listView.getSelectionModel().getSelectedItem();
        if (selectedAlbumName == null || selectedAlbumName.trim().isEmpty()) {
            showAlert("Rename Album", "No album selected to rename.", Alert.AlertType.ERROR);
            return;
        }

        Album selectedAlbum = currentUser.getAlbum(selectedAlbumName);
        if (selectedAlbum == null) {
            showAlert("Rename Album", "Selected album not found.", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog renameDialog = new TextInputDialog(selectedAlbumName);
        renameDialog.setTitle("Rename Album");
        renameDialog.setHeaderText("Enter the new name for the album:");
        Optional<String> result = renameDialog.showAndWait();

        result.ifPresent(newName -> {
            if (newName.trim().isEmpty() || newName.trim().equals(selectedAlbumName)) {
                showAlert("Rename Album", "Invalid or unchanged name.", Alert.AlertType.WARNING);
                return;
            }

            if (currentUser.getAlbum(newName.trim()) != null) {
                showAlert("Rename Album", "An album with this name already exists.", Alert.AlertType.ERROR);
                return;
            }

            // Update the album name
            currentUser.renameAlbum(selectedAlbumName, newName.trim());
            loadAlbumsForUser(); // Refresh the album list
            saveUserData(); // Save the updated user data
        });
    }

    @FXML
    private void handleAddAlbumAction() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Album");
        dialog.setHeaderText("Create a New Album");
        dialog.setContentText("Enter album name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(albumName -> {
            if (!albumName.trim().isEmpty()) {
                currentUser.addAlbum(albumName.trim());
                loadAlbumsForUser(); // Refresh the album list
                saveUserData(); // Save the updated user data
            }
        });
    }

    @FXML
    public void onDeleteAlbumAction() {
        String selectedAlbum = listView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            showAlert("Delete Album", "No album selected!", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the album: " + selectedAlbum + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            currentUser.removeAlbum(selectedAlbum);
            loadAlbumsForUser(); // Refresh the album list view
            tilePane.getChildren().clear(); // Clear the photo view
            saveUserData(); // Save changes
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void saveUserData() {
        try {
            UserDataManager.saveUserData(currentUser);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (maybe show an error dialog)
        }
    }

    @FXML
    private void handleAddTagAction() {
        if (selectedPhoto == null) {
            showAlert("No Photo Selected", "Please select a photo to add a tag to.", Alert.AlertType.INFORMATION);
            return;
        }

        // Show dialog to add tag
        Dialog<Tag> dialog = createTagDialog();
        Optional<Tag> result = dialog.showAndWait();

        result.ifPresent(tag -> {
            selectedPhoto.addTag(tag);
            showAlert("Tag Added", "Tag added to photo successfully.", Alert.AlertType.INFORMATION);
            // Save changes, update view, etc.
            saveUserData();
        });
    }

    private Dialog<Tag> createTagDialog() {
        Dialog<Tag> dialog = new Dialog<>();
        dialog.setTitle("Add Tag");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField tagNameField = new TextField();
        TextField tagValueField = new TextField();

        grid.add(new Label("Tag Name:"), 0, 0);
        grid.add(tagNameField, 1, 0);
        grid.add(new Label("Tag Value:"), 0, 1);
        grid.add(tagValueField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                return new Tag(tagNameField.getText(), tagValueField.getText());
            }
            return null;
        });

        return dialog;
    }

    @FXML
    public void handleDeleteTagAction() {
        if (selectedPhoto == null) {
            showAlert("No Photo Selected", "Please select a photo to delete a tag from.", Alert.AlertType.INFORMATION);
            return;
        }
    
        if (selectedPhoto.getTags().isEmpty()) {
            showAlert("No Tags", "This photo does not have any tags to delete.", Alert.AlertType.INFORMATION);
            return;
        }
    
        // Show dialog to delete tag
        ChoiceDialog<Tag> dialog = new ChoiceDialog<>(null, selectedPhoto.getTags());
        dialog.setTitle("Delete Tag");
        dialog.setHeaderText("Select a tag to delete from this photo:");
        dialog.setContentText("Select tag:");
    
        Optional<Tag> result = dialog.showAndWait();
    
        result.ifPresent(tag -> {
            selectedPhoto.removeTag(tag);
            showAlert("Tag Deleted", "Tag removed from photo successfully.", Alert.AlertType.INFORMATION);
            saveUserData();
        });
    }

    @FXML
    private void handleLogoutAction(ActionEvent event) {
        try {
            // Save any changes
            saveUserData();

            // Get the current stage from the event source
            Stage stage;
            if (event.getSource() instanceof MenuItem) {
                // If the event source is a MenuItem, get the owner window directly
                MenuItem menuItem = (MenuItem) event.getSource();
                stage = (Stage) menuItem.getParentPopup().getOwnerWindow();
            } else {
                // Otherwise, it is coming from a Node
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }

            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/login1.fxml"));
            Parent root = loader.load();
            
            // Set the scene to the login screen
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (perhaps show an error alert)
        }
    }

    @FXML
private void onSearch(ActionEvent event) {
    Dialog<Tag> dialog = createTagSearchDialog();
    Optional<Tag> result = dialog.showAndWait();

    result.ifPresent(this::performTagSearch);
}

private Dialog<Tag> createTagSearchDialog() {
    Dialog<Tag> dialog = new Dialog<>();
    dialog.setTitle("Search by Tag");

    // Set the button types.
    ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    TextField tagNameField = new TextField();
    tagNameField.setPromptText("Tag Name");
    TextField tagValueField = new TextField();
    tagValueField.setPromptText("Tag Value");

    grid.add(new Label("Tag Name:"), 0, 0);
    grid.add(tagNameField, 1, 0);
    grid.add(new Label("Tag Value:"), 0, 1);
    grid.add(tagValueField, 1, 1);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == searchButtonType) {
            return new Tag(tagNameField.getText(), tagValueField.getText());
        }
        return null;
    });

    return dialog;
}

private void performTagSearch(Tag tag) {
    System.out.println("Performing tag search with parameters: " + tag);
    tilePane.getChildren().clear();
    searchResults.clear();

    Set<String> displayedPhotos = new HashSet<>();

    for (Album album : currentUser.getAlbumsList()) {
        for (Photo photo : album.getPhotos()) {
            if (displayedPhotos.contains(photo.getPath())) {
                continue;
            }

            if (photo.getTags().stream().anyMatch(t -> t.getName().equalsIgnoreCase(tag.getName()) &&
                                                       t.getValue().equalsIgnoreCase(tag.getValue()))) {
                System.out.println("Match found: " + photo.getName());
                displayPhotoInTilePane(photo);
                searchResults.add(photo);
                displayedPhotos.add(photo.getPath());
            }
        }
    }

    if (searchResults.isEmpty()) {
        showAlert("No Results", "No photos found with the specified tag.", Alert.AlertType.INFORMATION);
    } else {
        compileResultsButton.setVisible(true); // Show compile results button if needed
    }
}


    
    
    private void displayPhotoInTilePane(Photo photo) {
        System.out.println("Displaying photo: " + photo.getName() + " with caption: " + photo.getCaption());

        ImageView imageView = new ImageView(new Image(new File(photo.getPath()).toURI().toString()));
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        Label captionLabel = new Label(photo.getCaption());
    
        VBox photoBox = new VBox(5);
        photoBox.getChildren().addAll(imageView, captionLabel);
        photoBox.setOnMouseClicked(event -> {
            selectedPhoto = photo;
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                List<Photo> photos = new ArrayList<>();
                photos.add(photo);
                openPhotoViewer(photos, 0);
            }
        });
    
        tilePane.getChildren().add(photoBox);
    }
    

    /*private AdvancedSearchParameters showAdvancedSearchDialog() {
        System.out.println("Displaying Advanced Search Dialog");
        Dialog<AdvancedSearchParameters> dialog = new Dialog<>();
        dialog.setTitle("Advanced Search");
        
        dialog.getDialogPane().setMinWidth(600);
        
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);
        
        System.out.println("Dialog components initialized");
        
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        
        ListView<HBox> tagsListView = new ListView<>();
        tagsListView.setMinWidth(300);
        
        System.out.println("Date pickers and tags list view set up");
        
        Button addButton = new Button("Add Tag");
        addButton.setOnAction(e -> {
            System.out.println("Add Tag button clicked");
            if (tagsListView.getItems().size() < 2) {
                TextField tagNameField = new TextField();
                tagNameField.setPromptText("Tag Name");
                TextField tagValueField = new TextField();
                tagValueField.setPromptText("Tag Value");
                Button deleteButton = new Button("Delete");
                HBox tagBox = new HBox(10, new Label("Tag:"), tagNameField, new Label("Value:"), tagValueField, deleteButton);
                System.out.println("New tag row added");
                tagsListView.getItems().add(tagBox);
                deleteButton.setOnAction(event -> {
                    System.out.println("Delete button clicked for tag");
                    tagsListView.getItems().remove(tagBox);
                });
            } else {
                System.out.println("Limit of 2 tags reached");
            }
        });
        
        CheckBox useAndCheckbox = new CheckBox("Use AND instead of OR");
        
        System.out.println("Add Tag functionality set up");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints column1 = new ColumnConstraints(100); // First column for labels
        ColumnConstraints column2 = new ColumnConstraints(300); // Second column for inputs
        ColumnConstraints column3 = new ColumnConstraints(300); // Third column for tags ListView
        ColumnConstraints column4 = new ColumnConstraints(100); // Fourth column for add button
        grid.getColumnConstraints().addAll(column1, column2, column3, column4);
        
        grid.add(new Label("Start Date:"), 0, 0);
        grid.add(startDatePicker, 1, 0);
        grid.add(new Label("End Date:"), 0, 1);
        grid.add(endDatePicker, 1, 1);
        grid.add(new Label("Tags:"), 0, 2);
        grid.add(tagsListView, 1, 2, 2, 1);
        grid.add(addButton, 3, 2);
        grid.add(new Label("Search Logic:"), 0, 3);
        grid.add(useAndCheckbox, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            System.out.println("Dialog button clicked: " + dialogButton.getText());
            if (dialogButton == searchButtonType) {
                Map<String, String> tags = new HashMap<>();
                tagsListView.getItems().forEach(item -> {
                    TextField nameField = (TextField) item.getChildren().get(1);
                    TextField valueField = (TextField) item.getChildren().get(3);
                    tags.put(nameField.getText(), valueField.getText());
                    System.out.println("Tag added: " + nameField.getText() + " = " + valueField.getText());
                });
                
                System.out.println("Search button clicked, parameters set");
                return new AdvancedSearchParameters(startDatePicker.getValue(), endDatePicker.getValue(), tags, useAndCheckbox.isSelected());
            }
            System.out.println("Search cancelled or no parameters entered");
            return null;
        });
        
        Optional<AdvancedSearchParameters> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println("Dialog closed with parameters set");
            AdvancedSearchParameters params = result.get();
            performAdvancedSearch(params);  // Call search method directly after getting the parameters
            return params;
        } else {
            System.out.println("Dialog closed without setting parameters");
            return null;
        }
    }

    private boolean matchesSearchCriteria(Photo photo, AdvancedSearchParameters params) {
        boolean dateMatch = true;
        if (params.getStartDate() != null && params.getEndDate() != null) {
            LocalDateTime photoDate = photo.getLastModified();
            LocalDateTime startOfDay = params.getStartDate().atStartOfDay();
            LocalDateTime endOfDay = params.getEndDate().atTime(23, 59);
            dateMatch = !photoDate.isBefore(startOfDay) && !photoDate.isAfter(endOfDay);
        }
    
        boolean tagMatch = params.getTags().isEmpty(); // Assume match if no tags specified
    
        if (!params.getTags().isEmpty()) {
            if (params.isUseAnd()) {
                tagMatch = true; // Assume true for AND, any false will override
                for (Map.Entry<String, String> entry : params.getTags().entrySet()) {
                    boolean matchThisTag = photo.getTags().stream()
                                                 .anyMatch(tag -> tag.getName().equalsIgnoreCase(entry.getKey()) &&
                                                                   tag.getValue().equalsIgnoreCase(entry.getValue()));
                    if (!matchThisTag) {
                        tagMatch = false; // If any tag does not match, set to false
                        break;
                    }
                }
            } else {
                // OR condition: true if any of the tags match
                tagMatch = params.getTags().entrySet().stream()
                                 .anyMatch(entry -> photo.getTags().stream()
                                                          .anyMatch(tag -> tag.getName().equalsIgnoreCase(entry.getKey()) &&
                                                                            tag.getValue().equalsIgnoreCase(entry.getValue())));
            }
        }
    
        return dateMatch && tagMatch;
    }

    @FXML
    private void onAdvancedSearch(ActionEvent event) {
        System.out.println("Advanced search initiated");

        AdvancedSearchParameters params = showAdvancedSearchDialog();
        if (params != null) {
            System.out.println("Search parameters received, initiating search...");
            performAdvancedSearch(params);
        } else {
            System.out.println("Search cancelled or no parameters entered");
        }
    }

    private List<Photo> searchResults = new ArrayList<>();  // Store the search results

    @FXML
    private VBox searchResultsContainer; // Ensure this is linked to your FXML

    private Button compileResultsButton;  // Declare the button at class level to manage its visibility


    private void performAdvancedSearch(AdvancedSearchParameters params) {
        System.out.println("Performing advanced search with parameters: " + params);
        tilePane.getChildren().clear(); // Clear previous results
        searchResults.clear(); // Clear previous search results
        
        Set<String> uniquePhotoPaths = new HashSet<>();  // Use a Set to track unique photo paths
    
        for (Album album : currentUser.getAlbumsList()) {
            for (Photo photo : album.getPhotos()) {
                if (matchesSearchCriteria(photo, params) && uniquePhotoPaths.add(photo.getPath())) {
                    System.out.println("Match found: " + photo.getName());
                    displayPhotoInTilePane(photo);
                    searchResults.add(photo);  // Add photo to search results
                }
            }
        }
        
        if (!searchResults.isEmpty()) {
            if (compileResultsButton == null) {
                compileResultsButton = new Button("Compile Results into Album");
                compileResultsButton.setOnAction(this::compileResultsIntoAlbum);
            }
            if (!tilePane.getChildren().contains(compileResultsButton)) {
                tilePane.getChildren().add(0, compileResultsButton);  // Add the button to the top
            }
        } else {
            tilePane.getChildren().remove(compileResultsButton);  // Remove the button if no results
        }
    }
    
    

    private void compileResultsIntoAlbum(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Compile Results into Album");
        dialog.setHeaderText("Enter the name for the new album:");
        dialog.setContentText("Album name:");
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(albumName -> {
            if (!albumName.trim().isEmpty() && currentUser.getAlbum(albumName.trim()) == null) {
                Album newAlbum = new Album(albumName.trim());
                for (Photo originalPhoto : searchResults) {
                    // Copy the photo, retaining all its properties
                    Photo copiedPhoto = new Photo(originalPhoto.getName(), originalPhoto.getPath());
                    copiedPhoto.setCaption(originalPhoto.getCaption());
                    // Copy tags
                    for (Tag tag : originalPhoto.getTags()) {
                        copiedPhoto.addTag(new Tag(tag.getName(), tag.getValue()));
                    }
                    newAlbum.addPhoto(copiedPhoto);
                }
                currentUser.addAlbum(newAlbum);
                loadAlbumsForUser(); // Refresh the album list
                saveUserData(); // Save the updated user data
            } else {
                showAlert("Error", "Invalid album name or album already exists.", Alert.AlertType.ERROR);
            }
        });
    
        // Hide the button after compiling the results into an album
        tilePane.getChildren().remove(compileResultsButton);
    }

    public void clearSearchResults() {
        searchResults.clear();
        if (compileResultsButton != null) {
            tilePane.getChildren().remove(compileResultsButton);
        }
    }*/

    @FXML
    private void onSearchByDates(ActionEvent event) {
        System.out.println("Search by dates initiated");
        clearSearchResults(); // Clear previous search results before starting a new search
        AdvancedSearchParameters params = showDateSearchDialog();
        if (params != null) {
            System.out.println("Search parameters received, initiating search by dates...");
            performDateSearch(params);
        } else {
            System.out.println("Search cancelled or no parameters entered");
        }
    }
    

private AdvancedSearchParameters showDateSearchDialog() {
    System.out.println("Displaying Date Search Dialog");
    Dialog<AdvancedSearchParameters> dialog = new Dialog<>();
    dialog.setTitle("Date Search");

    dialog.getDialogPane().setMinWidth(300);

    ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

    DatePicker startDatePicker = new DatePicker();
    DatePicker endDatePicker = new DatePicker();

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add(new Label("Start Date:"), 0, 0);
    grid.add(startDatePicker, 1, 0);
    grid.add(new Label("End Date:"), 0, 1);
    grid.add(endDatePicker, 1, 1);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == searchButtonType) {
            return new AdvancedSearchParameters(startDatePicker.getValue(), endDatePicker.getValue(), null, false);
        }
        return null;
    });

    Optional<AdvancedSearchParameters> result = dialog.showAndWait();
    if (result.isPresent()) {
        System.out.println("Dialog closed with parameters set");
        return result.get();
    } else {
        System.out.println("Dialog closed without setting parameters");
        return null;
    }
}

private void performDateSearch(AdvancedSearchParameters params) {
    System.out.println("Performing date search with parameters: " + params);
    tilePane.getChildren().clear();
    searchResults.clear();
    
    Set<String> displayedPhotos = new HashSet<>(); // Set to track displayed photo paths or IDs

    LocalDateTime startDateTime = params.getStartDate().atStartOfDay();
    LocalDateTime endDateTime = params.getEndDate().atTime(23, 59);

    for (Album album : currentUser.getAlbumsList()) {
        for (Photo photo : album.getPhotos()) {
            if (displayedPhotos.contains(photo.getPath())) {
                continue; // Skip this photo as it's already been displayed
            }
            
            LocalDateTime photoDateTime = photo.getLastModified();
            if (!photoDateTime.isBefore(startDateTime) && !photoDateTime.isAfter(endDateTime)) {
                System.out.println("Match found: " + photo.getName());
                displayPhotoInTilePane(photo);
                searchResults.add(photo);
                displayedPhotos.add(photo.getPath()); // Remember this photo as displayed
            }
        }
    }

    if (!searchResults.isEmpty()) {
        compileResultsButton.setVisible(true);
    } else {
        compileResultsButton.setVisible(false);
    }
}




@FXML
private void onSearchByTagPairs(ActionEvent event) {
    System.out.println("Search by tag pairs initiated");
    AdvancedSearchParameters params = showTagSearchDialog();
    if (params != null) {
        System.out.println("Search parameters received, initiating search by tags...");
        performTagSearch(params);
    } else {
        System.out.println("Search cancelled or no parameters entered");
    }
}

private AdvancedSearchParameters showTagSearchDialog() {
    System.out.println("Displaying Tag Search Dialog");
    Dialog<AdvancedSearchParameters> dialog = new Dialog<>();
    dialog.setTitle("Tag Search");

    dialog.getDialogPane().setMinWidth(500);  // Increase dialog width

    ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

    ListView<HBox> tagsListView = new ListView<>();
    tagsListView.setMinWidth(450);  // Increase list view width
    tagsListView.setMaxHeight(100);  // Limit list view height

    // Initialize with two tag fields
    for (int i = 0; i < 2; i++) {
        TextField tagNameField = new TextField();
        tagNameField.setPromptText("Tag Name");
        tagNameField.setMinWidth(150);  // Set minimum width for text field

        TextField tagValueField = new TextField();
        tagValueField.setPromptText("Tag Value");
        tagValueField.setMinWidth(150);  // Set minimum width for text field

        HBox tagBox = new HBox(10, new Label("Tag:"), tagNameField, new Label("Value:"), tagValueField);
        tagsListView.getItems().add(tagBox);
    }

    CheckBox andLogicCheckBox = new CheckBox("Use AND logic (unchecked for OR)");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add(new Label("Tags:"), 0, 0);
    grid.add(tagsListView, 1, 0);
    grid.add(andLogicCheckBox, 1, 1);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == searchButtonType) {
            Map<String, String> tags = new HashMap<>();
            tagsListView.getItems().forEach(item -> {
                TextField nameField = (TextField) item.getChildren().get(1);
                TextField valueField = (TextField) item.getChildren().get(3);
                tags.put(nameField.getText(), valueField.getText());
            });
            return new AdvancedSearchParameters(null, null, tags, andLogicCheckBox.isSelected());
        }
        return null;
    });

    Optional<AdvancedSearchParameters> result = dialog.showAndWait();
    if (result.isPresent()) {
        System.out.println("Dialog closed with parameters set");
        return result.get();
    } else {
        System.out.println("Dialog closed without setting parameters");
        return null;
    }
}


private void performTagSearch(AdvancedSearchParameters params) {
    System.out.println("Performing tag search with parameters: " + params);
    tilePane.getChildren().clear();
    searchResults.clear();

    Map<String, String> tags = params.getTags();
    boolean useAndLogic = params.isUseAnd();
    Set<String> displayedPhotoPaths = new HashSet<>(); // Set to track displayed photo paths

    for (Album album : currentUser.getAlbumsList()) {
        for (Photo photo : album.getPhotos()) {
            if (displayedPhotoPaths.contains(photo.getPath())) {
                continue; // Skip this photo as it's already been displayed
            }

            boolean matches = false;
            if (useAndLogic) {
                matches = tags.entrySet().stream()
                              .allMatch(entry -> photo.getTags().stream()
                              .anyMatch(tag -> tag.getName().equalsIgnoreCase(entry.getKey()) &&
                                               tag.getValue().equalsIgnoreCase(entry.getValue())));
            } else {
                matches = tags.entrySet().stream()
                              .anyMatch(entry -> photo.getTags().stream()
                              .anyMatch(tag -> tag.getName().equalsIgnoreCase(entry.getKey()) &&
                                               tag.getValue().equalsIgnoreCase(entry.getValue())));
            }
            if (matches) {
                System.out.println("Match found: " + photo.getName());
                displayPhotoInTilePane(photo);
                searchResults.add(photo);
                displayedPhotoPaths.add(photo.getPath()); // Add to displayed photos set
            }
        }
    }

    if (!searchResults.isEmpty()) {
        compileResultsButton.setVisible(true);
    } else {
        compileResultsButton.setVisible(false);
    }
}



private void compileResultsIntoAlbum(ActionEvent event) {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Compile Results into Album");
    dialog.setHeaderText("Enter the name for the new album:");
    dialog.setContentText("Album name:");

    Optional<String> result = dialog.showAndWait();
    result.ifPresent(albumName -> {
        if (!albumName.trim().isEmpty() && currentUser.getAlbum(albumName.trim()) == null) {
            Album newAlbum = new Album(albumName.trim());
            for (Photo originalPhoto : searchResults) {
                // Copy the photo, retaining all its properties
                Photo copiedPhoto = new Photo(originalPhoto.getName(), originalPhoto.getPath());
                copiedPhoto.setCaption(originalPhoto.getCaption());
                // Copy tags
                for (Tag tag : originalPhoto.getTags()) {
                    copiedPhoto.addTag(new Tag(tag.getName(), tag.getValue()));
                }
                newAlbum.addPhoto(copiedPhoto);
            }
            currentUser.addAlbum(newAlbum);
            loadAlbumsForUser(); // Refresh the album list
            saveUserData(); // Save the updated user data
        } else {
            showAlert("Error", "Invalid album name or album already exists.", Alert.AlertType.ERROR);
        }
    });

    // Hide the button after compiling the results into an album
    tilePane.getChildren().remove(compileResultsButton);
}

}

