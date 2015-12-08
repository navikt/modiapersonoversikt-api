package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class VarslingMockContext {


    @Bean(name = "varsling-cms-integrasjon")
    public CmsContentRetriever varslingCmsContentRetriver() throws URISyntaxException {
        CmsContentRetriever contentRetriever = mock(CmsContentRetriever.class);
        when(contentRetriever.hentTekst(anyString())).thenReturn("");
        return contentRetriever;
    }

    @Bean
    public VarslerService varslerService() {
        VarslerService varslerServiceMock = mock(VarslerService.class);
        List<Varsel> varselList = new ArrayList<>();
        varselList.add(new Varsel("", now(), "", new ArrayList<Varsel.VarselMelding>()));
        when(varslerServiceMock.hentAlleVarsler(anyString())).thenReturn(optional(varselList));
        return varslerServiceMock;
    }
}
