package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

public class Utils {

    public static  <T> T konfigurerMedHttps(T portType) {
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters params = new TLSClientParameters();
        params.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(params);
        return portType;
    }
}
