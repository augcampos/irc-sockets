package com.augcampos.sserver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author augcampos
 */
public class ClientSender extends Thread {

    private Vector mMessageQueue = new Vector();
    private ServerDispatcher mServerDispatcher;
    private ClientInfo mClientInfo;
    private PrintWriter mOut;

    public ClientSender(ClientInfo aClientInfo, ServerDispatcher aServerDispatcher) throws IOException {
        mClientInfo = aClientInfo;
        mServerDispatcher = aServerDispatcher;
        Socket socket = aClientInfo.mSocket;
        mOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        this.sendMessageToClient("Insert Login:");
    }

    public synchronized void sendMessage(String aMessage) {
        mMessageQueue.add(aMessage);
        notify();
    }

    public void sendMessageToClient(String aMessage) {
        mOut.println(aMessage);
        mOut.flush();
    }


    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                String message = getNextMessageFromQueue();
                sendMessageToClient(message);
            }
        } catch (Exception e) {
            // Commuication problem
        }

        mClientInfo.mClientListener.interrupt();
        mServerDispatcher.deleteClient(mClientInfo);
    }

    private synchronized String getNextMessageFromQueue() throws InterruptedException {
        while (mMessageQueue.size() == 0) {
            wait();
        }
        String message = (String) mMessageQueue.get(0);
        mMessageQueue.removeElementAt(0);
        return message;
    }
}
