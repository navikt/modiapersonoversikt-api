package no.nav.sbl.modiabrukerdialog.pip.journalforing.config;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NAVAnsattEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NAVOrgEnhetEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.DefaultSaksbehandlerInnstillingerService;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.DefaultJournalfortTemaAttributeLocatorDelegate;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.JournalfortTemaAttributeLocatorDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({NAVOrgEnhetEndpointConfig.class, NAVAnsattEndpointConfig.class})
public class JournalfortTemaPipConfig {

    @Inject
    private GOSYSNAVOrgEnhet enhetService;

    @Bean
    public JournalfortTemaAttributeLocatorDelegate enhetAttributeLocatorDelegate() {
        return new DefaultJournalfortTemaAttributeLocatorDelegate(enhetService, saksbehandlerInnstillingerService());
    }

    @Bean
    public AnsattService ansattService() {
        return new AnsattService();
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return new DefaultSaksbehandlerInnstillingerService();
    }

}
