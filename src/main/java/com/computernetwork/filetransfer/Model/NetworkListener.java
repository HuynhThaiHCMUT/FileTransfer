package com.computernetwork.filetransfer.Model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetworkListener {
    private LocalDatabase database;
    private TextArea output;
    private NetworkSender sender;
    private boolean started;

    public NetworkListener(LocalDatabase database, TextArea output, NetworkSender sender) {
        this.database = database;
        this.output = output;
        this.sender = sender;
        started = false;
    }
    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(4041);
        Task<String> startTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                started = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        output.appendText("Listener started, waiting for connection\n");
                    }
                });
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
                }
                output.appendText("Listener caught an exception: " + getException().getMessage() + "\n");
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
                                "Correct user, ping returned\n";
                    } else {
                        ostream.writeShort(401);
                        return clientIP + " sent a ping request for " + username +"\n" +
                                "Incorrect user, ping returned\n";
                    }
                case 6:
                    if (username.equals(database.getUser())) {
                        ostream.writeShort(200);
                        ArrayList<ClientFileData> result = database.checkFile();
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
            }
            output.appendText(getValue());
        }
        @Override
        protected void failed() {
            try {
                client.close();
            } catch (IOException e) {
                output.appendText("Error while closing client connection: " + e.getMessage() + "\n");
            }
            output.appendText("Error while handling request from " + clientIP + ": " + getException().getMessage() + "\n");
        }
    }

    public boolean isStarted() {
        return started;
    }
}
