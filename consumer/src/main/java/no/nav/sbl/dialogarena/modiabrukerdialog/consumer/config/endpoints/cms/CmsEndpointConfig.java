package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.cms;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.CMSValueRetrieverMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.CMSValueRetrieverMock.CMS_KEY;

@Configuration
public class CmsEndpointConfig {

    public static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/app/saksoversikt/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innholdstekster";

    @Value("${appres.cms.url}")
    private String appresUrl;

    private Logger log = LoggerFactory.getLogger(CmsEndpointConfig.class);

    @Bean
    public CmsContentRetriever cmsContentRetriever()  throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setTeksterRetriever(siteContentRetriever());
        cmsContentRetriever.setArtikkelRetriever(siteContentRetriever());
        return cmsContentRetriever;
    }

    @Bean
    public Pingable cmsPing() throws URISyntaxException {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "CMS";
                try {
                    new HttpContentRetriever().ping(new URI(appresUrl + INNHOLDSTEKSTER_NB_NO_REMOTE));
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
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
                try {
                    log.debug("Henter tekst fra CMS");
                    if (mockErTillattOgSlaattPaaForKey(CMS_KEY)) {
                        return mock.getValueOf(key, language);
                    }
                    String valueOf = prod.getValueOf(key, language);
                    log.debug("Tekst fra CMS OK");
                    return valueOf;
                } catch (MissingResourceException e) {
                    log.error("MissingResourceException", e);
                    return "Manglende tekst"; // Vi returnerer allikevel fordi man ikke vil ødelegge for resten av Modia som stort sett ikke bruker CMS
                }
            }
        };
    }

    private ValuesFromContentWithResourceBundleFallback getValueRetriever() throws URISyntaxException {
        Map<String, URI> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, new URI(appresUrl + INNHOLDSTEKSTER_NB_NO_REMOTE));
        ContentRetriever contentRetriever = new HttpContentRetriever();
        return new ValuesFromContentWithResourceBundleFallback(INNHOLDSTEKSTER_NB_NO_LOCAL, contentRetriever, uris, DEFAULT_LOCALE);
    }
}
