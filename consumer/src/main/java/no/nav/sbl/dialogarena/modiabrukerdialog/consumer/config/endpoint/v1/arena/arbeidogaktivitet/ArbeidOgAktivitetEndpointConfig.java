package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arena.arbeidogaktivitet;

import no.nav.modig.modia.ping.FailedPingResult;
import no.nav.modig.modia.ping.OkPingResult;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.ArbeidOgAktivitetEndpointMock.createArbeidOgAktivitetMock;

@Configuration
public class ArbeidOgAktivitetEndpointConfig {

    public static final String ARENA_ARBEIDOGATKIVITET_KEY = "start.arena.arbeidogaktivitet.withmock";

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        ArbeidOgAktivitet prod = createArbeidOgAktivitet();
        ArbeidOgAktivitet mock = createArbeidOgAktivitetMock();

        return createMetricsProxyWithInstanceSwitcher("ArbeidOgAktivitet", prod, mock, ARENA_ARBEIDOGATKIVITET_KEY, ArbeidOgAktivitet.class);
    }

    private static ArbeidOgAktivitet createArbeidOgAktivitet() {
        return new CXFClient<>(ArbeidOgAktivitet.class)
                .address(EnvironmentUtils.getRequiredProperty("arena.arbeidogaktivitet.v1.url"))
                .configureStsForSystemUserInFSS()
                .build();
    }

    @Bean
    public Pingable arbeidOgAktivitetPing(final ArbeidOgAktivitet ws) {
        return new Pingable() {
            @Override
            public PingResult ping() {
                long start = System.currentTimeMillis();
                try {
                    ws.hentSakListe(new WSHentSakListeRequest()
                            .withBruker(new WSBruker().withBrukertypeKode("PERSON").withBruker("10108000398"))
                            .withFom(LocalDate.now())
                            .withTom(LocalDate.now()));
                    return new OkPingResult(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    return new FailedPingResult(e, System.currentTimeMillis() - start);
                }
            }

            @Override
            public String name() {
                return "Arena - arbeid- og aktivitetssaker";
            }

            @Override
            public String method() {
                return "hentSakListe";
            }

            @Override
            public String endpoint() {
                return EnvironmentUtils.getRequiredProperty("arena.arbeidogaktivitet.v1.url");
            }
        };
    }
}
