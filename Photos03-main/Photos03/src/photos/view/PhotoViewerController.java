package photos.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import photos.model.Photo;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PhotoViewerController {
    @FXML
    private ImageView imageView;
    @FXML
    private Label dateLabel; // Label to display the date of the photo
    @FXML
    private ListView<String> tagsListView;
    private List<Photo> photos;
    private int currentIndex;

    public void setPhotos(List<Photo> photos, int startIndex) {
        this.photos = photos;
        currentIndex = startIndex;
        showPhoto(currentIndex);
    }

    @FXML
    private void nextPhoto() {
        if (currentIndex < photos.size() - 1) {
            showPhoto(++currentIndex);
        }
    }

    @FXML
    private void previousPhoto() {
        if (currentIndex > 0) {
            showPhoto(--currentIndex);
        }
    }

    private void showPhoto(int index) {
        if (index >= 0 && index < photos.size()) {
            Photo photo = photos.get(index);
            imageView.setImage(new Image("file:" + photo.getPath()));
    
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateText = "Date: " + photo.getLastModified().format(formatter);
            String captionText = "Caption: " + photo.getCaption();
    
            // Combine date, caption, and tags into a single list
            List<String> photoDetails = new ArrayList<>();
            photoDetails.add(dateText);
            photoDetails.add(captionText);
            photoDetails.addAll(
                photo.getTags().stream()
                     .map(tag -> tag.getName() + ": " + tag.getValue())
                     .collect(Collectors.toList())
            );
    
            tagsListView.setItems(FXCollections.observableArrayList(photoDetails));
    
            currentIndex = index;
        }
    }
    
}