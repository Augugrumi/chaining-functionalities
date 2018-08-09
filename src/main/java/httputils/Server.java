package httputils;

/**
 * Basic interface that exposes methods for a server object
 */
public interface Server {

    /**
     * Method to wait for a connection and manage the message
     * @param port int that represent the port on which the sever is listening
     * @param handler MessageHandler to manage message received
     */
    void receive(int port, MessageHandler handler);

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
