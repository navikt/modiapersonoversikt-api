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
                utbetalinger.add(createFerietilleggDagpenger());
                utbetalinger.add(createDagpenger());
                utbetalinger.add(createDagpenger1());
                utbetalinger.add(createYrkesskade());
                utbetalinger.add(createAlderspensjon());
                return new WSHentUtbetalingListeResponse().withUtbetalingListe(utbetalinger);
            }

            private WSUtbetaling createFerietilleggDagpenger() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Ferietillegg dagpenger", KONTO_NR),
                                                       createPosteringsDetalj("Dagpenger", KONTO_NR),
                                                       createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(1622.84).withBruttobelop(2153.84).withStatusKode("12").withStatusBeskrivelse("").withUtbetalingId("1")
                        .withUtbetalingDato(now().minusDays(4)).withUtbetalingsPeriode(createPeriode(now().minusDays(34), now().minusDays(4)));
                utbetaling.withBilagListe(bilag1);
                return utbetaling;
            }

            private WSUtbetaling createUtbetalingMedLangBeskrivelse() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Uføre", KONTO_NR),createPosteringsDetalj("En lang beskrivelse", KONTO_NR),createPosteringsDetalj("En enda lengre beskrivelse", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Foreldrepenger", KONTO_NR), createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(2000.0).withBruttobelop(2000.0).withStatusKode("12").withStatusBeskrivelse("Trygd").withUtbetalingId("2")
                        .withUtbetalingDato(now().minusDays(150)).withUtbetalingsPeriode(createPeriode(now().minusDays(180), now().minusDays(150)));
                utbetaling.withBilagListe(bilag1, bilag2);
                return utbetaling;
            }

            private WSUtbetaling createDagpenger() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Dagpenger", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(2718.0).withBruttobelop(3040.0).withStatusKode("12").withStatusBeskrivelse("").withUtbetalingId("3")
                        .withUtbetalingDato(now().minusDays(25)).withUtbetalingsPeriode(createPeriode(now().minusDays(39), now().minusDays(25)));
                utbetaling.withBilagListe(bilag1, bilag2);
                return utbetaling;
            }

            private WSUtbetaling createDagpenger1() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Dagpenger", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(2718.0).withBruttobelop(3040.0).withStatusKode("12").withStatusBeskrivelse("").withUtbetalingId("4")
                        .withUtbetalingDato(now().minusDays(50)).withUtbetalingsPeriode(createPeriode(now().minusDays(39), now().minusDays(25)));
                utbetaling.withBilagListe(bilag1, bilag2);
                return utbetaling;
            }

            private WSUtbetaling createYrkesskade() {
                WSBilag bilag2 = createBilag("bilag2", createPosteringsDetalj("Ytkesskade", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(2258.0).withBruttobelop(2258.0).withStatusKode("12").withStatusBeskrivelse("").withUtbetalingId("5")
                        .withUtbetalingDato(now().minusDays(40)).withUtbetalingsPeriode(createPeriode(now().minusDays(70), now().minusDays(40)));
                utbetaling.withBilagListe(bilag2);
                return utbetaling;
            }


            private WSUtbetaling createAlderspensjon() {
                WSBilag bilag1 = createBilag("bilag1", createPosteringsDetalj("Alderpensjon", KONTO_NR));
                WSBilag bilag2 = createBilag("bilag1", createPosteringsDetalj("Alderpensjon", KONTO_NR));
                WSBilag bilag3 = createBilag("bilag2", createPosteringsDetalj("Skatt", KONTO_NR));
                WSUtbetaling utbetaling = new WSUtbetaling();
                utbetaling.withNettobelop(19029.0).withBruttobelop(16365.0).withStatusKode("12").withStatusBeskrivelse("").withUtbetalingId("6")
                        .withUtbetalingDato(now().minusDays(20)).withUtbetalingsPeriode(createPeriode(now().minusDays(50), now().minusDays(20)));
                utbetaling.withBilagListe(bilag1, bilag2, bilag3);
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
