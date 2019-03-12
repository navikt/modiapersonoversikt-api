package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import javax.security.auth.callback.CallbackHandler;
import java.util.HashMap;
import java.util.Map;

public class NorgEndpointFelles {

    public static final String NORG_KEY = "start.norg.withmock";

    public static Map<String, Object> getSecurityProps() {
        String user = System.getProperty("ctjenestebuss.username");

        Map<String, Object> props = new HashMap<>();
        props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        props.put(WSHandlerConstants.USER, user);
        props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        props.put(WSHandlerConstants.PW_CALLBACK_REF, (CallbackHandler) callbacks -> {
            String password = System.getProperty("ctjenestebuss.password");

            WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[0];
            passwordCallback.setPassword(password);
        });
        return props;
    }
}
