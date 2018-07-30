package httputils;

import java.io.IOException;
import java.net.ServerSocket;

public interface Server {
    HttpPacket receive(ServerSocket ss) throws IOException;
    void send(String message, String destination);
}
