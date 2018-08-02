package enchainer;

import httputils.AbsBaseServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static httputils.MessageWrapper.wrapMessage;

public class Enchainer extends AbsBaseServer {

    private int port;

    private String[] chain;

    public Enchainer(int port, String[] chain) {
        this.port = port;
        this.chain = chain;
    }

    // TODO set it in some better way
    public void setChain(String[] chain) {
        this.chain = chain;
    }

    public void execute() throws IOException {
        ServerSocket ss = new ServerSocket(port);

        // To execute all runnable
        ExecutorService executor = Executors.newCachedThreadPool();

        for(;;) {
            String originalMessage = receive(ss).toString();

            executor.execute(() -> {
                String wrappedMessage = wrapMessage(originalMessage, chain);
                sendPOST(wrappedMessage, chain[0]);
            });
        }
    }

    public static void main(String[] args) {
        String[] chain = Arrays.copyOfRange(args, 1, args.length);
        try {
            new Enchainer(Integer.parseInt(args[0]), chain).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
