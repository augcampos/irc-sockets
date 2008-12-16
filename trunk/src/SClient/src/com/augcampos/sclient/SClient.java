package com.augcampos.sclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 *
 * @author augcampos
 */
public class SClient extends Thread {

    private String mServerHostname;
    private int mServerPort;
    private ServerSocket p2pServer = null;
    private int port = 6223;
    private LinkedList<P2PClientInfo> p2pClients = new LinkedList<P2PClientInfo>();

    public SClient(String hostname, int port) {
        this.mServerHostname = hostname;
        this.mServerPort = port;
        BufferedReader in = null;
        PrintWriter out = null;


        try {
            p2pServer = new ServerSocket(this.port);
            System.out.println("p2pServer started on port " + this.port);
            this.start();
        } catch (IOException se) {
            System.err.println("Can not start listening on port " + this.port);
            se.printStackTrace();
            System.exit(-1);
        }


        try {
            Socket socket = new Socket(this.mServerHostname, this.mServerPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connected to server " + this.mServerHostname + ":" + this.mServerPort);
        } catch (IOException ioe) {
            System.err.println("Can not establish connection to " + this.mServerHostname + ":" + this.mServerPort);
            ioe.printStackTrace();
            System.exit(-1);
        }

        Sender sender = new Sender(out);
        sender.setDaemon(true);
        sender.start();

        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException ioe) {
            System.err.println("Connection to server broken.");
            ioe.printStackTrace();
        }




    }

    
    @Override
    public void run() {

        while (true) {
            try {
                Socket socket = p2pServer.accept();
                P2PClientInfo ci = new P2PClientInfo(socket);

                p2pClients.add(ci);
                ci.start();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}