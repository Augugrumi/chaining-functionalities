package vfn.testvnfs;

import vfn.BaseVNF;

import java.io.IOException;

/**
 * Simple VNF that append a string in the end of a packet
 */
public class AddStringVNF extends BaseVNF {

    private String toAdd;

    public AddStringVNF(String toAdd, int port) {
        super(port);
        this.toAdd = toAdd;
    }

    public String functionality(String message) {
        return message + toAdd;
    }

    public static void main(String[] args) {
        try {
            new AddStringVNF(args[1], Integer.parseInt(args[0])).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}