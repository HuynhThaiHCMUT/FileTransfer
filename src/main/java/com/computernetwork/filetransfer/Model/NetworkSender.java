package com.computernetwork.filetransfer.Model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class NetworkSender {
    private Socket socket;
    private DataInputStream istream;
    private DataOutputStream ostream;

    public NetworkSender(String serverIP) throws IOException {
        socket = new Socket(serverIP, 4040);
        istream = new DataInputStream(socket.getInputStream());
        ostream = new DataOutputStream(socket.getOutputStream());
    }
    public Respond register(String username) {
        //TODO
        return null;
    }
    public Respond login(String username) {
        //TODO
        return null;
    }
    public ArrayList<FileData> search(String query) {
        //TODO
        return null;
    }
    public Respond upload(FileData file) {
        //TODO
        return null;
    }
    public Respond reportMissingFile(FileData file, String username) {
        //TODO
        return null;
    }
}
