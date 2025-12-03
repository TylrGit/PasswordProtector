package org.tyler.passprotector.passprotector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;


public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        final int HEIGHT = 425; // Window Height
        final int WIDTH = (int) (HEIGHT*1.618); // Window Width

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Main.fxml")); // Loads FXML file
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);

        // Theme
        if(ConfigFileManager.theme) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm()); // Loads CSS file
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());
        }

        stage.setScene(scene);

        stage.setTitle("Pass Protector");

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/simple-icon-circle.png"))));

        // Mini window, add support for bigger, scalable, window later
        stage.setMaxHeight(HEIGHT);
        stage.setMaxWidth(WIDTH);

        // Removes toolbar
        stage.initStyle(StageStyle.UNDECORATED);

        stage.show();
    }

    public static void main(String[] args) {
        FileManager.createProjectDirectories();
        ConfigFileManager.createConfigFile();

        if (DatabaseAccess.connectDB()) {
            FileManager.getFilesInFolder();
            launch();
        } else {
            System.out.println("Could not connect to database");
        }
    }
}