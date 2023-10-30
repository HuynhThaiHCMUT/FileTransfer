package com.computernetwork.filetransfer.Model;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class NetworkSender {
    private String serverIP;

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public Task<Respond> signUp(String username) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO

                socket.close();
                return null;
            }
        };
    }
    public Task<Respond> login(String username) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO

                socket.close();
                return null;
            }
        };
    }
    public Task<ObservableList<FileData>> search(String query) throws IOException {
        return new Task<ObservableList<FileData>>() {
            @Override
            protected ObservableList<FileData> call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO

                socket.close();
                return null;
            }
        };
    }
    public Task<Respond> upload(FileData file) throws IOException {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO

                socket.close();
                return null;
            }
        };
    }
    public Task<Respond> reportMissingFile(FileData file, String username) throws IOException {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO

                socket.close();
                return null;
            }
        };
    }
}
