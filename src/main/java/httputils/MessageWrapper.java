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

    /**
     * Method to puth a message inside a JSON to be exchange among links of the VNF chain
     * @param message message that will be added to the JSON accessible with the key "message"
     * @param chain list of the host that the message must pass thought accessible with the key "chain"
     * @return String that represent the JSON created
     */
    // TODO check if other fields have to be added
    public static String wrapMessage(String message, String[] chain) {
        JSONObject jsonMessage = new JSONObject();

        jsonMessage.put("message", message);
        if (chain.length > 0) {
            JSONArray mJSONArray = new JSONArray(Arrays.asList(chain));
            jsonMessage.put("chain", mJSONArray);
        } else
            jsonMessage.put("chain", "[]");
        return jsonMessage.toString();
    }
}
