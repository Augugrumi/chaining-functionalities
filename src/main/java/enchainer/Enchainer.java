package enchainer;

import executeonmain.ExecuteOnMain;
import httputils.AbsNettyMessageHandler;
import httputils.NettyTCPAndUDPServer;
import httputils.Server;

import java.util.Arrays;

import static httputils.MessageWrapper.wrapMessage;

public class Enchainer implements ExecuteOnMain {

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
            public void handleMessage() {
                server.sendPOST(wrapMessage(getMessage(), chain), chain[0]);
            }
        });
    }

    public static void main(String[] args) {
        String[] chain = Arrays.copyOfRange(args, 1, args.length);
        new Enchainer(Integer.parseInt(args[0]), chain).execute();
    }

}
