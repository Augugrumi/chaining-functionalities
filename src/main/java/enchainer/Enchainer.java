package enchainer;

import executeonmain.ExecuteOnMain;
import httputils.AbsNettyMessageHandler;
import httputils.NettyTCPAndUDPServer;
import httputils.Server;
import vfn.AbsBaseVNF;

import java.util.Arrays;
import java.util.logging.Logger;

import static httputils.MessageWrapper.wrapMessage;

public class Enchainer implements ExecuteOnMain {

    /**
     * Logging utility field
     */
    private static final Logger LOGGER = Logger.getLogger(Enchainer.class.getName());

    private int port;

    private String[] chain;

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

    public static void main(String[] args) {
        String[] chain = Arrays.copyOfRange(args, 1, args.length);
        new Enchainer(Integer.parseInt(args[0]), chain).execute();
    }

}
