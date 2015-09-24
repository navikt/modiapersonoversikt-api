package no.nav.sbl.dialogarena.varsel.config;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import no.nav.sbl.dialogarena.varsel.service.VarslerServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class VarslingContext {

    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/app/modiabrukerdialog/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.modia-varsling";
    public static final String DEFAULT_LOCALE = "nb";

    @Value("${appres.cms.url}")
    private String appresUrl;

    @Bean
    public VarslerService varslerService() {
        return new VarslerServiceImpl();
    }

    @Bean(name = "varsling-cms-integrasjon")
    public CmsContentRetriever varslingCmsContentRetriver() throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setTeksterRetriever(siteContentRetriever());
        return cmsContentRetriever;
    }

    private ValueRetriever siteContentRetriever() throws URISyntaxException {
        Map<String, URI> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE,
                new URI(appresUrl + INNHOLDSTEKSTER_NB_NO_REMOTE)
        );
        ContentRetriever contentRetriever = new HttpContentRetriever();

        return new ValuesFromContentWithResourceBundleFallback(
                INNHOLDSTEKSTER_NB_NO_LOCAL,
                contentRetriever,
                uris,
                DEFAULT_LOCALE
        );
    }
}
