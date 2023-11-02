package com.computernetwork.filetransfer.Model;

import javafx.concurrent.Task;

import java.io.*;
import java.net.*;
import java.sql.Date;
import java.util.ArrayList;

public class NetworkSender {
    private String serverIP;

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
            } else if (statusCode == 403 && messageType == 3) { // upload
                response.setMessage("Invalid file name");
            } else if (statusCode == 404 && messageType == 7) { // download
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
        return new Task<>() {
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
        return new Task<>() {
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
    public Task<Respond> search(String username, String query, ArrayList<ServerFileData> returnedFileList) {
        return new Task<>() {
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
                            String username = istream.readUTF();
                            String userIP = istream.readUTF();
                            boolean isOnline = istream.readBoolean();
                            long fileSize = istream.readLong();
                            String filename = istream.readUTF();
                            String fileDescription = istream.readUTF();
                            Date uploadDate = new Date(istream.readLong());

                            ServerFileData serverFileData = new ServerFileData(filename, fileSize, fileDescription, uploadDate, username, userIP, isOnline);
                            returnedFileList.add(serverFileData);
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
        return new Task<>() {
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
    public Task<Respond> reportMissingFile(ServerFileData file) {
        return new Task<>() {
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
                    ostream.writeUTF(file.getOwner());
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
    public Task<Respond> requestFile(ServerFileData file, File savedFile) {
        return new Task<>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(file.getOwnerIP(), 4041);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                short messageType = 7;

                socket.setSoTimeout(5000);
                try {
                    // request
                    ostream.writeShort(messageType);
                    ostream.writeUTF(file.getName());

                    // respond
                    Respond response = buildResponse(istream, messageType);
                    if (response.isSuccess()) {
                        FileOutputStream fileOutputStream = new FileOutputStream(savedFile);
                        long size = istream.readLong();
                        int bytes;
                        byte[] buffer = new byte[4 * 1024];
                        while (size > 0 && (bytes = istream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                            fileOutputStream.write(buffer, 0, bytes);
                            size -= bytes;
                        }
                        fileOutputStream.close();
                    } else {
                        Task<Respond> task = reportMissingFile(file);
                        Thread t = new Thread(task);
                        t.setDaemon(true);
                        t.start();
                    }
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
