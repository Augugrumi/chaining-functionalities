package httputils;

import java.io.*;
import java.net.HttpURLConnection;
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
    private static final Logger LOGGER = Logger.getLogger(AbsBaseServer.class.getName());

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
                    "application/json");

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
        LOGGER.info(message);
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
