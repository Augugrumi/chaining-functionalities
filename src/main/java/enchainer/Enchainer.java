package enchainer;

import executeonmain.ExecuteOnMain;
import httputils.AbsNettyMessageHandler;
import httputils.NettyTCPAndUDPServer;
import httputils.Server;

import java.util.logging.Logger;
import static httputils.MessageWrapper.wrapMessage;

/**
 * Class that start the chain of VNF embedding the message received into a JSON
 */
public class Enchainer implements ExecuteOnMain {

    /**
     * Logging utility field
     */
    private static final Logger LOGGER = Logger.getLogger(Enchainer.class.getName());

    /**
     * Port on which the Enchainer wait for connections
     */
    private int port;

    /**
     * Chain of hosts that message have to pass through
     */
    private String[] chain;

    /**
     * Server that manage connections
     */
    private Server server;

    public Enchainer(int port, String[] chain) {
        this.port = port;
        this.chain = chain;
        server = new NettyTCPAndUDPServer();
    }

    @Override
    public void execute() {
        server.receive(port, new AbsNettyMessageHandler() {
            @Override
            public void handleMessage(String message) {
                String jsonMessage = wrapMessage(message, chain);
                LOGGER.warning(jsonMessage);
                server.sendPOST(jsonMessage, chain[0]);
            }
        });
    }

}
