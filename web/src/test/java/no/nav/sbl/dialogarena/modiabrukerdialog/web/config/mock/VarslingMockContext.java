package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.of;
import static org.joda.time.DateTime.now;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class VarslingMockContext {


    @Bean(name = "varsling-cms-integrasjon")
    public ContentRetriever varslingCmsContentRetriver() throws URISyntaxException {
        ContentRetriever contentRetriever = mock(ContentRetriever.class);
        when(contentRetriever.hentTekst(anyString())).thenReturn("");
        return contentRetriever;
    }

    @Bean
    public VarslerService varslerService() {
        VarslerService varslerServiceMock = mock(VarslerService.class);
        List<Varsel> varselList = new ArrayList<>();
        varselList.add(new Varsel("", now(), new ArrayList<>(), true));
        when(varslerServiceMock.hentAlleVarsler(anyString())).thenReturn(of(varselList));
        return varslerServiceMock;
    }
}
