package com.augcampos.sserver;

/**
 *
 * @author augcampos
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int p = Integer.valueOf(args[0]);

        new SServer(p);
    }
}
