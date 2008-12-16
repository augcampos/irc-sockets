/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.augcampos.sclient;

/**
 *
 * @author augcampos
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String h = args[0];
        int p = Integer.valueOf(args[1]);

       SClient sc = new SClient(h, p);
    }

}
