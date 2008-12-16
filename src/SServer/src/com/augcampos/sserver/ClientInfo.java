package com.augcampos.sserver;

import java.net.Socket;
import java.util.Date;

/**
 *
 * @author augcampos
 */
public class ClientInfo {
    public Socket mSocket = null;
    public ClientListener mClientListener = null;
    public ClientSender mClientSender = null;
    public String mLogin = null;
    public Date in = new Date();
}
