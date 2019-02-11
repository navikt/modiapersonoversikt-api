package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;


import no.nav.sbl.util.EnvironmentUtils;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.omg.CORBA.Environment;

import javax.security.auth.callback.CallbackHandler;
import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class NorgEndpointFelles {

    public static final String NORG_KEY = "start.norg.withmock";

    public static Map<String, Object> getSecurityProps() {
        String user = getRequiredProperty("ctjenestebuss.username");

        Map<String, Object> props = new HashMap<>();
        props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        props.put(WSHandlerConstants.USER, user);
        props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        props.put(WSHandlerConstants.PW_CALLBACK_REF, (CallbackHandler) callbacks -> {
            String password = getRequiredProperty("ctjenestebuss.password");

            WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[0];
            passwordCallback.setPassword(password);
        });
        return props;
    }
}
