package photos.app;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.Photo;
import photos.model.User;
import photos.model.UserDataManager;
import photos.view.PhotosController;

public class Photos extends Application {

    private static final String STOCK_USER = "stock";
    private static final String ADMIN_USER = "admin";
    private static final String STOCK_PHOTO_DIR = "Photos03/data/stock";
    private static Stage primaryStage;

    public static String getStockUser() {
        return STOCK_USER;
    }

    @Override
    public void start(Stage primaryStage) {
        Photos.primaryStage = primaryStage;
        initializeUsers();
        showLoginScreen();
        System.out.println(new File(STOCK_PHOTO_DIR).getAbsolutePath());
    }


    private void initializeUsers() {
        try {
            User adminUser = UserDataManager.loadUserData(ADMIN_USER);
            if (adminUser == null) {
                adminUser = new User(ADMIN_USER);
                UserDataManager.saveUserData(adminUser);
            }

            User stockUser = UserDataManager.loadUserData(STOCK_USER);
            if (stockUser == null) {
                stockUser = new User(STOCK_USER);
                initializeStockPhotos(stockUser);
                UserDataManager.saveUserData(stockUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initializeStockPhotos(User user) {
        if (user.getUsername().equalsIgnoreCase(STOCK_USER)) {
            Album stockAlbum = user.getAlbum("stock");
            if (stockAlbum == null || stockAlbum.getPhotos().isEmpty()) {
                stockAlbum = new Album("stock");
                user.addAlbum(stockAlbum);
                loadStockPhotos(stockAlbum);
            }
        }
    }

    private void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/login1.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Photo Application Login");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMainScreen(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(Photos.class.getResource("/photos/view/mainScreen.fxml"));
            Parent root = loader.load();
            PhotosController photosController = loader.getController();
            if (user != null && photosController != null) {
                photosController.userLoggedIn(user);
            }
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Photo Application");
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        private static void loadStockPhotos(Album album) {
            File stockDir = new File(STOCK_PHOTO_DIR);
            File[] stockPhotos = stockDir.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|bmp)$"));
            if (stockPhotos != null) {
                for (File photoFile : stockPhotos) {
                    if (!albumContainsPhoto(album, photoFile.getAbsolutePath())) {
                        Photo photo = new Photo(photoFile.getName(), photoFile.getAbsolutePath());
                        album.addPhoto(photo);
                    }
                }
            }
        }
    
        private static boolean albumContainsPhoto(Album album, String photoPath) {
            return album.getPhotos().stream().anyMatch(p -> p.getPath().equals(photoPath));
        }    

    public static void main(String[] args) {
        launch(args);
    }
}

