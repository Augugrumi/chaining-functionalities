package vfn;

import enchainer.Enchainer;
import httputils.AbsBaseServer;
import httputils.MessageWrapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static httputils.MessageWrapper.wrapMessage;

/**
 * Abstract class that implements VNF interface. Use TEMPLATE METHOD pattern to make new VNF implementation works with
 * the other classes
 */
public abstract class BaseVNF extends AbsBaseServer implements VNF {

    /**
     * Logging utility field
     */
    private static final Logger LOGGER = Logger.getLogger(BaseVNF.class.getName());

    /**
     * Port on which the VNF waits for messages
     */
    private final int port;

    /**
     * Constructor of the class
     * @param port Port on which the VNF waits for messages, must be in range 0-65535
     */
    public BaseVNF(int port) {
        // TODO think a better check
        if (!(port >= 0 && port <= 65535))
            throw new RuntimeException("Port number must be in range 0-65535");
        this.port = port;
    }

    /**
     * Method to make VNF waiting for message (use TEMPLATE METHOD)
     * @throws IOException if connection breaks
     */
    public void execute() throws IOException {
        ServerSocket ss = new ServerSocket(port);

        // To execute all runnable -> VNFs functionalities
        ExecutorService executor = Executors.newCachedThreadPool();

        for(;;) {
            String postData = receive(ss).getPostData();

            executor.execute(() -> {
                try {

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

                    System.out.println("message: " + wrapMessage(messageModified, chain));
                    send(wrapMessage(messageModified, chain), next);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}