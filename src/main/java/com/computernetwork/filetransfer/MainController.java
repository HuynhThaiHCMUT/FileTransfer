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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
            errorDialog("Database", "Can't connect to database");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        sender = new NetworkSender();
        listener = new NetworkListener(database, output);
        listener.start();
        output.appendText("Type help for a list of command\n");

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
                            chooser.setTitle("Save as");
                            chooser.setInitialFileName(selectedFile.getName());
                            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileExtension, "*" + fileExtension));
                            File savedFile = chooser.showSaveDialog((filePanel.getScene().getWindow()));

                            if (savedFile != null) {
                                download(selectedFile, savedFile);
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
        Task<Response> task = signUp ? sender.signUp(username) : sender.login(username);

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
            errorDialog("Server", task.getException().getMessage());
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
                if (database.existFile(nameField.getText() + fileExtensionField.getText()) != null) {
                    errorDialog("Database", "File with that name already exist");
                    event.consume();
                }
            } catch (SQLException e) {
                errorDialog("Database", e.getMessage());
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
            Task<Response> task = sender.upload(this.username, file);
            task.setOnSucceeded(event -> {
                finishTask();
                if (!task.getValue().isSuccess()) {
                    errorDialog("Server", task.getValue().getMessage());
                } else {
                    userFile.add(file);
                    output.appendText("Upload succeeded\n");
                }
            });
            task.setOnFailed(event -> {
                finishTask();
                try {
                    database.deleteFileData(file.getName());
                } catch (SQLException e) {
                    errorDialog("Database", e.getMessage());
                    e.printStackTrace();
                }
                errorDialog("Server", task.getException().getMessage());
                task.getException().printStackTrace();
            });
            startTask(task);
        } catch (SQLException e) {
            errorDialog("Database", "Cannot insert file data to database");
            e.printStackTrace();
        }
    }
    private void download(ServerFileData selectedFile, File savedFile) {
        Task<Response> task = sender.requestFile(selectedFile, savedFile);
        task.setOnSucceeded(ev -> {
            finishTask();
            if (task.getValue().isSuccess()) {
                try {
                    output.appendText("Download succeeded\n");
                    if (database.existFile(savedFile.getName()) == null)
                        upload(new ClientFileData(savedFile.getName(), selectedFile.getSize(), selectedFile.getDescription(), savedFile.getAbsolutePath()));
                    else errorDialog("Database", "Upload failed, you already uploaded a file with that name");
                } catch (SQLException e) {
                    errorDialog("Database", e.getMessage());
                    e.printStackTrace();
                }
            } else {
                savedFile.delete();
                errorDialog("Download", "File doesn't exist");
            }
        });
        task.setOnFailed(ev -> {
            finishTask();
            savedFile.delete();
            errorDialog("Download", task.getException().getMessage());
            task.getException().printStackTrace();
        });
        output.appendText("Downloading " + selectedFile.getName() + " from " + selectedFile.getOwner() + " as " + savedFile.getAbsolutePath() + "\n");
        startTask(task);
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
        Task<Response> task = sender.search(username, searchBar.getText(), fileList);
        task.setOnSucceeded(event -> {
            finishTask();
            searchResult = FXCollections.observableArrayList(fileList);
            searchResultTable.setItems(searchResult);
            if (searchResult == null || searchResult.isEmpty()) output.appendText("No file found\n");
            else {
                output.appendText("File that match result: \n");
                for (int i = 0; i < searchResult.size(); i++) {
                    output.appendText(i +
                            " " + searchResult.get(i).getName() +
                            " " + searchResult.get(i).getSize() +
                            " " + searchResult.get(i).getDescription() +
                            " " + searchResult.get(i).getUploadedDate().toString() +
                            " " + searchResult.get(i).getOwner() +
                            " " + (searchResult.get(i).isOnline() ? "Online" : "Offline") +"\n");
                }
            }
        });
        task.setOnFailed(event -> {
            finishTask();
            errorDialog("Server", task.getException().getMessage());
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
        ArrayList<String> tokens = splitTokens(cmd);
        if (tokens.isEmpty()) return null;
        switch (tokens.get(0)) {
            case "help":
                return """
                        Command format: command "parameter1" "parameter2" ...
                        List of command:
                        > start: start the network listener
                        > stop: stop the network listener
                        > clear: clear the command-line output
                        > publish "local name" "upload name" "description": upload a local file at <local name> as <upload name> to server
                        > fetch "file name": fetch a list of file available on server with name similar to <file name>
                        > download "index" "save location": download the file with the index <index> return from the fetch command to <save location> on your computer""";
            case "start":
                if (listener.isStarted()) return "Listener already started";
                listener.start();
                return "Starting listener...";
            case "clear":
                output.clear();
                return null;
            case "publish":
                if (tokens.size() != 3 && tokens.size() != 4) return "Incorrect parameter count";
                File file = new File(tokens.get(1));
                if (file.exists()) {
                    try {
                        if (database.existFile(tokens.get(2)) == null) {
                            ClientFileData fileData = new ClientFileData(tokens.get(2), file.length(), (tokens.size() == 3 ? "" : tokens.get(3)), file.getAbsolutePath());
                            upload(fileData);
                            return null;
                        }
                        return "Upload failed, you already have a file with that name";
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return "Failed to upload file: " + e.getMessage();
                    }
                }
                return "File does not exist";
            case "fetch":
                if (tokens.size() != 2) return "Incorrect parameter count";
                searchBar.setText(tokens.get(1));
                onSearchClick();
                return null;
            case "download":
                if (tokens.size() != 3) return "Incorrect parameter count";
                try {
                    int i = Integer.parseInt(tokens.get(1));
                    File savedFile = new File(tokens.get(2));
                    if (!savedFile.createNewFile()) return "Can't save file as " + tokens.get(2);
                    download(searchResult.get(i), savedFile);
                    return null;
                } catch (NumberFormatException e) {
                    return "File index is not a number";
                } catch (IOException e) {
                    return "Can not save file as " + tokens.get(2);
                } catch (IndexOutOfBoundsException e) {
                    return "Index out of bound";
                }
            case "stop":
                listener.stop();
                return "Listener stopped";
            default:
                return "Invalid command: " + tokens.get(0);
        }
    }

    public static ArrayList<String> splitTokens(String input) {
        ArrayList<String> tokens = new ArrayList<>();

        // Regular expression to match tokens with or without double quotes
        Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String token = matcher.group(1);

            // Remove double quotes if present
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }

            tokens.add(token);
        }
        return tokens;
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
            errorDialog("Database","Failed to saved login information");
            e.printStackTrace();
        }

    }
    private void errorDialog(String errorType ,String reason) {
        if (reason == null) reason = "Unidentified error";
        output.appendText(errorType + " error: " + reason + "\n");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(errorType + " error");
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