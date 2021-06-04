package no.nav.modiapersonoversikt.config.endpoint.v1.norg;

import no.nav.common.utils.EnvironmentUtils;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import javax.security.auth.callback.CallbackHandler;
import java.util.HashMap;
import java.util.Map;

public class NorgEndpointFelles {
    public static final String KJERNEINFO_TJENESTEBUSS_USERNAME = "SRV_KJERNEINFO_TJENESTEBUSS_USERNAME";
    public static final String KJERNEINFO_TJENESTEBUSS_PASSWORD = "SRV_KJERNEINFO_TJENESTEBUSS_PASSWORD";

    public static Map<String, Object> getSecurityProps() {
        final String user = EnvironmentUtils.getRequiredProperty("ctjenestebuss.username", KJERNEINFO_TJENESTEBUSS_USERNAME);
        final String password = EnvironmentUtils.getRequiredProperty("ctjenestebuss.password", KJERNEINFO_TJENESTEBUSS_PASSWORD);

        Map<String, Object> props = new HashMap<>();
        props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        props.put(WSHandlerConstants.USER, user);
        props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        props.put(WSHandlerConstants.PW_CALLBACK_REF, (CallbackHandler) callbacks -> {
            WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[0];
            passwordCallback.setPassword(password);
        });
        return props;
    }
}
