package httputils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Class that contains methods to wrap message received into a JSON for the VNF chain
 */
public class MessageWrapper {

    // private constructor because there is no need to create object of this class
    private MessageWrapper(){}

    public static final String MESSAGE = "message";
    public static final String CHAIN = "chain";

    /**
     * Method to puth a message inside a JSON to be exchange among links of the VNF chain
     * @param message message that will be added to the JSON accessible with the key "message"
     * @param chain list of the host that the message must pass thought accessible with the key "chain"
     * @return String that represent the JSON created
     */
    // TODO check if other fields have to be added
    public static String wrapMessage(String message, String[] chain) {
        JSONObject jsonMessage = new JSONObject();

        jsonMessage.put(MESSAGE, message);
        if (chain.length > 0) {
            JSONArray mJSONArray = new JSONArray(Arrays.asList(chain));
            jsonMessage.put(CHAIN, mJSONArray);
        } else
            jsonMessage.put(CHAIN, "[]");
        return jsonMessage.toString();
    }

    /**
     * Method that given a JSON message return the original message embedded
     * @param jsonMessage String that represent the JSON message
     * @return String that represent the original message
     */
    public static String unwrapMessage(String jsonMessage) {
        JSONObject jsonData = new JSONObject(jsonMessage);
        return (String) jsonData.get(MESSAGE);
    }

    /**
     * Method that given a JSON message return the chain of VNF to which the message have to pass through
     * @param jsonMessage String that represent the JSON message
     * @return String[] chain of VNFs
     */
    public static String[] unwrapChain(String jsonMessage) {
        JSONObject jsonData = new JSONObject(jsonMessage);
        JSONArray arr = jsonData.getJSONArray(CHAIN);
        String[] chain = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++)
            chain[i] = arr.getString(i);
        return chain;
    }
}
