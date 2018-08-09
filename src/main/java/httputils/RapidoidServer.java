package httputils;

import org.json.JSONObject;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.setup.Setup;

public class RapidoidServer extends AbsBaseServer {

    @Override
    public void receive(int port, MessageHandler handler) {
        Setup setup = Setup.create(RapidoidServer.class.getName() + String.valueOf(port));
        setup.address("localhost").port(port);
        setup.req((ReqHandler) req -> {
            String message = (new JSONObject(req.posted())).toString();
            handler.handleMessage(message);
            return null;
        });
    }
}
