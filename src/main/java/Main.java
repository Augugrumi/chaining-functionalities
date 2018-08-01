import enchainer.Enchainer;
import vfn.BaseVNF;
import vfn.testvnfs.AddStringVNF;

import java.io.IOException;

public class Main {

    public static void main(String args[]) {
        String[] chain = new String[3];
        chain[0] = "http://localhost:55561";
        chain[1] = "http://localhost:55560";
        chain[2] = "http://localhost:80";
        Enchainer e = new Enchainer(55560, chain);


        chain = new String[1];
        chain[0] = "http://localhost:55560";
        final AddStringVNF v2 = new AddStringVNF("v2", 55561);
        chain = new String[1];
        chain[0] = "http://localhost:80";
        final AddStringVNF v1 = new AddStringVNF("v1", 55560);

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
            e.execute();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }
}