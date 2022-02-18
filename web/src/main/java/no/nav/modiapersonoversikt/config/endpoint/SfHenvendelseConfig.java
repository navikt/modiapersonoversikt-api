package no.nav.modiapersonoversikt.config.endpoint;

import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.modiapersonoversikt.infrastructure.ping.ConsumerPingable;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService;
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService;
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseServiceImpl;
import org.springframework.context.annotation.Bean;

public class SfHenvendelseConfig {
    @Bean
    public SfHenvendelseService sfHenvendelseApi(
            PdlOppslagService pdlOppslagService,
            ArbeidsfordelingService arbeidsfordelingService,
            AnsattService ansattService,
            SystemUserTokenProvider systemUserTokenProvider
    ) {
        return new SfHenvendelseServiceImpl(
                pdlOppslagService,
                arbeidsfordelingService,
                ansattService,
                systemUserTokenProvider
        );
    }

    @Bean
    public Pingable sfHenvendelseApiPing(SfHenvendelseService service) {
        return new ConsumerPingable(
                "Salesforce - Henvendelse",
                service::ping
        );
    }
}
