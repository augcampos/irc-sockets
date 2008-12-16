package com.augcampos.sserver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

/**
 *
 * @author augcampos
 */
public class ServerDispatcher extends Thread {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    private Vector mMessageQueue = new Vector();
    private LinkedList<ClientInfo> mClients = new LinkedList<ClientInfo>();


    public synchronized void addClient(ClientInfo aClientInfo) {
        mClients.add(aClientInfo);
    }

    public synchronized void deleteClient(ClientInfo aClientInfo) {
        int clientIndex = mClients.indexOf(aClientInfo);
        if (clientIndex != -1) {

            if (aClientInfo.mLogin != null) {
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter("log.txt", true));
                    out.write(aClientInfo.mLogin + " -> " + df.format(aClientInfo.in) + " <---> " + df.format(new Date()) + "\n");
                    out.close();
                } catch (IOException se) {
                    System.err.println("Errot Writing Log");
                    se.printStackTrace();
                }
            }
            mClients.remove(clientIndex);
        }


    }

    public synchronized boolean isValiLogin(String login) {
        for (ClientInfo clientInfo : mClients) {
            if (clientInfo.mLogin != null && clientInfo.mLogin.equalsIgnoreCase(login)) {
                return false;
            }
        }
        return true;
    }

    public synchronized String getClientsList() {
        StringBuilder sb = new StringBuilder();

        for (ClientInfo c : mClients) {
            sb.append(c.mLogin).append("\n");
        }

        return sb.toString();
    }

    public synchronized void dispatchMessage(ClientInfo aClientInfo, String aMessage) {
        aMessage = aClientInfo.mLogin + "(" + df.format(new Date()) + ")  : " + aMessage;
        mMessageQueue.add(aMessage);
        notify();
    }


    private synchronized String getNextMessageFromQueue() throws InterruptedException {
        while (mMessageQueue.size() == 0) {
            wait();
        }
        String message = (String) mMessageQueue.get(0);
        mMessageQueue.removeElementAt(0);
        return message;
    }


    public synchronized void sendMessageToAllClients(String aMessage) {
        for (ClientInfo clientInfo : mClients) {
            clientInfo.mClientSender.sendMessage(aMessage);
        }
    }

    public synchronized void sendMessageToLogin(ClientInfo aClientInfo, String login, String aMessage) {
        ClientInfo ci = getUser(login);
        if (ci != null) {
            aMessage = aClientInfo.mLogin + "(" + df.format(new Date()) + ")  : " + aMessage;
            ci.mClientSender.sendMessage(aMessage);
        }

    }

    public synchronized ClientInfo getUser(String login) {
        for (ClientInfo clientInfo : mClients) {
            if (clientInfo.mLogin != null && clientInfo.mLogin.equalsIgnoreCase(login)) {
                return clientInfo;
            }
        }
        return null;
    }


    @Override
    public void run() {
        try {
            while (true) {
                String message = getNextMessageFromQueue();
                sendMessageToAllClients(message);
            }
        } catch (InterruptedException ie) {
            // Thread interrupted. Stop its execution
        }
    }
}
