package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private static final long serialVersionUID = 1L; 
    private String username;
    private Map<String, Album> albums;

    private Map<String, Photo> allPhotos = new HashMap<>();

    public Photo getOrCreatePhoto(String path, String name) {
        return allPhotos.computeIfAbsent(path, p -> new Photo(name, p));
    }

    public User(String username) {
        this.username = username;
        this.albums = new HashMap<>();
    }

    public void addAlbum(Album album) {
        if (!this.albums.containsKey(album.getName())) {
            this.albums.put(album.getName(), album);
        } else {
            System.out.println("Album " + album.getName() + " already exists.");
        }
    }

    public void addAlbum(String albumName) {
        if (!this.albums.containsKey(albumName)) {
            Album newAlbum = new Album(albumName);
            this.albums.put(albumName, newAlbum);
        } else {
            System.out.println("Album " + albumName + " already exists.");
        }
    }

    public void removeAlbum(String albumName) {
        this.albums.remove(albumName);
    }

    public Album getAlbum(String name) {
        return this.albums.get(name);
    }

    public List<Album> getAlbumsList() {
        return new ArrayList<>(this.albums.values());
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void renameAlbum(String oldName, String newName) {
        if (oldName == null || newName == null || oldName.equals(newName) || !this.albums.containsKey(oldName)) {
            return; 
        }

        Album albumToRename = this.albums.remove(oldName);
        if (albumToRename != null) {
            albumToRename.setName(newName);
            this.albums.put(newName, albumToRename);
        }
    }
}
