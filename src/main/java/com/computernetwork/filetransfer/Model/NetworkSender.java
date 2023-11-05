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
    private Response buildResponse(DataInputStream istream, short messageType) throws IOException {
        Response response = new Response(false, null);
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

    public Task<Response> signUp(String username) {
        return new Task<>() {
            @Override
            protected Response call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
                updateMessage("Connecting");

                short messageType = 2;

                socket.setSoTimeout(5000);
                try {
                    // request
                    updateProgress(20, 100);
                    updateMessage("Sending request");
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);

                    // respond
                    updateProgress(50, 100);
                    updateMessage("Reading respond");
                    Response response = buildResponse(istream, messageType);

                    updateProgress(90, 100);
                    updateMessage("Closing Connection");
                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
    public Task<Response> login(String username) {
        return new Task<>() {
            @Override
            protected Response call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
                updateMessage("Connecting");

                short messageType = 1;

                socket.setSoTimeout(5000);
                try {
                    // request
                    updateProgress(20, 100);
                    updateMessage("Sending request");
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);

                    // respond
                    updateProgress(50, 100);
                    updateMessage("Reading respond");
                    Response response = buildResponse(istream, messageType);

                    updateProgress(90, 100);
                    updateMessage("Closing Connection");
                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
    public Task<Response> search(String username, String query, ArrayList<ServerFileData> returnedFileList) {
        return new Task<>() {
            @Override
            protected Response call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
                updateMessage("Connecting");

                short messageType = 4;

                socket.setSoTimeout(5000);
                try {
                    // request
                    updateProgress(20, 100);
                    updateMessage("Sending request");
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);
                    ostream.writeUTF(query);

                    // response
                    updateProgress(50, 100);
                    updateMessage("Reading response");
                    Response response = buildResponse(istream, messageType);

                    if (response.isSuccess()) {
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
                            updateProgress(0.5 + (i/fileCount)*0.4, 1.0);
                        }
                    }

                    updateProgress(90, 100);
                    updateMessage("Closing Connection");
                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
    public Task<Response> upload(String username, ClientFileData file) {
        return new Task<>() {
            @Override
            protected Response call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
                updateMessage("Connecting");

                short messageType = 3;

                socket.setSoTimeout(5000);
                try {
                    // request
                    updateProgress(20, 100);
                    updateMessage("Sending request");
                    ostream.writeShort(messageType);
                    updateProgress(30, 100);
                    ostream.writeUTF(username);
                    updateProgress(40, 100);
                    ostream.writeLong(file.getSize());
                    updateProgress(50, 100);
                    ostream.writeUTF(file.getName());
                    updateProgress(60, 100);
                    ostream.writeUTF(file.getDescription());

                    // respond
                    updateProgress(70, 100);
                    updateMessage("Reading respond");
                    Response response = buildResponse(istream, messageType);

                    updateProgress(90, 100);
                    updateMessage("Closing Connection");
                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
    public Task<Response> checkFile(String username) {
        return new Task<>() {
            @Override
            protected Response call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
                updateMessage("Connecting");

                short messageType = 5;

                socket.setSoTimeout(5000);
                try {
                    // request
                    updateProgress(20, 100);
                    updateMessage("Sending request");
                    ostream.writeShort(messageType);
                    ostream.writeUTF(username);

                    // respond
                    updateProgress(50, 100);
                    updateMessage("Reading respond");
                    Response response = buildResponse(istream, messageType);

                    updateProgress(90, 100);
                    updateMessage("Closing Connection");
                    socket.close();
                    return response;
                } catch (SocketTimeoutException e) {
                    socket.close();
                    throw new SocketTimeoutException("Request timed out");
                }
            }
        };
    }
    public Task<Response> requestFile(ServerFileData file, File savedFile) {
        return new Task<>() {
            @Override
            protected Response call() throws Exception {
                Socket socket = new Socket(file.getOwnerIP(), 4041);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
                updateMessage("Connecting");

                short messageType = 7;

                socket.setSoTimeout(5000);
                try {
                    // request
                    updateProgress(20, 100);
                    updateMessage("Sending request");
                    ostream.writeShort(messageType);
                    ostream.writeUTF(file.getName());

                    // respond
                    updateProgress(50, 100);
                    updateMessage("Reading respond");
                    Response response = buildResponse(istream, messageType);
                    if (response.isSuccess()) {
                        updateProgress(100, 100);
                        updateMessage("File found, downloading");
                        FileOutputStream fileOutputStream = new FileOutputStream(savedFile);
                        updateProgress(0.0, 1.0);
                        long size = istream.readLong();
                        long maxSize = size;
                        int bytes;
                        byte[] buffer = new byte[4 * 1024];
                        while (size > 0 && (bytes = istream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                            fileOutputStream.write(buffer, 0, bytes);
                            size -= bytes;
                            updateProgress(1 - ((double) size /maxSize), 1.0);
                        }
                        updateMessage("Saving file");
                        fileOutputStream.close();
                    } else {
                        Task<Response> task = checkFile(file.getOwner());
                        Thread t = new Thread(task);
                        t.setDaemon(true);
                        t.start();
                    }

                    updateProgress(90, 100);
                    updateMessage("Closing Connection");
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
