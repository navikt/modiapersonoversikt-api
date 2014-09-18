package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.transport.http.HTTPConduit;

import static java.lang.Boolean.TRUE;

public class TLSOppsettUtils {
    public static final String SKRU_AV_SERTIFIKATSJEKK_LOKALT = "skru.av.sertifikatsjekk.lokalt";

    public static void skruAvSertifikatsjekkDersomLokalOppstart(Client client) {
        String lokalOppstartProperty = System.getProperty(SKRU_AV_SERTIFIKATSJEKK_LOKALT);
        if (lokalOppstartProperty != null && lokalOppstartProperty.equals(TRUE.toString())) {
            HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
            TLSClientParameters clientParameters = new TLSClientParameters();
            clientParameters.setDisableCNCheck(true);
            httpConduit.setTlsClientParameters(clientParameters);
        }
    }
}
