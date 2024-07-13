package photos.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getEarliestDate() {
        return photos.stream()
                     .map(Photo::getLastModified) // Replace with the method that gets the photo's date
                     .min(LocalDateTime::compareTo)
                     .orElse(LocalDateTime.now()); // or another default value
    }

    public LocalDateTime getLatestDate() {
        return photos.stream()
                     .map(Photo::getLastModified) // Replace with the method that gets the photo's date
                     .max(LocalDateTime::compareTo)
                     .orElse(LocalDateTime.now()); // or another default value
    }
}
