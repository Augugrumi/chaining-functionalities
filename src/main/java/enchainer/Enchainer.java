package enchainer;

import httputils.AbsBaseServer;
import java.io.IOException;
import java.net.ServerSocket;

import static httputils.MessageWrapper.wrapMessage;

public class Enchainer extends AbsBaseServer {

    public String[] chain;


    public void method(int port) throws IOException {
        ServerSocket ss = new ServerSocket(port);

        String originalMessage = receive(ss).toString();

        String wrappedMessage = wrapMessage(originalMessage, chain);

        send(wrappedMessage, chain[0]);
    }
}
