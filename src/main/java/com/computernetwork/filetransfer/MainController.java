package com.computernetwork.filetransfer;

import com.computernetwork.filetransfer.Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class MainController {
    private LocalDatabase database;
    private NetworkSender sender;
    private NetworkListener listener;
    private String username;
    private ObservableList<ClientFileData> userFile;
    private FilteredList<ClientFileData> filteredData;
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
    private TextField localSearchBar;
    @FXML
    private TableView<ClientFileData> userFileTable;
    @FXML
    private TextField searchBar;
    @FXML
    private TableView<ServerFileData> searchResultTable;
    @FXML
    private TableColumn<ClientFileData, String> userFileColumn1;
    @FXML
    private TableColumn<ClientFileData, Long> userFileColumn2;
    @FXML
    private TableColumn<ClientFileData, String> userFileColumn3;
    @FXML
    private TableColumn<ClientFileData, String> userFileColumn4;
    @FXML
    private TableColumn<ClientFileData, String> userFileColumn5;
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
    private TextField input;
    @FXML
    private  TextArea output;
    @FXML
    public void initialize() {
        username = null;
        serverIP = null;
        userFile = null;
        searchResult = null;
        //Open database and get initial value
        try {
            database = new LocalDatabase();
            username = database.getUser();
            serverIP = database.getServerIP();
            userFile = FXCollections.observableArrayList(database.getFileData());
        } catch (SQLException e) {
            databaseError("Can't connect to database");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        sender = new NetworkSender();
        listener = new NetworkListener(database, output);
        try {
            listener.start();
        } catch (IOException e) {
            output.appendText("Failed to start listener: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        if (username == null || serverIP == null) {
            authScreen();
        } else {
            auth(false);
        }

        filteredData = new FilteredList<>(userFile, p -> true);

        //Set up userFile table
        userFileTable.setPlaceholder(new Text("You haven't uploaded any file"));
        userFileTable.setItems(filteredData);
        userFileColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
        userFileColumn2.setCellValueFactory(new PropertyValueFactory<>("size"));
        userFileColumn3.setCellValueFactory(value -> new SimpleStringProperty(value.getValue().getUploadedDate().toString()));
        userFileColumn4.setCellValueFactory(new PropertyValueFactory<>("fileLocation"));
        userFileColumn5.setCellValueFactory(new PropertyValueFactory<>("description"));

        //Set up searchResult table
        searchResultTable.setPlaceholder(new Text("No result"));
        searchResultTable.setItems(searchResult);
        searchResultColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
        searchResultColumn2.setCellValueFactory(new PropertyValueFactory<>("size"));
        searchResultColumn3.setCellValueFactory(value -> new SimpleStringProperty(value.getValue().getUploadedDate().toString()));
        searchResultColumn4.setCellValueFactory(new PropertyValueFactory<>("owner"));
        searchResultColumn5.setCellValueFactory(new PropertyValueFactory<>("description"));

        searchResultTable.setRowFactory(value -> new TableRow<>() {
            @Override
            protected void updateItem(ServerFileData item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && !isEmpty()) {
                            ServerFileData selectedFile = getItem();
                            String fileExtension = "";

                            int lastDotIndex = selectedFile.getName().lastIndexOf('.');
                            if (lastDotIndex > 0 && lastDotIndex < selectedFile.getName().length() - 1) {
                                fileExtension = selectedFile.getName().substring(lastDotIndex);
                            }

                            FileChooser chooser = new FileChooser();
                            chooser.setTitle("Select file to upload");
                            chooser.setInitialFileName(selectedFile.getName());
                            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileExtension, "*" + fileExtension));
                            File savedFile = chooser.showSaveDialog((filePanel.getScene().getWindow()));

                            if (savedFile != null) {
                                Task<Respond> task = sender.requestFile(selectedFile, savedFile);
                                task.setOnSucceeded(ev -> {
                                    finishTask();
                                    if (task.getValue().isSuccess()) {
                                        try {
                                            if (database.existFile(savedFile.getName()) == null)
                                                upload(new ClientFileData(savedFile.getName(), selectedFile.getSize(), selectedFile.getDescription(), savedFile.getAbsolutePath()));
                                            else databaseError("Upload failed, you already have a file with that name");
                                        } catch (SQLException e) {
                                            databaseError(e.getMessage());
                                            e.printStackTrace();
                                        }
                                    } else {
                                        savedFile.delete();
                                        downloadError("File doesn't exist");
                                    }
                                });
                                task.setOnFailed(ev -> {
                                    finishTask();
                                    savedFile.delete();
                                    downloadError(task.getException().getMessage());
                                    task.getException().printStackTrace();
                                });
                                startTask(task);
                            }
                        }
                    });
                    if (item.isOnline()) {
                        setStyle("-fx-text-background-color: green;");
                    } else {
                        setStyle("-fx-text-background-color: red;");
                    }
                }
            }
        });
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
            if (!userPanel.isVisible()) authScreen();
            serverError(task.getException().getMessage());
            task.getException().printStackTrace();
        });

        startTask(task);
    }
    @FXML
    protected void onUploadClick() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file to upload");
        File selectedFile = chooser.showOpenDialog(filePanel.getScene().getWindow());
        if (selectedFile == null) return;

        String filePath = selectedFile.getAbsolutePath();
        String fileExtension = null;

        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            fileExtension = filePath.substring(lastDotIndex);
        }

        // Create a GridPane for the custom dialog layout
        VBox box = new VBox(10);
        box.setPadding(new Insets(10, 10, 10, 10));

        // Create input fields
        TextField nameField = new TextField();
        TextArea descriptionArea = new TextArea();
        Label fileExtensionField = new Label(fileExtension);
        HBox nameBox = new HBox(5);
        nameBox.getChildren().add(nameField);
        nameBox.getChildren().add(fileExtensionField);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        fileExtensionField.setTranslateY(5);

        box.getChildren().add(new Label("File location: " + filePath));
        box.getChildren().add(new Label("File size: " + ClientFileData.formatFileSize(selectedFile.length()) + " (" + selectedFile.length() + ") bytes"));
        box.getChildren().add(new Label("Name:"));
        box.getChildren().add(nameBox);
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
                if (database.existFile(nameField.getText()) != null) {
                    databaseError("File with that name already exist");
                    event.consume();
                }
            } catch (SQLException e) {
                databaseError(e.getMessage());
                e.printStackTrace();
            }
        });

        // Handle the result when the OK button is clicked
        String finalFileExtension = fileExtension;
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                String name = nameField.getText() + finalFileExtension;
                String description = descriptionArea.getText();
                return new String[]{name, description};
            }
            return null;
        });

        if (dialog.showAndWait().isPresent()) {
            ClientFileData file = new ClientFileData(dialog.getResult()[0], selectedFile.length(), dialog.getResult()[1], selectedFile.getAbsolutePath());
            upload(file);
        }
    }
    private void upload(ClientFileData file) {
        try {
            database.insertFileData(file);
            Task<Respond> task = sender.upload(this.username, file);
            task.setOnSucceeded(event -> {
                finishTask();
                if (!task.getValue().isSuccess()) {
                    serverError(task.getValue().getMessage());
                } else {
                    userFile.add(file);
                }
            });
            task.setOnFailed(event -> {
                finishTask();
                try {
                    database.deleteFileData(file.getName());
                } catch (SQLException e) {
                    databaseError(e.getMessage());
                    e.printStackTrace();
                }
                serverError(task.getException().getMessage());
                task.getException().printStackTrace();
            });
            startTask(task);
        }
        catch (SQLException e) {
            databaseError("Cannot insert file data to database");
            e.printStackTrace();
        }
    }
    @FXML
    protected void onLocalSearchClick() {
        filteredData.setPredicate(file -> {
            if (localSearchBar.getText() == null || localSearchBar.getText().isEmpty()) {
                return true;
            }
            String lowerCaseFilter = localSearchBar.getText().toLowerCase();

            return file.getName().toLowerCase().contains(lowerCaseFilter);
        });
    }
    @FXML
    protected void onSearchClick() {
        ArrayList<ServerFileData> fileList = new ArrayList<>();
        Task<Respond> task = sender.search(username, searchBar.getText(), fileList);
        task.setOnSucceeded(event -> {
            finishTask();
            searchResult = FXCollections.observableArrayList(fileList);
            searchResultTable.setItems(searchResult);
        });
        task.setOnFailed(event -> {
            finishTask();
            serverError(task.getException().getMessage());
            task.getException().printStackTrace();
        });
        startTask(task);
    }
    @FXML
    private void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            output.appendText(input.getText() + "\n");
            String result = process(input.getText());
            if (result != null) output.appendText(result + "\n");
            input.clear();
        }
    }
    private String process(String cmd) {
        String[] tokens = cmd.split("_");
        if (tokens.length == 0) return null;
        switch (tokens[0]) {
            case "start":
                if (listener.isStarted()) return "Listener already started";
                try {
                    listener.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    return ("Failed to start listener: " + e.getMessage());
                }
                return "Starting listener...";
            /*
            case "publish":
                if (tokens.length < 3) return "Not enough parameters";
                File file = new File(tokens[2]);
                if (file.exists()) {
                    try {
                        if (database.existFile(tokens[1]) == null) {
                            ClientFileData fileData = new ClientFileData(tokens[1], file.length(), null, file.getAbsolutePath());
                            try {
                                database.insertFileData(fileData);
                                Task<Respond> task = sender.upload(this.username, fileData);
                                task.setOnSucceeded(event -> {
                                    finishTask();
                                    if (!task.getValue().isSuccess()) {
                                        output.appendText(task.getValue().getMessage());
                                    } else {
                                        userFile.add(fileData);
                                    }
                                });
                                task.setOnFailed(event -> {
                                    finishTask();
                                    try {
                                        database.deleteFileData(file.getName());
                                    } catch (SQLException e) {
                                        output.appendText(e.getMessage());
                                        e.printStackTrace();
                                    }
                                    output.appendText(task.getException().getMessage());
                                    task.getException().printStackTrace();
                                });
                                startTask(task);
                                return "Upload successful";
                            }
                            catch (SQLException e) {
                                e.printStackTrace();
                                return "Cannot insert file data to database";
                            }
                        }
                        else return "Upload failed, you already have a file with that name";
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return "Failed to upload file: " + e.getMessage();
                    }
                }
                return "File does not exist";
                */
            //TODO: Add more command

            default:
                return "Invalid command";
        }
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

        try {
            database.setUser(username);
            database.setServerIP(serverIP);
        } catch (SQLException e) {
            databaseError("Failed to saved login information");
            e.printStackTrace();
        }

    }
    private void databaseError(String reason) {
        if (reason == null) reason = "Access denied or connection closed";
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Database error");
        alert.setContentText(reason);
        alert.showAndWait();
    }
    private void serverError(String reason) {
        if (reason == null) reason = "Can't connect to server";
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Server error");
        alert.setContentText(reason);
        alert.showAndWait();
    }
    private void downloadError(String reason) {
        if (reason == null) reason = "Can't connect to host";
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Download error");
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