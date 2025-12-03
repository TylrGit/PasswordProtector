package org.tyler.passprotector.passprotector;


import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static double ogMouseX = 0;
    private static double ogMouseY = 0;

    // Toolbar Elements
    @FXML
    private BorderPane toolBar;
    @FXML
    private ImageView closeApp;
    @FXML
    private ImageView themeChanger;
    @FXML
    private ImageView minimizeApp;

    // Tab Elements
    @FXML
    private TabPane navTabPane;

    // Tabs
    @FXML
    private Tab hubTab;
    @FXML
    private Tab fileManageTab;
    @FXML
    private Tab passManageTab;

    // Hub Tab Elements
    @FXML
    private Button startButtonManageFiles;
    @FXML
    private Button startButtonManagePasswords;

    // Adds updates to the updates list
    @FXML
    private ListView<String> featureList;
    private final String[] features = {"- Securely store passwords", "- Use different passwords to\n secure passwords", "- Store an unlimited amount\n of passwords"};


    // File Manager Tab Elements
    // Table View Stuff
    @FXML
    private Button createFile;
    @FXML
    private Button deleteFile;
    @FXML
    private Button changePassword;

    @FXML
    private TableView<FileTableRow> fileTable;
    @FXML
    private TableColumn<FileTableRow, Button> lockColumn;
    @FXML
    private TableColumn<FileTableRow, String> fileNameColumn;


    // Password Manager Tab Elements
    @FXML
    private ChoiceBox<String> unlockedFileDropdown;

    // Password Table
    @FXML
    private Button createPassword;
    @FXML
    private Button deletePassword;

    @FXML
    private TableView<PasswordTableRow> passwordTable;
    @FXML
    private TableColumn<PasswordTableRow, String> sourceColumn;
    @FXML
    private TableColumn<PasswordTableRow, String> usernameColumn;
    @FXML
    private TableColumn<PasswordTableRow, String> passwordColumn;

    //ObservableList<PasswordTableRow> passwordTableData = FXCollections.observableArrayList();


    // Repeated Use Methods
    private String promptUser(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(prompt);
        dialog.setHeaderText(prompt+":");
        return dialog.showAndWait().orElse(null);
    }

    private void tabChange(javafx.event.Event e) {
        Tab tab = (Tab) e.getSource();
        ImageView image = (ImageView) tab.getGraphic();

        ColorAdjust colorAdjust = new ColorAdjust();

        // Changes image white or black
        if(tab.isSelected()) {
            colorAdjust.setBrightness(1.0);
        } else {
            colorAdjust.setBrightness(-1.0);
        }
        image.setEffect(colorAdjust);

        if(passManageTab.isSelected()) {
            // Update unlocked file dropdown
            unlockedFileDropdown.getItems().clear();
            unlockedFileDropdown.getItems().add("Pick an unlocked file");

            FileManager.getUnlockedFileList();
            unlockedFileDropdown.getItems().addAll(FileManager.getUnlockedFileList());

            unlockedFileDropdown.getSelectionModel().select(0);
            passwordTable.getItems().clear();
        }
    }

    private void encryptionLockButton(javafx.event.Event e) {
        // Delusional way to get the button's table row from event
        TableRow<?> tableRowFromEvent = (TableRow<?>) (((Button) e.getSource()).getParent().getParent());

        // Select the current row when clicking button, easier to work with getting data from table
        fileTable.getSelectionModel().select(tableRowFromEvent.getIndex());
        FileTableRow selectedRow = fileTable.getSelectionModel().getSelectedItem();

        String fileName = selectedRow.getFileNameColumn();
        boolean encryptionStatus = selectedRow.getLockStatus();


        String filePW = promptUser("Enter the file's password");
        // Check if file name was entered
        if(filePW == null || filePW.isEmpty()) {
            return;
        }

        // Verify with database
        if(!DatabaseAccess.verifyPassword(fileName, filePW)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("File password was incorrect");
            alert.show();
            return;
        }

        // True = locked/encrypted
        if(encryptionStatus) {
            // Change status in row class
            selectedRow.setLockStatus(false);

            // Change to Unlock icon
            selectedRow.getLockColumn().setText("\uD83D\uDD13");

            // Decrypt
            FileManager.decryptFileData(fileName, filePW);
        } else {
            // Change status in row class
            selectedRow.setLockStatus(true);

            // Change to Lock icon
            selectedRow.getLockColumn().setText("\uD83D\uDD10");

            // Encrypt
            FileManager.encryptFileData(fileName, filePW);
        }

        // Update file list
        FileManager.getFilesInFolder();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Tool Bar

        // Draggable toolbar
        toolBar.setOnMousePressed((e) -> {
            ogMouseX = e.getSceneX();
            ogMouseY = e.getSceneY();
        });
        toolBar.setOnMouseDragged((e) -> {
            Stage stage = (Stage) ((BorderPane) (e.getSource())).getScene().getWindow();

            stage.setX(e.getScreenX() - ogMouseX);
            stage.setY(e.getScreenY() - ogMouseY);
        });

        closeApp.setOnMouseClicked(e -> {
            ImageView image = (ImageView) (e.getSource());
            Stage stage = (Stage) image.getParent().getScene().getWindow();

            stage.close();
            /* Save features for stuff potentially? */
        });

        // On load use device theme
        Image initialThemeImage;
        if (ConfigFileManager.theme) {
            // Light Mode
            initialThemeImage = new Image(Objects.requireNonNull(getClass().getResource("images/sun.png")).toExternalForm());
        } else {
            // Dark Mode
            initialThemeImage = new Image(Objects.requireNonNull(getClass().getResource("images/moon.png")).toExternalForm());
        }
        themeChanger.setImage(initialThemeImage);

        themeChanger.setOnMouseClicked(e -> {
            ImageView imageElement = (ImageView) (e.getSource());
            Stage stage = (Stage) imageElement.getParent().getScene().getWindow();
            Scene scene = stage.getScene();

            themeChanger.setRotate(0);
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(300), themeChanger);
            rotateTransition.setByAngle(360);
            rotateTransition.setCycleCount(1);
            rotateTransition.setAutoReverse(false);
            rotateTransition.setInterpolator(Interpolator.EASE_IN);
            rotateTransition.play();


            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), themeChanger);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setAutoReverse(false);

            fadeOut.play();

            fadeOut.setOnFinished(_ -> {
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), themeChanger);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.setCycleCount(1);
                fadeIn.setAutoReverse(false);
                fadeIn.play();

                ConfigFileManager.theme = !ConfigFileManager.theme;
                if(ConfigFileManager.theme) {
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm()); // Loads CSS file

                    Image image = new Image(Objects.requireNonNull(getClass().getResource("images/sun.png")).toExternalForm());
                    themeChanger.setImage(image);
                    ConfigFileManager.saveSettings();
                } else {
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());

                    Image image = new Image(Objects.requireNonNull(getClass().getResource("images/moon.png")).toExternalForm());
                    themeChanger.setImage(image);
                    ConfigFileManager.saveSettings();
                }
            });
        });

        minimizeApp.setOnMouseClicked(e -> {
            ImageView image = (ImageView) (e.getSource());
            Stage stage = (Stage) image.getParent().getScene().getWindow();

            stage.setIconified(true);
        });

        // Tabs
        hubTab.setOnSelectionChanged(this::tabChange);
        fileManageTab.setOnSelectionChanged(this::tabChange);
        passManageTab.setOnSelectionChanged(this::tabChange);
        //Tab.setOnSelectionChanged(this::tabChange);


        // Hub Tab
        startButtonManageFiles.setOnMouseClicked(_ -> navTabPane.getSelectionModel().select(fileManageTab));
        startButtonManagePasswords.setOnMouseClicked(_ -> navTabPane.getSelectionModel().select(passManageTab));

        featureList.getItems().addAll(features); // For Update List



        // File Manager Tab
        // Table View
        lockColumn.setCellValueFactory(new PropertyValueFactory<>("lockColumn"));

        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileNameColumn"));
        fileNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Column "Styling"
        lockColumn.setReorderable(false);
        lockColumn.setResizable(false);
        fileNameColumn.setReorderable(false);
        fileNameColumn.setResizable(false);
        fileNameColumn.setStyle("-fx-alignment: CENTER-LEFT;");

        // Creates file, and adds its information to database
        createFile.setOnMouseClicked(_ -> {
            // Check if file name was entered
            String fileName = promptUser("Enter a File Name");
            if(fileName == null || fileName.isEmpty()) {
                return;
            }

            // If file name already in use
            if (FileManager.getFileExists(fileName) || DatabaseAccess.getFileDataByName(fileName) != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("File already exists");
                alert.show();
                return;
            }

            // Check if password was entered
            String filePassword = promptUser("Enter a Password");
            if(filePassword == null || filePassword.isEmpty()) {
                return;
            }

            // Create local file, update file list
            FileManager.createPasswordJson(fileName);
            FileManager.getFilesInFolder();

            // Add to DB
            DatabaseAccess.newFileDataInsert(fileName, filePassword, AES.generateSaltOrIV(), AES.generateSaltOrIV());

            // Add to table
            Button newButton = new Button("\uD83D\uDD13");
            newButton.setOnMouseClicked(this::encryptionLockButton);
            FileTableRow newData = new FileTableRow(false, newButton, fileName);
            fileTable.getItems().add(newData);
        });

        // Deletes the local file, and it's information on database
        deleteFile.setOnMouseClicked(_ -> {
            ObservableList<FileTableRow> allTableData;
            allTableData = fileTable.getItems();
            FileTableRow selectedRow = fileTable.getSelectionModel().getSelectedItem();

            // Check if a row is selected
            if(selectedRow == null) {
                return;
            }

            String fileName = selectedRow.getFileNameColumn();

            // Check if password was entered
            String fileNameResponse = promptUser("To confirm, enter the file name");
            if(fileNameResponse == null || fileNameResponse.isEmpty()) {
                return;
            }

            if(fileName.equals(fileNameResponse)) {
                // Remove local file, update file list
                FileManager.deleteFile(fileName);
                FileManager.getFilesInFolder();

                // Remove from DB
                DatabaseAccess.deleteFileData(fileName);

                // Remove from Table
                allTableData.remove(selectedRow);

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("File name was incorrect");
                alert.show();
            }
        });

        // For changing file password in database (and iv / salt)
        changePassword.setOnMouseClicked(_ -> {
            FileTableRow selectedRow = fileTable.getSelectionModel().getSelectedItem();

            // Check if a row is selected
            if(selectedRow == null) {
                return;
            }

            // If true (locked)
            if(selectedRow.getLockStatus()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Please unlock the file first");
                alert.show();
                return;
            }

            String fileName = selectedRow.getFileNameColumn();

            // Check if password was entered
            String filePassword = promptUser("Enter current file password");
            if(filePassword == null || filePassword.isEmpty()) {
                return;
            }

            if(DatabaseAccess.verifyPassword(fileName, filePassword)) {
                // Check if password was entered
                String newFilePassword = promptUser("Enter the new File Password");
                if(newFilePassword == null || newFilePassword.isEmpty()) {
                    return;
                }

                // Add to DB
                DatabaseAccess.updateFilePassword(fileName, newFilePassword, AES.generateSaltOrIV(), AES.generateSaltOrIV());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setHeaderText("File password changed");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("File password was incorrect");
                alert.show();
            }
        });

        fileNameColumn.setOnEditCommit(e -> {
            String oldName = e.getOldValue();
            String newName = e.getNewValue();

            if(newName.isEmpty()) {
                return;
            }
            // Get selected row object
            FileTableRow fileManagerTableRow = fileTable.getSelectionModel().getSelectedItem();

            // Rename local file, update file list
            FileManager.renameFile(oldName, newName);
            FileManager.getFilesInFolder();

            // Rename in DB
            DatabaseAccess.updateFileName(oldName, newName);

            // Rename in Table
            fileManagerTableRow.setFileNameColumn(newName);
        });

        // Adds initial table data from folder
        FileManager.passwordFiles.forEach(file -> {
            String fileName = file.getName();
            int dotIndex = fileName.indexOf(".");

            boolean encryptionStatus;
            String buttonSymbol;

            // Unencrypted, else encrypted
            if(fileName.endsWith(".json")) {
                encryptionStatus = false;
                buttonSymbol = "\uD83D\uDD13"; // Unlock Symbol

            } else {
                encryptionStatus = true;
                buttonSymbol = "\uD83D\uDD10"; // Lock Symbol
            }

            // Lock button creations
            Button lockButton = new Button(buttonSymbol);
            lockButton.setOnMouseClicked(this::encryptionLockButton);


            FileTableRow newData = new FileTableRow(encryptionStatus, lockButton, fileName.substring(0, dotIndex));
            fileTable.getItems().add(newData);
        });

        // Password Manager Tab

        // Password Table
        // Column setup
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("sourceColumn"));
        sourceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("usernameColumn"));
        usernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("passwordColumn"));
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Column "Styling"
        sourceColumn.setReorderable(false);
        sourceColumn.setResizable(true);
        usernameColumn.setReorderable(false);
        usernameColumn.setResizable(true);
        passwordColumn.setReorderable(false);
        passwordColumn.setResizable(true);

        // Check if dropdown selection changed
        unlockedFileDropdown.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            passwordTable.getItems().clear();
            if(newValue.equals("Pick an unlocked file")) {
                return;
            }

            // Update table from json file
            ArrayList<PasswordTableRow> passwords = FileManager.getPasswordsFromJson(newValue);
            if(passwords != null) {
                for(PasswordTableRow password : passwords) {
                    passwordTable.getItems().add(password);
                }
            }
        });

        createPassword.setOnMouseClicked(_ -> {
            // Dropdown selected file(name)
            String dropdownItem = unlockedFileDropdown.getValue();

            // Check if a file was selected
            if (dropdownItem.equals("Pick an unlocked file")) {
                return;
            }

            // Update table
            PasswordTableRow newData = new PasswordTableRow("...", "...", "...");
            passwordTable.getItems().add(newData);

            // Update json file
            ArrayList<PasswordTableRow> passwords = new ArrayList<>(passwordTable.getItems());
            FileManager.storeJson(dropdownItem, passwords);
        });

        deletePassword.setOnMouseClicked(_ -> {
            String fileName = unlockedFileDropdown.getValue();
            ObservableList<PasswordTableRow> allTableData;
            allTableData = passwordTable.getItems();
            PasswordTableRow selectedRow = passwordTable.getSelectionModel().getSelectedItem();

            // Check if a row is selected
            if(selectedRow == null) {
                return;
            }

            // Check if filename matches
            String fileCheck = promptUser("To confirm, enter the file name");
            if(!fileName.equals(fileCheck)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("File name did not match");
                alert.show();
                return;
            }
            // Remove from Table
            allTableData.remove(selectedRow);

            // Update json file
            ArrayList<PasswordTableRow> passwords = new ArrayList<>(passwordTable.getItems());
            FileManager.storeJson(fileName, passwords);
        });

        sourceColumn.setOnEditCommit(e -> {
            String fileName = unlockedFileDropdown.getValue();
            String newSource = e.getNewValue();

            if(newSource.isEmpty()) {
                return;
            }
            // Get selected row object
            PasswordTableRow passwordRow = passwordTable.getSelectionModel().getSelectedItem();

            // Rename in Table
            passwordRow.setSourceColumn(newSource);

            // Update json file
            ArrayList<PasswordTableRow> passwords = new ArrayList<>(passwordTable.getItems());
            FileManager.storeJson(fileName, passwords);
        });

        usernameColumn.setOnEditCommit(e -> {
            String fileName = unlockedFileDropdown.getValue();
            String newUsername = e.getNewValue();

            if(newUsername.isEmpty()) {
                return;
            }
            // Get selected row object
            PasswordTableRow passwordRow = passwordTable.getSelectionModel().getSelectedItem();

            // Rename in Table
            passwordRow.setUsernameColumn(newUsername);

            // Update json file
            ArrayList<PasswordTableRow> passwords = new ArrayList<>(passwordTable.getItems());
            FileManager.storeJson(fileName, passwords);
        });

        passwordColumn.setOnEditCommit(e -> {
            String fileName = unlockedFileDropdown.getValue();
            String password = e.getNewValue();

            if(password.isEmpty()) {
                return;
            }
            // Get selected row object
            PasswordTableRow passwordRow = passwordTable.getSelectionModel().getSelectedItem();

            // Rename in Table
            passwordRow.setPasswordColumn(password);

            // Update json file
            ArrayList<PasswordTableRow> passwords = new ArrayList<>(passwordTable.getItems());
            FileManager.storeJson(fileName, passwords);
        });


    }
}