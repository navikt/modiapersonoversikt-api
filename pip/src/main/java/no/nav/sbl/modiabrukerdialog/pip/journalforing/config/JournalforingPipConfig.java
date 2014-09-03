package no.nav.sbl.modiabrukerdialog.pip.journalforing.config;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.norg.NAVAnsattEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.norg.NAVOrgEnhetEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SaksbehandlerInnstillingerService;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.DefaultTemagruppeAttributeLocatorDelegate;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.TemagruppeAttributeLocatorDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({NAVOrgEnhetEndpointConfig.class, NAVAnsattEndpointConfig.class})
public class JournalforingPipConfig {

    @Inject
    private GOSYSNAVOrgEnhet enhetService;

    @Bean
    public TemagruppeAttributeLocatorDelegate enhetAttributeLocatorDelegate() {
        return new DefaultTemagruppeAttributeLocatorDelegate(enhetService, new SaksbehandlerInnstillingerService());
    }

    @Bean
    public AnsattService ansattService() {
        return new AnsattService();
    }

}
