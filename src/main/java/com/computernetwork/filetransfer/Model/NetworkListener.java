package com.computernetwork.filetransfer.Model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkListener {
    private ServerSocket socket;
    private DataInputStream istream;
    private DataOutputStream ostream;

    public NetworkListener(String yourIP) throws IOException {
        InetAddress address = InetAddress.getByName(yourIP);
        socket = new ServerSocket(4040, 0, address);
        Socket client = socket.accept();
        istream = new DataInputStream(client.getInputStream());
        ostream = new DataOutputStream(client.getOutputStream());
    }
}
