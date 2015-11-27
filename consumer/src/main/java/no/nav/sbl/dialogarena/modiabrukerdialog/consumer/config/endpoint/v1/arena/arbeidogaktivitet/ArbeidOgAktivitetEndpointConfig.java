package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arena.arbeidogaktivitet;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.ArbeidOgAktivitetEndpointMock.createArbeidOgAktivitetMock;

@Configuration
public class ArbeidOgAktivitetEndpointConfig {

    public static final String ARENA_ARBEIDOGATKIVITET_KEY = "start.arena.arbeidogaktivitet.withmock";

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        ArbeidOgAktivitet prod = createArbeidOgAktivitet();
        ArbeidOgAktivitet mock = createArbeidOgAktivitetMock();

        return createMetricsProxyWithInstanceSwitcher(prod, mock, ARENA_ARBEIDOGATKIVITET_KEY, ArbeidOgAktivitet.class);
    }

    private static ArbeidOgAktivitet createArbeidOgAktivitet() {
        return new CXFClient<>(ArbeidOgAktivitet.class)
                .address(System.getProperty("arena.arbeidogaktivitet.v1.url"))
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }

    @Bean
    public Pingable arbeidOgAktivitetPing(final ArbeidOgAktivitet ws) {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                try {
                    ws.hentSakListe(new WSHentSakListeRequest()
                            .withBruker(new WSBruker().withBrukertypeKode("PERSON").withBruker("10108000398"))
                            .withFom(LocalDate.now())
                            .withTom(LocalDate.now()));
                    return asList(new PingResult(SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }

            @Override
            public String name() {
                return "ARENA_ARBEIDOGAKTIVITET_V1";
            }

            @Override
            public String method() {
                return "hentSakListe";
            }
        };
    }
}
