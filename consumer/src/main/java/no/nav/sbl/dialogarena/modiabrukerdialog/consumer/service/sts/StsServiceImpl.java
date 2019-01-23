package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.sts;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import no.nav.modig.common.MDCOperations;
import no.nav.sbl.rest.RestUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Constants.*;

public class StsServiceImpl {
    public static final String EXPIRARY_URI = "exp";

    private Map<String, String> tokenCache = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(StsServiceImpl.class);

    public String hentConsumerOidcToken() {
        String cachedToken = tokenCache.get(MODIABRUKERDIALOG_SYSTEM_USER);

        if (cachedToken != null) {
            if (tokenNotExpired(cachedToken)) {
                return cachedToken;
            } else {
                tokenCache.remove(MODIABRUKERDIALOG_SYSTEM_USER);
            }
        }

        String consumerOidcToken = getConsumerOidcToken();
        tokenCache.put(MODIABRUKERDIALOG_SYSTEM_USER, consumerOidcToken);

        return consumerOidcToken;
    }

    private String getConsumerOidcToken() {
        JsonParser jsonParser = new JsonParser();

        String oidcConsumerTokenResponse = gjorSporring(String.class);
        JsonObject oidcConsumerTokenJsonObject = jsonParser.parse(oidcConsumerTokenResponse).getAsJsonObject();

        if (oidcConsumerTokenJsonObject == null || oidcConsumerTokenJsonObject.get("access_token") == null) {
            throw new RuntimeException("Har ikke fått OIDC-token, OIDC-token er: " + oidcConsumerTokenResponse);
        }
        return oidcConsumerTokenJsonObject.get("access_token").getAsString();
    }

    private boolean tokenNotExpired(String cachedToken) {
        JsonParser jsonParser = new JsonParser();
        String base64encodedToken = cachedToken.split("\\.")[1];

        JsonElement oidcToken = jsonParser.parse(new String(Base64.getUrlDecoder().decode(base64encodedToken)));
        JsonElement expiry = oidcToken.getAsJsonObject().get(EXPIRARY_URI);

        DateTime nowPlus5mins = DateTime.now().plusMinutes(5);

        return nowPlus5mins.getMillis() < expiry.getAsLong() * 1000;
    }

    private <T> T gjorSporring(Class<T> targetClass) {
        String auth = MODIABRUKERDIALOG_SYSTEM_USER + BASIC_AUTH_SEPERATOR + MODIABRUKERDIALOG_SYSTEM_USER_PASSWORD;
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());

        return RestUtils.withClient(client -> client
                .target(SECURITY_TOKEN_SERVICE_BASEURL + "?grant_type=client_credentials&scope=openid")
                .request()
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .header(NAV_CALL_ID_HEADER, MDCOperations.generateCallId())
                .header(AUTHORIZATION, encodedAuth)
                .get(targetClass)
        );
    }
}
