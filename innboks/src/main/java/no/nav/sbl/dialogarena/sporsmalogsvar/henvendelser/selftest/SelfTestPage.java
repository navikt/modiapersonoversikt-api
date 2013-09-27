package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.selftest;

import no.nav.modig.wicket.selftest.SelfTestBase;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Viser status for alle integrasjonspunkter
 */
public class SelfTestPage extends SelfTestBase {


    private static final Logger logger = LoggerFactory.getLogger(SelfTestPage.class);

    @Inject
    @Named("henvendelsesSystemUser")
    private HenvendelsePortType henvendelsesSystemUser;

    public SelfTestPage(PageParameters params) throws IOException {
        super("Minehenvendelser", params);
    }


    private AvhengighetStatus getCmsStatus() {
        String cmsBaseUrl = System.getProperty("dialogarena.cms.url");
        long start = currentTimeMillis();
        int statusCode = 0;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(cmsBaseUrl).openConnection();
            connection.setConnectTimeout(10000);
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            logger.warn("Cms not reachable on " + cmsBaseUrl);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        String status = HTTP_OK == statusCode ? SelfTestBase.STATUS_OK : SelfTestBase.STATUS_ERROR;
        return new AvhengighetStatus("ENONIC_CMS", status, currentTimeMillis() - start, format("URL: %s", cmsBaseUrl));
    }

    private AvhengighetStatus getHenvendelseWSStatus() {
        long start = currentTimeMillis();
        String status = SelfTestBase.STATUS_ERROR;
        try {
            if (henvendelsesSystemUser.ping()) {
                status = SelfTestBase.STATUS_OK;
            }
        } catch (Exception e) {
            logger.warn("<<<<<<Error Contacting Henvendelse WS: " + e.getMessage(), e);
        }

        return new AvhengighetStatus("HENVENDELSE_TJENESTE_PING", status, currentTimeMillis() - start);
    }

    @Override
    protected void addToStatusList(List<AvhengighetStatus> statusList) {
        statusList.add(getHenvendelseWSStatus());
        statusList.add(getCmsStatus());
    }

}
