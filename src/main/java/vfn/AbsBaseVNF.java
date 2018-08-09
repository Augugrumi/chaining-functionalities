package vfn;

import httputils.AbsBaseServer;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static httputils.MessageWrapper.unwrapChain;
import static httputils.MessageWrapper.unwrapMessage;
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
                    // retrieve the original message and perform modification
                    String messageModified = functionality(unwrapMessage(postData));

                    String[] chain = unwrapChain(postData);
                    String next;
                    if (chain.length > 2) {
                        String[] newChain = Arrays.copyOfRange(chain, 1, chain.length);
                        next = newChain[0];
                        System.out.println("message: " + wrapMessage(messageModified, newChain));
                        sendPOST(wrapMessage(messageModified, newChain), next);
                    } else {
                        next = chain[1];
                        //messageModified = messageModified.replaceAll("Host: .+", "Host: " + next);
                        System.out.println("message: " + messageModified);
                        System.out.println("next: " + next);

                        send(messageModified, next);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}