package vfn;

import enchainer.Enchainer;
import httputils.AbsBaseServer;
import httputils.MessageWrapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.util.logging.Logger;

public abstract class BaseVNF extends AbsBaseServer implements VNF {

    private static final Logger LOGGER = Logger.getLogger(BaseVNF.class.getName());
    private int port;
    private String[] chain;

    public BaseVNF(int port, String[] chain) {
        // TODO check port number
        this.port = port;
        // TODO check chain
        this.chain = chain;
    }

    public void execute() throws IOException {
        ServerSocket ss = new ServerSocket(port);

        for(;;) {
            try {
                String postData = receive(ss).getPostData();

                System.out.println(postData);

                JSONObject jsonData = new JSONObject(postData);

                String message = (String) jsonData.get("message");
                String messageModified = functionality(message);

                // TODO do something better
                String next = "http://localhost:80";
                String[] chain = new String[0];
                JSONArray arr = jsonData.getJSONArray("chain");
                if (arr.length() > 1) {
                    chain = new String[arr.length() - 1];
                    next = arr.getString(1);
                    for (int i = 1; i < arr.length(); i++) {
                        chain[i - 1] = arr.getString(i);
                    }
                }

                System.out.println("message: " + new Enchainer().wrapMessage(messageModified, chain));
                send(MessageWrapper.wrapMessage(messageModified, chain), next);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}