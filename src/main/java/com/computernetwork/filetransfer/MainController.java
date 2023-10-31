package com.computernetwork.filetransfer;

import com.computernetwork.filetransfer.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class MainController {
    private LocalDatabase database;
    private NetworkSender sender;
    private String username;
    private ObservableList<FileData> userFile;
    private ObservableList<ServerFileData> searchResult;
    private String serverIP;
    @FXML
    private Pane filePanel;
    @FXML
    private Pane searchPanel;
    @FXML
    private Pane terminalPanel;
    @FXML
    private Pane userPanel;
    @FXML
    private VBox fileTab;
    @FXML
    private VBox searchTab;
    @FXML
    private VBox terminalTab;
    @FXML
    private VBox userTab;
    @FXML
    private Label loadingMessage;
    @FXML
    private ProgressBar loadingProgress;
    @FXML
    private VBox authBox;
    @FXML
    private Label authMessage;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField serverIPInput;
    @FXML
    private VBox userInfo;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField searchBar;
    @FXML
    private TableView<FileData> userFileTable;
    @FXML
    private TableView<ServerFileData> searchResultTable;
    @FXML
    private TableColumn<FileData, String> userFileColumn1;
    @FXML
    private TableColumn<FileData, Long> userFileColumn2;
    @FXML
    private TableColumn<FileData, String> userFileColumn3;
    @FXML
    private TableColumn<FileData, String> userFileColumn4;
    @FXML
    private TableColumn<FileData, String> userFileColumn5;
    @FXML
    private TableColumn<ServerFileData, String> searchResultColumn1;
    @FXML
    private TableColumn<ServerFileData, Long> searchResultColumn2;
    @FXML
    private TableColumn<ServerFileData, String> searchResultColumn3;
    @FXML
    private TableColumn<ServerFileData, String> searchResultColumn4;
    @FXML
    private TableColumn<ServerFileData, String> searchResultColumn5;
    @FXML
    public void initialize() {
        username = null;
        serverIP = null;
        userFile = null;
        searchResult = null;
        sender = new NetworkSender();
        //Open database and get initial value
        try {
            database = new LocalDatabase();
            username = database.getUser();
            serverIP = database.getServerIP();
            userFile = FXCollections.observableArrayList(database.getFileData());
        } catch (SQLException e) {
            databaseError("Can't connect to database");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        if (username == null || serverIP == null) {
            //authScreen();
            authSuccess();
        } else {
            auth(false);
        }

        //Set up userFile table
        userFileTable.setPlaceholder(new Text("You haven't uploaded any file"));
        userFileTable.setItems(userFile);
        userFileColumn1.setCellValueFactory(new PropertyValueFactory<FileData, String>("name"));
        userFileColumn2.setCellValueFactory(new PropertyValueFactory<FileData, Long>("size"));
        userFileColumn3.setCellValueFactory(new PropertyValueFactory<FileData, String>("uploadedDate.toString()"));
        userFileColumn4.setCellValueFactory(new PropertyValueFactory<FileData, String>("fileLocation"));
        userFileColumn5.setCellValueFactory(new PropertyValueFactory<FileData, String>("description"));

        //Set up searchResult table
        searchResultTable.setPlaceholder(new Text("No result"));
        searchResultTable.setItems(searchResult);
        searchResultColumn1.setCellValueFactory(new PropertyValueFactory<ServerFileData, String>("name"));
        searchResultColumn2.setCellValueFactory(new PropertyValueFactory<ServerFileData, Long>("size"));
        searchResultColumn3.setCellValueFactory(new PropertyValueFactory<ServerFileData, String>("uploadedDate.toString()"));
        searchResultColumn4.setCellValueFactory(new PropertyValueFactory<ServerFileData, String>("fileLocation"));
        searchResultColumn5.setCellValueFactory(new PropertyValueFactory<ServerFileData, String>("description"));

    }
    private void switchToPane(Pane selectedPane, VBox selectedTab) {
        filePanel.setVisible(selectedPane == filePanel);
        searchPanel.setVisible(selectedPane == searchPanel);
        terminalPanel.setVisible(selectedPane == terminalPanel);
        userPanel.setVisible(selectedPane == userPanel);

        fileTab.setDisable(selectedTab == fileTab);
        searchTab.setDisable(selectedTab == searchTab);
        terminalTab.setDisable(selectedTab == terminalTab);
        userTab.setDisable(selectedTab == userTab);
    }

    @FXML
    protected void onFileTabClick() {
        switchToPane(filePanel, fileTab);
    }
    @FXML
    protected void onSearchTabClick() {
        switchToPane(searchPanel, searchTab);
    }
    @FXML
    protected void onTerminalTabClick() {
        switchToPane(terminalPanel, terminalTab);
    }
    @FXML
    protected void onUserTabClick() {
        switchToPane(userPanel, userTab);
    }
    @FXML
    protected void onLoginClick() {
        username = usernameInput.getText();
        serverIP = serverIPInput.getText();
        auth(false);
    }
    @FXML
    protected void onSignUpClick() {
        username = usernameInput.getText();
        serverIP = serverIPInput.getText();
        auth(true);
    }
    @FXML
    protected void onSignOutClick() {
        authScreen();
    }
    protected void auth(boolean signUp) {
        sender.setServerIP(serverIP);
        Task<Respond> task = signUp ? sender.signUp(username) : sender.login(username);

        task.setOnSucceeded(event -> {
            if (task.getValue().isSuccess()) {
                authSuccess();
            } else {
                authMessage.setText(task.getValue().getMessage());
                if (!userPanel.isVisible()) authScreen();
            }
            finishTask();
        });

        task.setOnFailed(event -> {
            finishTask();
            serverError(task.getException().getMessage());
        });

        startTask(task);
    }
    @FXML
    protected void onUploadClick() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file to upload");
        File selectedFile = chooser.showOpenDialog(filePanel.getScene().getWindow());
        if (selectedFile == null) return;

        // Create a GridPane for the custom dialog layout
        VBox box = new VBox();
        box.setPadding(new Insets(10, 10, 10, 10));
        box.setSpacing(10);

        // Create input fields
        TextField nameField = new TextField();
        TextArea descriptionArea = new TextArea();

        box.getChildren().add(new Label("File location: " + selectedFile.getAbsolutePath()));
        box.getChildren().add(new Label("File size: " + selectedFile.length() + "bytes"));
        box.getChildren().add(new Label("Name:"));
        box.getChildren().add(nameField);
        box.getChildren().add(new Label("Description:"));
        box.getChildren().add(descriptionArea);

        // Create a custom dialog
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Upload a file");
        dialog.setHeaderText("Enter file name to be saved on server and description.");

        // Set the buttons
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        dialog.getDialogPane().setContent(box);

        final Button ok = (Button) dialog.getDialogPane().lookupButton(okButton);
        ok.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                if (database.existFile(nameField.getText())) {
                    databaseError("File with that name already exist");
                    event.consume();
                }
            } catch (SQLException e) {
                databaseError(e.getMessage());
            }
        });

        // Handle the result when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                String name = nameField.getText();
                String description = descriptionArea.getText();
                return new String[]{name, description};
            }
            return null;
        });

        if (dialog.showAndWait().isPresent()) {
            FileData file = new FileData(dialog.getResult()[0], selectedFile.length(), dialog.getResult()[1], selectedFile.getAbsolutePath());
            try {
                database.insertFileData(file);
                Task<Respond> task = sender.upload(file);
                task.setOnSucceeded(event -> {
                    finishTask();
                    if (!task.getValue().isSuccess()) {
                        serverError(task.getValue().getMessage());
                    }
                });
                task.setOnFailed(event -> {
                    finishTask();
                    try {
                        database.deleteFileData(file.getName());
                    } catch (SQLException e) {
                        databaseError(e.getMessage());
                    }
                    serverError(task.getException().getMessage());
                });
                startTask(task);
            }
            catch (SQLException e) {
                databaseError("Cannot insert file data to database");
            }
        }
    }
    @FXML
    protected void onSearchClick() {
        Task<ArrayList<ServerFileData>> task = sender.search(searchBar.getText());
        task.setOnSucceeded(event -> {
            finishTask();
            searchResult = FXCollections.observableArrayList(task.getValue());
        });
        task.setOnFailed(event -> {
            finishTask();
            serverError(task.getException().getMessage());
        });
        startTask(task);
    }
    /**
     * Switch to auth screen
     */
    private void authScreen() {
        fileTab.setDisable(true);
        searchTab.setDisable(true);
        terminalTab.setDisable(true);
        userTab.setDisable(true);

        userInfo.setVisible(false);
        authBox.setVisible(true);

        userPanel.setVisible(true);
    }

    /**
     * Successful auth
     */
    private void authSuccess() {
        fileTab.setDisable(false);
        searchTab.setDisable(false);
        terminalTab.setDisable(false);
        userTab.setDisable(true);

        authBox.setVisible(false);
        userInfo.setVisible(true);

        userPanel.setVisible(true);
        usernameLabel.setText(username);
    }
    private void databaseError(String reason) {
        if (reason == null) reason = "Access denied or connection closed";
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database error");
        alert.setContentText(reason);
        alert.showAndWait();
    }
    private void serverError(String reason) {
        if (reason == null) reason = "Can't connect to server";
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Server error");
        alert.setContentText(reason);
        alert.showAndWait();
    }
    private void startTask(Task task) {
        loadingMessage.textProperty().bind(task.messageProperty());
        loadingProgress.progressProperty().bind(task.progressProperty());
        loadingMessage.setVisible(true);
        loadingProgress.setVisible(true);

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
    private void finishTask() {
        loadingMessage.setVisible(false);
        loadingProgress.setVisible(false);
    }
    public void onClose() throws SQLException, IOException {
        if (database != null) database.close();
    }
}