package com.augcampos.sserver;
/**
 *
 * @author augcampos
 */
import java.net.*;
import java.io.*;

public class SServer {

    private ServerSocket serverSocket = null;
    private int port;
    private ServerDispatcher serverDispatcher = new ServerDispatcher();

    public SServer(int port) {
        this.port = port;
        // Open server socket for listening
        try {
            serverSocket = new ServerSocket(this.port);
            System.out.println("ChatServer started on port " + this.port);
        } catch (IOException se) {
            System.err.println("Can not start listening on port " + this.port);
            se.printStackTrace();
            System.exit(-1);
        }
        this.start();
    }

    private void start() {
        // Start ServerDispatcher thread
        serverDispatcher.start();
        // Accept and handle client connections
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientInfo clientInfo = new ClientInfo();
                clientInfo.mSocket = socket;
                ClientListener clientListener = new ClientListener(clientInfo, serverDispatcher);
                ClientSender clientSender = new ClientSender(clientInfo, serverDispatcher);
                clientInfo.mClientListener = clientListener;
                clientInfo.mClientSender = clientSender;
                clientListener.start();
                clientSender.start();
                serverDispatcher.addClient(clientInfo);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
