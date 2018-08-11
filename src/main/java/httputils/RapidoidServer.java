package httputils;

import org.json.JSONObject;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.setup.Setup;

/**
 * Simple TCP server developed using Rapidoid. Used for exchange messages among VNFs
 */
public class RapidoidServer extends AbsBaseServer {

    /**
     * Method to wait for a connection and manage the message
     * @param port int that represent the port on which the sever is listening
     * @param handler AbsNettyMessageHandler to which requests are retrieved
     */
    @Override
    public void receive(int port, MessageHandler handler) {
        Setup setup = Setup.create(RapidoidServer.class.getName() + String.valueOf(port));
        setup.address(MyHttpConstants.LOCAL_ADDRESS).port(port);
        setup.req((ReqHandler) req -> {
            String message = (new JSONObject(req.posted())).toString();
            handler.handleMessage(message);
            return MyHttpConstants.OK;
        });
    }
}
