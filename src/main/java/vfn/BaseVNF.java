package vfn;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Logger;

public abstract class BaseVNF implements VNF {

    private static final Logger LOGGER = Logger.getLogger(BaseVNF.class.getName());
    private int port;
    private String[] chain;

    public BaseVNF(int port, String[] chain) {
        // TODO check port number
        this.port = port;
        // TODO check chain
        this.chain = chain;
    }

    private String receive(ServerSocket ss) throws IOException {
        LOGGER.info("New request accepted");

        Socket client = ss.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String line;
        StringBuffer request = new StringBuffer();
        while ((line = in.readLine()) != null) {
            request.append(line);
            request.append("\r\n");
            if (line.length() == 0)
                break;
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

        return request.toString();
    }

    private void send(String message) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(chain[0]);
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

    public void execute() throws IOException {
        ServerSocket ss = new ServerSocket(port);

        for(;;) {
            try {
                String messageModified = functionality(receive(ss));
                send(messageModified);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}