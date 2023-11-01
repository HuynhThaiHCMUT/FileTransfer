package com.computernetwork.filetransfer.Model;

import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.nio.charset.Charset;
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

    // helper functions -------------------------------------------------------------------------
    private static void writeString(DataOutputStream ostream, String string) throws Exception {
        ostream.writeShort((short)string.length());
        ostream.write(string.getBytes(Charset.forName("UTF-8")));
    }

    private static String readString(DataInputStream istream) throws Exception {
        short length = istream.readShort();
        byte[] byteArray = new byte[length];
        istream.readFully(byteArray);
        return new String(byteArray);
    }

    private static Respond buildResponse(DataInputStream istream, short messageType) throws Exception {
        Respond response = new Respond(false, null);
        short statusCode = istream.readShort();

        if (statusCode == 200) {
            response.setSuccess(true);
            response.setMessage("Success");
        } else {
            response.setSuccess(false);

            if (statusCode == 400) {
                response.setMessage("Invalid username length");
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
                response.setMessage("Invalid file name length");
            } else if (statusCode == 404 && messageType == 4) { // search
                response.setMessage("File not found");
            } else if (statusCode == 405 && messageType == 3) { // upload
                response.setMessage("Invalid file description length");
            } else if (statusCode == 406 && messageType == 4) { // search
                response.setMessage("Invalid query length");
            } else if (statusCode == 500) {
                response.setMessage("Internal server error");
            } else {
                response.setMessage("Wrong status code in response");
            }
        }

        return response;
    }
    // ------------------------------------------------------------------------------------------

    public Task<Respond> signUp(String username) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO
                short messageType = 2;

                // request
                ostream.writeShort(messageType);
                NetworkSender.writeString(ostream, username);

                // respond
                Respond response = NetworkSender.buildResponse(istream, messageType);

                socket.close();
                return response;
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
                short messageType = 1;

                // request
                ostream.writeShort(messageType);
                NetworkSender.writeString(ostream, username);

                // respond
                Respond response = NetworkSender.buildResponse(istream, messageType);
                
                socket.close();
                return response;
            }
        };
    }
    public Task<ArrayList<ServerFileData>> search(String username, String query) {
        return new Task<ArrayList<ServerFileData>>() {
            @Override
            protected ArrayList<ServerFileData> call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO
                short messageType = 4;

                // request
                ostream.writeShort(messageType);
                NetworkSender.writeString(ostream, username);
                NetworkSender.writeString(ostream, query);

                // respond
                ArrayList<ServerFileData> fileList = new ArrayList<ServerFileData>();
                short statusCode = istream.readShort();
                
                if (statusCode == 200) {                    
                    istream.readShort(); // msgtype
                    short fileCount = istream.readShort();
                    
                    for (short i = 0; i < fileCount; ++i) {
                        String username = NetworkSender.readString(istream);
                        long fileSize = istream.readLong();
                        String filename = NetworkSender.readString(istream);
                        String fileDescription = NetworkSender.readString(istream);
                        Date uploadDate = new Date(istream.readLong());

                        ServerFileData serverFileData = new ServerFileData(filename, fileSize, fileDescription, uploadDate, username);
                        fileList.add(serverFileData);
                    }
                }
                // else fileList is empty

                socket.close();
                return fileList;
            }
        };
    }
    public Task<Respond> upload(String username, FileData file) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO
                short messageType = 3;

                // request
                ostream.writeShort(messageType);
                NetworkSender.writeString(ostream, username);
                ostream.writeLong(file.getSize());
                NetworkSender.writeString(ostream, file.getName());
                NetworkSender.writeString(ostream, file.getDescription());  

                // respond
                Respond response = NetworkSender.buildResponse(istream, messageType);

                socket.close();
                return response;
            }
        };
    }
    public Task<Respond> reportMissingFile(String username, FileData file) {
        return new Task<Respond>() {
            @Override
            protected Respond call() throws Exception {
                Socket socket = new Socket(serverIP, 4040);
                DataInputStream istream = new DataInputStream(socket.getInputStream());
                DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());

                //TODO
                short messageType = 5;

                // request
                ostream.writeShort(messageType);
                NetworkSender.writeString(ostream, username);
                NetworkSender.writeString(ostream, file.getName());

                // respond
                Respond response = NetworkSender.buildResponse(istream, messageType);

                socket.close();
                return response;
            }
        };
    }
}
