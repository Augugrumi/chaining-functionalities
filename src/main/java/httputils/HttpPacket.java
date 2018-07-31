package httputils;

/**
 * Simple class that roughly represent a HTTP request
 */
public class HttpPacket {
    /**
     * String that represent the header of the HTTP request
     */
    private final String header;

    /**
     * String that represent data in case of POST request (empty string in case of GET)
     */
    private final String postData;

    /**
     * Constructor of the class
     * @param header String that represent the header of the HTTP request
     * @param postData String that represent data in case of POST request (empty string in case of GET)
     */
    public HttpPacket(String header, String postData) {
        this.header = header;
        this.postData = postData;
    }

    /**
     * Method to access to the header of the HTTP request
     * @return String - the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Method to access data of the POST request
     * @return String - data of the POST
     */
    public String getPostData() {
        return postData;
    }

    /**
     * Return the string representation of the HTTP request
     * @return String - header + post data
     */
    @Override
    public String toString() {
        return header + postData;
    }
}
