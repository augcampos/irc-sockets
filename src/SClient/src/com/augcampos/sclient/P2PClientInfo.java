/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.augcampos.sclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author augcampos
 */
public class P2PClientInfo extends Thread {

    private Socket mSocket = null;
    private BufferedReader mIn;
    private PrintWriter mOut;
     private static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    public P2PClientInfo(Socket socket) {
        this.mSocket = socket;
        try {
            mIn = new BufferedReader(new InputStreamReader(this.mSocket.getInputStream()));
            mOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(P2PClientInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendMessageToClient(String aMessage) {
        mOut.println(aMessage);
        mOut.flush();
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = mIn.readLine()) != null) {
                System.out.println("P2P: "+ "(" + df.format(new Date()) + ")  : " + message);
            }
        } catch (IOException ioe) {
            System.err.println("Connection to server broken.");
            ioe.printStackTrace();
        }
    }
}
