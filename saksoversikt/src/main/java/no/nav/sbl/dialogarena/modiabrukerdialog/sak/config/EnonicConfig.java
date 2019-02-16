package no.nav.sbl.dialogarena.modiabrukerdialog.sak.config;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.sbl.util.EnvironmentUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class EnonicConfig {
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/app/modia-saksoversikt/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.saksoversikt";
    public static final String DEFAULT_LOCALE = "nb";

    @Inject
    private ContentRetriever contentRetriever;

    @Bean(name = "saksoversikt-cms-integrasjon")
    public CmsContentRetriever contentRetriever() throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setTeksterRetriever(siteContentRetriever());
        return cmsContentRetriever;
    }

    private ValueRetriever siteContentRetriever() throws URISyntaxException {
        Map<String, URI> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE,
                new URI(System.getProperty("appres.cms.url") + INNHOLDSTEKSTER_NB_NO_REMOTE)
        );

        return new ValuesFromContentWithResourceBundleFallback(
                INNHOLDSTEKSTER_NB_NO_LOCAL,
                contentRetriever,
                uris,
                DEFAULT_LOCALE
        );
    }

}
