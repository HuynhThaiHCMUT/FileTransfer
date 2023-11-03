package com.computernetwork.filetransfer.Model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkListener {
    private final LocalDatabase database;
    private final TextArea output;
    private ServerSocket socket;
    private boolean started;

    public NetworkListener(LocalDatabase database, TextArea output) {
        this.database = database;
        this.output = output;
        started = false;
    }
    public void start() {
        try {
            socket = new ServerSocket(4041);
        } catch (IOException e) {
            e.printStackTrace();
            output.appendText("Failed to start listener: " + e.getMessage() +"\n");
        }
        Task<String> startTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                started = true;
                Platform.runLater(() -> output.appendText("Listener started, waiting for connection\n"));
                while (!isCancelled()) {
                    Socket newClient = socket.accept();
                    ListenerTask task = new ListenerTask(newClient);
                    Thread t = new Thread(task);
                    t.setDaemon(true);
                    t.start();
                }
                return "Listener stopped\n";
            }
            @Override
            protected void succeeded() {
                try {
                    started = false;
                    socket.close();
                } catch (IOException e) {
                    output.appendText("Error while closing listener: " + e.getMessage() + "\n");
                    e.printStackTrace();
                }
                output.appendText(getValue());
            }
            @Override
            protected void failed() {
                try {
                    started = false;
                    socket.close();
                } catch (IOException e) {
                    output.appendText("Error while closing listener: " + e.getMessage() + "\n");
                    e.printStackTrace();
                }
                output.appendText("Listener caught an exception: " + getException().getMessage() + "\n");
                getException().printStackTrace();
            }
        };
        Thread startThread = new Thread(startTask);
        startThread.setDaemon(true);
        startThread.start();
    }
    class ListenerTask extends Task<String> {
        private final Socket client;
        private final String clientIP;

        public ListenerTask(Socket newClient) {
            client = newClient;
            clientIP = client.getInetAddress().getHostAddress();
        }
        @Override
        protected String call() throws Exception {
            DataInputStream istream = new DataInputStream(client.getInputStream());
            DataOutputStream ostream = new DataOutputStream(client.getOutputStream());

            short msgType = istream.readShort();
            String username = istream.readUTF();

            switch (msgType) {
                case 0:
                    if (username.equals(database.getUser())) {
                        ostream.writeShort(200);
                        return clientIP + " sent a ping request for " + username +"\n" +
                                "Correct user, successful ping returned\n";
                    } else {
                        ostream.writeShort(401);
                        return clientIP + " sent a ping request for " + username +"\n" +
                                "Incorrect user, failed ping returned\n";
                    }
                case 6:
                    if (username.equals(database.getUser())) {
                        ostream.writeShort(200);
                        ArrayList<ClientFileData> result = database.getFileData();
                        ostream.writeShort(result.size());
                        for (ClientFileData file: result) {
                            ostream.writeLong(file.getSize());
                            ostream.writeUTF(file.getName());
                            ostream.writeUTF(file.getDescription());
                            ostream.writeUTF(file.getFileLocation());
                        }
                        return clientIP + " sent a discover request for " + username +"\n" +
                                "Correct user, returned " + result.size() +" results\n";
                    } else {
                        ostream.writeShort(401);
                        return clientIP + " sent a discover request for " + username +"\n" +
                                "Incorrect user, request denied\n";
                    }
                case 7:
                    //username is now filename
                    ClientFileData fileData = database.existFile(username);
                    if (fileData != null) {
                        File file = new File(fileData.getFileLocation());
                        if (file.canRead()) {
                            ostream.writeShort(200);
                            FileInputStream fileInputStream = new FileInputStream(file);
                            ostream.writeLong(file.length());
                            byte[] buffer = new byte[4 * 1024];
                            int bytes;
                            while ((bytes = fileInputStream.read(buffer)) != -1) {
                                ostream.write(buffer, 0, bytes);
                            }
                            fileInputStream.close();
                            return clientIP + " sent a download request for file " + username +"\n" +
                                    "File found, transferred file to requesting client\n";
                        }
                    }
                    ostream.writeShort(404);
                    return clientIP + " sent a download request for file " + username +"\n" +
                            "File not found, request denied\n";
                default:
                    ostream.writeShort(400);
                    ostream.writeShort(msgType);
                    return clientIP + " sent an invalid request (msgType = " + msgType +") as " + username + "\n";
            }
        }
        @Override
        protected void succeeded() {
            try {
                client.close();
            } catch (IOException e) {
                output.appendText("Error while closing client connection: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
            output.appendText(getValue());
        }
        @Override
        protected void failed() {
            try {
                client.close();
            } catch (IOException e) {
                output.appendText("Error while closing client connection: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
            output.appendText("Error while handling request from " + clientIP + ": " + getException().getMessage() + "\n");
            getException().printStackTrace();
        }
    }
    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            output.appendText("Error while closing socket: " + e.getMessage());
        }
    }

    public boolean isStarted() {
        return started;
    }
}
