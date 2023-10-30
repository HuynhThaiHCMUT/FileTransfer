package com.computernetwork.filetransfer;

import com.computernetwork.filetransfer.Model.LocalDatabase;
import com.computernetwork.filetransfer.Model.NetworkSender;
import com.computernetwork.filetransfer.Model.Respond;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;


public class MainController {
    private LocalDatabase database;
    private NetworkSender sender;
    private String username;
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
    public void initialize() {
        username = null;
        serverIP = null;
        sender = new NetworkSender();
        try {
            database = new LocalDatabase();
            username = database.getUser();
            serverIP = database.getServerIP();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setContentText("Can't connect to database");
            alert.showAndWait();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        if (username == null || serverIP == null) {
            authScreen();
        } else {
            auth(false);
        }

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
    protected void onFileClick() {
        switchToPane(filePanel, fileTab);
    }
    @FXML
    protected void onSearchClick() {
        switchToPane(searchPanel, searchTab);
    }
    @FXML
    protected void onTerminalClick() {
        switchToPane(terminalPanel, terminalTab);
    }
    @FXML
    protected void onUserClick() {
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
    private void auth(boolean signUp) {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server error");
            alert.setContentText("Cannot connect to server");
            alert.showAndWait();
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