package vfn.testvnfs;

import vfn.AbsBaseVNF;

import java.io.IOException;

/**
 * Simple VNF that append a string in the end of a packet
 */
public class AddStringVNF extends AbsBaseVNF {

    private String toAdd;

    public AddStringVNF(String toAdd, int port) {
        super(port);
        this.toAdd = toAdd;
    }

    public String functionality(String message) {
        int ind = message.lastIndexOf("\r\n");
        if( ind>=0 )
            message = new StringBuilder(message).replace(ind, ind+1,toAdd + "\r\n").toString();
        return message;
    }

    public static void main(String[] args) {
        new AddStringVNF(args[1], Integer.parseInt(args[0])).execute();
    }
}