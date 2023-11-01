package com.computernetwork.filetransfer.Model;

import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.sql.Date;
import java.util.ArrayList;

public class NetworkSender {
    private String serverIP;

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    //helper function
    private Respond buildResponse(DataInputStream istream, short messageType) throws IOException {
        Respond response = new Respond(false, null);
        short statusCode = istream.readShort();

        if (statusCode == 200) {
            response.setSuccess(true);
            response.setMessage("Success");
        } else {
            response.setSuccess(false);
            if (statusCode == 400) {
                response.setMessage("Invalid message type");
            } else if (statusCode == 401) {
                if (messageType == 2) { // sign up
                    response.setMessage("Username already exists");
                } else if (messageType == 1) { // login
                    response.setMessage("Username does not exist");
                } else { // others
                    response.setMessage("Username does not match the client's active user");
                }
            } else if (statusCode == 402 && messageType == 3) { // upload
                response.setMessage("Invalid file size");
            } else if (statusCode == 403 && messageType == 3) {
                response.setMessage("Invalid file name");
            } else if (statusCode == 404 && messageType == 4) { // search TODO
                response.setMessage("File not found");
            } else if (statusCode == 500) {
                response.setMessage("Internal server error");
            } else {
                response.setMessage("Wrong status code in response");
            }
        }

        return response;
    }

    public Task<Respond> signUp(String username) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                short messageType = 2;

                socket.setSoTimeout(5000);
                try {
                    // request
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);

                    // respond
                    Respond response = buildResponse(istream, messageType);

                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
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

                short messageType = 1;

                socket.setSoTimeout(5000);
                try {
                    // request
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);

                    // respond
                    Respond response = buildResponse(istream, messageType);

                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
    public Task<Respond> search(String username, String query, ArrayList<ServerFileData> fileList) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                short messageType = 4;

                socket.setSoTimeout(5000);
                try {
                    // request
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);
                    ostream.writeUTF(query);

                    // respond
                    Respond respond = buildResponse(istream, messageType);

                    if (respond.isSuccess()) {
                        short fileCount = istream.readShort();

                        for (short i = 0; i < fileCount; ++i) {
                            String username = istream.readUTF(istream);
                            long fileSize = istream.readLong();
                            String filename = istream.readUTF(istream);
                            String fileDescription = istream.readUTF(istream);
                            Date uploadDate = new Date(istream.readLong());

                            ServerFileData serverFileData = new ServerFileData(filename, fileSize, fileDescription, uploadDate, username);
                            fileList.add(serverFileData);
                        }
                    }

                    socket.close();
                    return respond;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
    public Task<Respond> upload(String username, ClientFileData file) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                short messageType = 3;

                socket.setSoTimeout(5000);
                try {
                    // request
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);
                    ostream.writeLong(file.getSize());
                    ostream.writeUTF(file.getName());
                    ostream.writeUTF(file.getDescription());

                    // respond
                    Respond response = buildResponse(istream, messageType);

                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
    public Task<Respond> reportMissingFile(String username, ClientFileData file) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                short messageType = 5;

                socket.setSoTimeout(5000);
                try {
                    // request
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);
                    ostream.writeUTF(file.getName());

                    // respond
                    Respond response = buildResponse(istream, messageType);

                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
}
