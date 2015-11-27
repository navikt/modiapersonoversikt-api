package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.modig.content.*;
import no.nav.modig.content.enonic.HttpContentRetriever;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.CMSValueRetrieverMock;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class CmsEndpointConfig {

    public static final String CMS_KEY = "start.cms.withmock";
    public static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_SAKSOVERSIKT_REMOTE = "/app/modia-saksoversikt/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_MODIA_REMOTE = "/app/modiabrukerdialog/nb/tekster";
    private static final String ARTIKLER_NB_NO_REMOTE = "/app/modia-saksoversikt/nb/saksinformasjon";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innholdstekster";
    private static final String MODIABRUKERDIALOG_NB_NO_LOCAL = "content.modiabrukerdialog";

    @Value("${appres.cms.url}")
    private String appresUrl;

    private static final Logger log = getLogger(CmsEndpointConfig.class);

    @Bean
    @Primary
    public CmsContentRetriever cmsContentRetriever() throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setTeksterRetriever(siteContentRetriever());
        cmsContentRetriever.setArtikkelRetriever(siteArtikkelRetriever());
        return cmsContentRetriever;
    }

    @Bean
    public PropertyResolver propertyResolver() throws URISyntaxException {
        return new PropertyResolver(cmsContentRetriever(), new InputStreamReader(
                Melding.class.getResourceAsStream("Melding.properties"), Charsets.UTF_8
        ));
    }

    @Bean
    public ContentRetriever contentRetriever() {
        return new HttpContentRetriever();
    }

    @Bean
    public Pingable cmsPing() throws URISyntaxException {
        return new Pingable() {

            private String url = appresUrl + INNHOLDSTEKSTER_NB_NO_SAKSOVERSIKT_REMOTE;

            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                try {
                    contentRetriever().ping(new URI(url));
                    return asList(new PingResult(SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    log.error("Fikk exception fra CMS " + url, e);
                    return asList(new PingResult(SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }

            @Override
            public String name() {
                return "CMS";
            }

            @Override
            public String method() {
                return "ping mot HttpContentRetriever";
            }

            @Override
            public String endpoint() {
                return url;
            }


        };
    }

    private ValueRetriever siteContentRetriever() throws URISyntaxException {
        final ValueRetriever prod = getValueRetriever();
        final ValueRetriever mock = new CMSValueRetrieverMock().getValueRetrieverMock();
        return createSwitcher(prod, mock, CMS_KEY, ValueRetriever.class);
    }

    private ValuesFromContentWithResourceBundleFallback getValueRetriever() throws URISyntaxException {
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, asList(
                new URI(appresUrl + INNHOLDSTEKSTER_NB_NO_MODIA_REMOTE),
                new URI(appresUrl + INNHOLDSTEKSTER_NB_NO_SAKSOVERSIKT_REMOTE)
        ));
        return new ValuesFromContentWithResourceBundleFallback(asList(
                INNHOLDSTEKSTER_NB_NO_LOCAL,
                MODIABRUKERDIALOG_NB_NO_LOCAL),
                contentRetriever(),
                uris,
                DEFAULT_LOCALE);
    }

    private ValueRetriever siteArtikkelRetriever() throws URISyntaxException {
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, singletonList(new URI(appresUrl + ARTIKLER_NB_NO_REMOTE)));
        return new ValuesFromContentWithResourceBundleFallback(asList(INNHOLDSTEKSTER_NB_NO_LOCAL,
                MODIABRUKERDIALOG_NB_NO_LOCAL),
                contentRetriever(),
                uris,
                DEFAULT_LOCALE);
    }
}
