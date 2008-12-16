package com.augcampos.sserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author augcampos
 */
public class ClientListener extends Thread {

    private ServerDispatcher mServerDispatcher;
    private ClientInfo mClientInfo;
    private BufferedReader mIn;

    public ClientListener(ClientInfo aClientInfo, ServerDispatcher aServerDispatcher) throws IOException {
        mClientInfo = aClientInfo;
        mServerDispatcher = aServerDispatcher;
        Socket socket = aClientInfo.mSocket;
        mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                String message = mIn.readLine();
                if (message == null) {
                    break;
                }


                if (mClientInfo.mLogin == null) {

                    if (mServerDispatcher.isValiLogin(message)) {
                        mClientInfo.mLogin = message;

                        sendUsersList();

                        mServerDispatcher.sendMessageToAllClients("+User :" + mClientInfo.mLogin);
                    } else {
                        mClientInfo.mClientSender.sendMessageToClient("Invalid Login!! Give a new one:");
                    }

                } else {
                    if (message.equalsIgnoreCase("/help")) {
                        StringBuilder sb = new StringBuilder("SServer Help\n");
                        sb.append("Commands:\n");
                        sb.append("/help                  -> This message\n");
                        sb.append("/quit                  -> Terminates connection\n");
                        sb.append("/users                 -> Get user list\n");
                        sb.append("/to <user> <message>   -> Send the private message to user\n");
                        mClientInfo.mClientSender.sendMessageToClient(sb.toString());
                    } else if (message.equalsIgnoreCase("/quit")) {
                        mServerDispatcher.deleteClient(mClientInfo);
                        mServerDispatcher.sendMessageToAllClients("-User :" + mClientInfo.mLogin);
                        mClientInfo.mSocket.close();
                    } else if (message.equalsIgnoreCase("/users")) {
                        sendUsersList();
                    } else if (message.startsWith("/to ")) {
                        message = message.replace("/to ", "");
                        String login = message.split(" ")[0];
                        message = message.replace(login + " ", "");
                        mServerDispatcher.sendMessageToLogin(mClientInfo, login, message);

                    } else {
                        mServerDispatcher.dispatchMessage(mClientInfo, message);
                    }
                }
            }
        } catch (IOException ioex) {
            // Problem reading from socket (communication is broken)
        }

        mClientInfo.mClientSender.interrupt();
        mServerDispatcher.deleteClient(mClientInfo);
    }

    private void sendUsersList() {
        mClientInfo.mClientSender.sendMessageToClient("You are known as " + mClientInfo.mLogin);
        mClientInfo.mClientSender.sendMessageToClient(" ------ Register Users ------");
        mClientInfo.mClientSender.sendMessageToClient(mServerDispatcher.getClientsList());
        mClientInfo.mClientSender.sendMessageToClient(" ------ Register Users ------");
    }
}
