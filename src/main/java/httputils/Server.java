package httputils;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Basic interface that exposes methods for a server object
 */
public interface Server {

    /**
     * Method to wait for a connection and retrieve data as an HttpPacket object
     * @param ss ServerSocket to which requests are retrieved
     * @return HttpPacket that represent the request
     * @throws IOException if connection breaks
     */
    HttpPacket receive(ServerSocket ss) throws IOException;

    /**
     * Method to send a POST request
     * @param message String that represent the message that will be sent
     * @param destination String that represent the address of the receiver
     */
    void sendPOST(String message, String destination);

    /**
     * Method to send a raw request
     * @param message String that represent the message that will be sent
     * @param destination String that represent the address of the receiver
     */
    void send(String message, String destination);
}
