package photos.model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataManager {
    private static final String DATA_FOLDER = "data";
    private static final String FILE_EXTENSION = ".ser";

    static {
        File dataDir = new File(DATA_FOLDER);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        createDefaultUsers();
    }

    public static User loadOrCreateUser(String username) throws IOException, ClassNotFoundException {
        User user = loadUserData(username);
        if (user == null) {
            user = new User(username);
            saveUserData(user);
        }
        return user;
    }

    public static void initializeStockPhotos(User stockUser) throws IOException {
        Album stockAlbum = stockUser.getAlbum("stock");
        if (stockAlbum == null) {
            stockAlbum = new Album("stock");
            stockUser.addAlbum(stockAlbum);
            StockPhotoLoader.loadStockPhotos(stockAlbum);
        }
    }

    private static void createDefaultUsers() {
        try {
            if (loadUserData("admin") == null) {
                saveUserData(new User("admin"));
            }
            if (loadUserData("stock") == null) {
                saveUserData(new User("stock"));
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error creating default users: " + e.getMessage());
        }
    }

    public static void saveUserData(User user) throws IOException {
        String filename = DATA_FOLDER + File.separator + user.getUsername() + FILE_EXTENSION;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(user);
        }
    }

    public static User loadUserData(String username) throws IOException, ClassNotFoundException {
        String filename = DATA_FOLDER + File.separator + username + FILE_EXTENSION;
        File userFile = new File(filename);
        if (userFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
                return (User) ois.readObject();
            }
        }
        return null;
    }

    public static Map<String, User> loadAllUsers() throws IOException, ClassNotFoundException {
        Map<String, User> users = new HashMap<>();
        File dataDir = new File(DATA_FOLDER);
        File[] userFiles = dataDir.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        if (userFiles != null) {
            for (File file : userFiles) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    User user = (User) ois.readObject();
                    users.put(user.getUsername(), user);
                }
            }
        }
        return users;
    }

    public static boolean isValidUser(String username) {
        try {
            return loadUserData(username) != null;
        } catch (Exception e) {
            System.out.println("Error loading user data: " + e.getMessage());
            return false;
        }
    }

    // Inner class for loading stock photos
    public static class StockPhotoLoader {
        private static final String STOCK_PHOTO_DIR = "src/photos/data/stock";

        public static void loadStockPhotos(Album stockAlbum) {
            File stockDir = new File(STOCK_PHOTO_DIR);
            File[] stockPhotos = stockDir.listFiles();
            if (stockPhotos != null) {
                for (File photoFile : stockPhotos) {
                    if (photoFile.isFile()) {
                        Photo photo = new Photo(photoFile.getName(), photoFile.getAbsolutePath());
                        stockAlbum.addPhoto(photo);
                    }
                }
            }
        }
    }

    public static void createUser(String username) throws IOException {
        User user = new User(username);
        saveUserData(user); // Save the new user data to file
    }

    public static void deleteUserData(String username) throws IOException {
        String filename = DATA_FOLDER + File.separator + username + FILE_EXTENSION;
        File userFile = new File(filename);
        if (userFile.exists()) {
            if (!userFile.delete()) {
                throw new IOException("Failed to delete user data for " + username);
            }
        } else {
            throw new FileNotFoundException("User data file not found for " + username);
        }
    }
    
    public static List<String> loadAllUsernames() throws IOException, ClassNotFoundException {
        List<String> usernames = new ArrayList<>();
        File dataDir = new File(DATA_FOLDER);
        File[] userFiles = dataDir.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
    
        if (userFiles != null) {
            for (File file : userFiles) {
                String filename = file.getName();
                String username = filename.substring(0, filename.length() - FILE_EXTENSION.length());
                usernames.add(username);
            }
        }
    
        return usernames;
    }
    

}
