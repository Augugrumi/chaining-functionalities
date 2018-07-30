package httputils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class MessageWrapper {
    private MessageWrapper(){}

    public static String wrapMessage(String originalMessage, String[] chain) {
        JSONObject message = new JSONObject();

        message.put("message", originalMessage);
        if (chain.length > 0) {
            JSONArray mJSONArray = new JSONArray(Arrays.asList(chain));
            message.put("chain", mJSONArray);
        } else
            message.put("chain", "[]");
        return message.toString();
    }
}
