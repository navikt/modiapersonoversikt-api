package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.transport.http.HTTPConduit;

import static org.apache.cxf.frontend.ClientProxy.getClient;

public class Utils {

    public static  <T> T konfigurerMedHttps(T portType) {
        Client client = getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters params = new TLSClientParameters();
        params.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(params);
        return portType;
    }
}
