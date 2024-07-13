package photos.model;

import java.io.Serializable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private String name;
    private String path;
    private LocalDateTime lastModified;
    private List<Tag> tags;
    private String caption;
    

    public Photo(String name, String path) {
        this.name = name;
        this.path = path;
        this.lastModified = getLastModifiedDate(path);
        this.tags = new ArrayList<>();
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    private LocalDateTime getLastModifiedDate(String path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
            return attrs.lastModifiedTime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (IOException e) {
            e.printStackTrace();
            return LocalDateTime.now(); 
        }
    }

    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> newTags) {
        this.tags = new ArrayList<>(newTags); 
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
