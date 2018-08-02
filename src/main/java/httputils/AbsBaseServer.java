package httputils;

import vfn.BaseVNF;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Class that implements base method for sending and receiving HTTP requests
 */
public abstract class AbsBaseServer implements Server {

    /**
     * Logging utility field
     */
    private static final Logger LOGGER = Logger.getLogger(BaseVNF.class.getName());

    /**
     * Method to wait for a connection and retrieve data as an HttpPacket object
     * @param ss ServerSocket to which requests are retrieved
     * @return HttpPacket that represent the request
     * @throws IOException if connection breaks
     */
    @Override
    public HttpPacket receive(ServerSocket ss) throws IOException {
        Socket client = ss.accept();
        InputStream is = client.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        line = in.readLine();
        String request_method = line;
        StringBuilder request = new StringBuilder();
        request.append(line);
        line = "";
        // looks for post data
        int postDataI = -1;
        while ((line = in.readLine()) != null && (line.length() != 0)) {
            request.append(line);
            request.append("\r\n");
            if (line.contains("Content-Length:")) {
                postDataI = new Integer(line.substring(line.indexOf("Content-Length:") + 16, line.length()));
            }
        }
        String postData = "";
        // read the post data
        if (postDataI > 0) {
            char[] charArray = new char[postDataI];
            in.read(charArray, 0, postDataI);
            postData = new String(charArray);
        }

        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.print("HTTP/1.1 200 \r\n"); // Version & status code
        out.print("Content-Type: text/plain\r\n"); // The type of data
        out.print("Connection: close\r\n"); // Will close stream
        out.print("\r\n");
        out.print("ACK\r\n");

        out.close(); // Flush and close the output stream
        in.close(); // Close the input stream
        client.close(); // Close the socket itself

        return new HttpPacket(request.toString(), postData);
    }

    /**
     * Method to send a POST request
     * @param message String that represent the message that will be sent
     * @param destination String that represent the address of the receiver
     */
    @Override
    public void sendPOST(String message, String destination) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(destination);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(message.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
            wr.writeBytes(message);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                //System.out.println(line);
                if (line.length() == 0)
                    break;
                response.append(line + "\r\n");
            }
            rd.close();
        } catch (Exception e) {
            LOGGER.warning("Something goes wrong");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Method to send a raw request
     * @param message String that represent the message that will be sent
     * @param destination String that represent the address of the receiver
     */
    @Override
    public void send(String message, String destination) {
        Socket socket = null;
        try {
            URL url = new URL(destination);

            socket = new Socket(url.getHost(),url.getPort());

            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.print(message);
            pw.flush();
            pw.close();

            socket.close();
        } catch (Exception e) {
             LOGGER.warning("Something goes wrong");
             e.printStackTrace();
        }
    }
}
