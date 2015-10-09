package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.EnhetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import(value = {ConsumerServicesMockContext.class})
public class DialogPanelMockContext {
    @Bean
    public CmsContentRetriever cmsContentRetriever() {
        return mock(CmsContentRetriever.class);
    }

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return mock(BrukerprofilServiceBi.class);
    }

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return mock(PersonKjerneinfoServiceBi.class);
    }

    @Bean
    public EnhetService enhetService() {
        EnhetService enhetService = mock(EnhetService.class);
        when(enhetService.hentEnhet(anyString())).thenReturn(new AnsattEnhet("", ""));
        return enhetService;
    }
}
