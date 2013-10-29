package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPeriode;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static org.joda.time.DateTime.now;

@Configuration
public class UtbetalingPortTypeMock {

    public static final String KONTO_NR = "12345678900";

    @Bean
    public UtbetalingPortType utbetalingPortType() {
        return new UtbetalingPortType() {
            @Override
            public WSHentUtbetalingListeResponse hentUtbetalingListe(WSHentUtbetalingListeRequest request) throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig, HentUtbetalingListeUgyldigDato {
                List<WSUtbetaling> utbetalinger = new ArrayList<>();
                utbetalinger.add(createUtbetaling1());
                utbetalinger.add(createUtbetaling2());
                utbetalinger.add(createUtbetaling3());
                utbetalinger.add(createUtbetaling4());
                utbetalinger.add(createUtbetaling5());
                utbetalinger.add(createUtbetaling6());
                return new WSHentUtbetalingListeResponse().withUtbetalingListe(utbetalinger);
            }

            private WSUtbetaling createUtbetaling1() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Alderspensjon", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(1000.0).withBruttobelop(1000.0).withStatusKode("12").withStatusBeskrivelse("Uføre")
                        .withUtbetalingDato(now().minusDays(4)).withUtbetalingsPeriode(createPeriode(new DateTime(2010, 1, 23, 0, 0), new DateTime(2011, 1, 24, 0, 0)));
                utbetaling.withBilagListe(bilag1, bilag2);
                return utbetaling;
            }

            private WSUtbetaling createUtbetaling2() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Uføre", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Foreldrepenger", KONTO_NR), createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(2000.0).withBruttobelop(2000.0).withStatusKode("12").withStatusBeskrivelse("Trygd")
                        .withUtbetalingDato(now().minusDays(7)).withUtbetalingsPeriode(createPeriode(new DateTime(2010, 2, 23, 0, 0), new DateTime(2011, 2, 24, 0, 0)));
                utbetaling.withBilagListe(bilag1, bilag2);
                return utbetaling;
            }

            private WSUtbetaling createUtbetaling3() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Barnepenger", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(3000.0).withBruttobelop(3000.0).withStatusKode("12").withStatusBeskrivelse("Barnepenger")
                        .withUtbetalingDato(now().minusDays(10)).withUtbetalingsPeriode(createPeriode(new DateTime(2010, 3, 23, 0, 0), new DateTime(2011, 3, 24, 0, 0)));
                utbetaling.withBilagListe(bilag1, bilag2);
                return utbetaling;
            }

            private WSUtbetaling createUtbetaling4() {
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Trygd", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(4000.00).withBruttobelop(4000.0).withStatusKode("12").withStatusBeskrivelse("Trygd")
                        .withUtbetalingDato(now().minusDays(40)).withUtbetalingsPeriode(createPeriode(new DateTime(2010, 4, 23, 0, 0), new DateTime(2011, 4, 24, 0, 0)));
                utbetaling.withBilagListe(bilag2);
                return utbetaling;
            }


            private WSUtbetaling createUtbetaling5() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("APGrunnbeløp", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(5100.50).withBruttobelop(5100.50).withStatusKode("12").withStatusBeskrivelse("APGrunnbeløp")
                        .withUtbetalingDato(now().minusDays(84)).withUtbetalingsPeriode(createPeriode(new DateTime(2010, 5, 23, 0, 0), new DateTime(2011, 5, 24, 0, 0)));
                utbetaling.withBilagListe(bilag1, bilag2);
                return utbetaling;
            }

            private WSUtbetaling createUtbetaling6() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Pensjon", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(6000.00).withBruttobelop(6000.0).withStatusKode("12").withStatusBeskrivelse("Pensjon")
                        .withUtbetalingDato(now().minusDays(200)).withUtbetalingsPeriode(createPeriode(new DateTime(2010, 6, 23, 0, 0), new DateTime(2011, 6, 24, 0, 0)));
                utbetaling.withBilagListe(bilag1, bilag2);
                return utbetaling;
            }

            private WSPeriode createPeriode(DateTime fomDate, DateTime tomDate) {
                return new WSPeriode().withPeriodeFomDato(fomDate).withPeriodeTomDato(tomDate);
            }

            private WSPosteringsdetaljer createPosteringsDetalj(String hovedBeskrivelse, String kontoNr) {
                return new WSPosteringsdetaljer().withKontoBeskrHoved(hovedBeskrivelse).withKontonr(kontoNr);
            }

            private WSBilag createBilag(String melding, WSPosteringsdetaljer... posteringsdetaljer) {
                return new WSBilag().withMeldingListe(new WSMelding().withMeldingtekst(melding)).withPosteringsdetaljerListe(posteringsdetaljer);
            }
        };
    }

}
