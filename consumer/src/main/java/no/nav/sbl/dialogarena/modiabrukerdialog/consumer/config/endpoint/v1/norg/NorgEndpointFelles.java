package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import no.nav.sbl.util.EnvironmentUtils;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import javax.security.auth.callback.CallbackHandler;
import java.util.HashMap;
import java.util.Map;

public class NorgEndpointFelles {
    public static Map<String, Object> getSecurityProps() {
        final String user = EnvironmentUtils.getRequiredProperty("ctjenestebuss.username", "SRV_KJERNEINFO_TJENESTEBUSS_USERNAME");
        final String password = EnvironmentUtils.getRequiredProperty("ctjenestebuss.password", "SRV_KJERNEINFO_TJENESTEBUSS_PASSWORD");

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
