package com.computernetwork.filetransfer;

import com.computernetwork.filetransfer.Model.LocalDatabase;
import com.computernetwork.filetransfer.Model.NetworkSender;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public class MainController {
    private LocalDatabase database;
    private NetworkSender sender;
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
    public void initialize() {
        database = new LocalDatabase();

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

    public void onClose() {
        database.close();
    }
}