package httputils;

/**
 * Interface for callback to manage messages
 */
public interface MessageHandler {
    /**
     * Handler of message
     * @param message String message received
     */
    void handleMessage(String message);
}
