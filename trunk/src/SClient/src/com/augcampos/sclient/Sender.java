package com.augcampos.sclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 *
 * @author augcampos
 */
public class Sender extends Thread {

    private PrintWriter mOut;

    public Sender(PrintWriter aOut) {
        mOut = aOut;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (!isInterrupted()) {
                String message = in.readLine();
                mOut.println(message);
                mOut.flush();
            }
        } catch (IOException ioe) {
            // Communication is broken
        }
    }
}
