package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.CMSValueRetrieverMock;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.CMSValueRetrieverMock.CMS_KEY;
import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class CmsEndpointConfig {

    public static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/app/saksoversikt/nb/tekster";
    private static final String ARTIKLER_NB_NO_REMOTE = "/app/saksoversikt/nb/saksinformasjon";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innholdstekster";

    @Value("${appres.cms.url}")
    private String appresUrl;

    private static final Logger log = getLogger(CmsEndpointConfig.class);

    @Bean
    public CmsContentRetriever cmsContentRetriever() throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setTeksterRetriever(siteContentRetriever());
        cmsContentRetriever.setArtikkelRetriever(siteArtikkelRetriever());
        return cmsContentRetriever;
    }

    @Bean
    public ContentRetriever contentRetriever() {
        return new HttpContentRetriever();
    }

    @Bean
    public Pingable cmsPing() throws URISyntaxException {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "CMS";
                try {
                    contentRetriever().ping(new URI(appresUrl + INNHOLDSTEKSTER_NB_NO_REMOTE));
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    log.error("Fikk exception fra CMS", e);
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private ValueRetriever siteContentRetriever() throws URISyntaxException {
        final ValueRetriever prod = getValueRetriever();
        final ValueRetriever mock = new CMSValueRetrieverMock().getValueRetrieverMock();

        return new ValueRetriever() {
            @Override
            public String getValueOf(String key, String language) {
                if (mockErTillattOgSlaattPaaForKey(CMS_KEY)) {
                    return mock.getValueOf(key, language);
                }
                return prod.getValueOf(key, language);
            }
        };
    }

    private ValuesFromContentWithResourceBundleFallback getValueRetriever() throws URISyntaxException {
        Map<String, URI> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, new URI(appresUrl + INNHOLDSTEKSTER_NB_NO_REMOTE));
        return new ValuesFromContentWithResourceBundleFallback(INNHOLDSTEKSTER_NB_NO_LOCAL, contentRetriever(), uris, DEFAULT_LOCALE);
    }

    private ValueRetriever siteArtikkelRetriever() throws URISyntaxException {
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, asList(new URI(appresUrl + ARTIKLER_NB_NO_REMOTE)));
        return new ValuesFromContentWithResourceBundleFallback(asList(INNHOLDSTEKSTER_NB_NO_LOCAL), contentRetriever(), uris, DEFAULT_LOCALE);
    }
}
