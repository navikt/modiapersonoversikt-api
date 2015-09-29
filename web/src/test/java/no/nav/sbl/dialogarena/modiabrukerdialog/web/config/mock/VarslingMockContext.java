package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class VarslingMockContext {

    @Bean
    public VarslerService varslerService() {
        return mock(VarslerService.class);
    }

    @Bean(name = "varsling-cms-integrasjon")
    public CmsContentRetriever varslingCmsContentRetriver() throws URISyntaxException {
        CmsContentRetriever contentRetriever = mock(CmsContentRetriever.class);
        when(contentRetriever.hentTekst(anyString())).thenReturn("");
        return contentRetriever;
    }
}
