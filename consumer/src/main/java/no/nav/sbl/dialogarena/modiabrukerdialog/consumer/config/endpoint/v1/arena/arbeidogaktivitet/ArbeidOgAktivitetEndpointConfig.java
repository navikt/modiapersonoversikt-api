package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arena.arbeidogaktivitet;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class ArbeidOgAktivitetEndpointConfig {

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        ArbeidOgAktivitet prod = createArbeidOgAktivitet();
        return createTimerProxyForWebService("ArbeidOgAktivitet", prod, ArbeidOgAktivitet.class);
    }

    private static ArbeidOgAktivitet createArbeidOgAktivitet() {
        return new CXFClient<>(ArbeidOgAktivitet.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_ARBEIDOGAKTIVITET_V1_ENDPOINTURL"))
                .configureStsForSystemUser()
                .build();
    }

    @Bean
    public Pingable arbeidOgAktivitetPing(final ArbeidOgAktivitet ws) {
        Pingable.Ping.PingMetadata metadata = new Pingable.Ping.PingMetadata(
                "ArbeidOgAktivitet",
                EnvironmentUtils.getRequiredProperty("VIRKSOMHET_ARBEIDOGAKTIVITET_V1_ENDPOINTURL"),
                "hentSakListe",
                false
        );

        return () -> Pingable.Ping.avskrudd(metadata);
    }
}
