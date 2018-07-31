import enchainer.Enchainer;
import vfn.BaseVNF;

import java.io.IOException;

public class Main {

    public static void main(String args[]) {
        String[] chain = new String[3];
        chain[0] = "http://localhost:55561";
        chain[1] = "http://localhost:55560";
        chain[2] = "http://localhost:80";
        Enchainer e = new Enchainer();
        e.chain = chain;


        chain = new String[1];
        chain[0] = "http://localhost:55560";
        final AddStringVNF v2 = new AddStringVNF("v2", 55561, chain);
        chain = new String[1];
        chain[0] = "http://localhost:80";
        final AddStringVNF v1 = new AddStringVNF("v1", 55560, chain);

        new Thread(new Runnable() {
            public void run() {
                try {
                    v1.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                try {
                    v2.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        try {
            e.method(55562);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }
}

class AddStringVNF extends BaseVNF {

    private String toAdd;

    public AddStringVNF(String toAdd, int port, String[] chain) {
        super(port);
        this.toAdd = toAdd;
    }

    public String functionality(String message) {
        //System.out.println("Message received:" + message);
        return message + toAdd;
    }
}
