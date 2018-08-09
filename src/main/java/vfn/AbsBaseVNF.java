package vfn;

import httputils.MessageHandler;
import httputils.RapidoidServer;
import httputils.Server;

import java.util.Arrays;
import java.util.logging.Logger;

import static httputils.MessageWrapper.*;

/**
 * Abstract class that implements VNF interface. Use TEMPLATE METHOD pattern to make new VNF implementation works with
 * the other classes
 */
public abstract class AbsBaseVNF extends RapidoidServer implements VNF, MessageHandler {

    /**
     * Logging utility field
     */
    private static final Logger LOGGER = Logger.getLogger(AbsBaseVNF.class.getName());

    /**
     * Port on which the VNF waits for messages
     */
    private final int port;

    /**
     * Server to receive messages
     */
    private Server server;

    /**
     * Constructor of the class
     * @param port Port on which the VNF waits for messages, must be in range 0-65535
     */
    public AbsBaseVNF(int port) {
        // TODO think a better check
        if (!(port >= 0 && port <= 65535))
            throw new RuntimeException("Port number must be in range 0-65535");
        this.port = port;
        server = new RapidoidServer();
    }

    /**
     * Method to run the object
     */
    @Override
    public void execute() {
        server.receive(port, this);
    }

    /**
     * Handler of message
     * @param message String message received
     */
    @Override
    public void handleMessage(String message) {
        try {
            // retrieve the original message and perform modification
            String messageModified = functionality(unwrapMessage(message));
            String[] chain = unwrapChain(message);
            String next;
            if (chain.length > 2) {
                String[] newChain = Arrays.copyOfRange(chain, 1, chain.length);
                next = newChain[0];
                LOGGER.info(wrapMessage(messageModified, newChain));
                sendPOST(wrapMessage(messageModified, newChain), next);
            } else {
                next = chain[1];
                LOGGER.info("message:\n" + messageModified +
                        "\nnext:\n" + next);
                send(messageModified, next);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}