package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static org.apache.commons.lang3.StringUtils.join;

public class TransformV2 {

    public static final int DEFAULT_ANTALL = 1;
    public static final double DEFAULT_SATS = 1.0;
    public static final String DEFAULT_SPESIFIKASJON = "";

    public static List<Utbetaling> lagUtbetalingerFraTjenesten(List<WSUtbetaling> wsUtbetalinger) {

        List<Utbetaling> utbetalinger = new ArrayList<>();

        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {

            Interval periode = wsUtbetaling.getUtbetalingsPeriode() != null ?
                        new Interval(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato(), wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato())
                        : new Interval(0,0);

            UtbetalingBuilder utbetalingBuilder = new UtbetalingBuilder()
                    .withMottakerId(wsUtbetaling.getUtbetalingMottaker().getMottakerId())
                    .withMottakernavn(wsUtbetaling.getUtbetalingMottaker().getNavn())
                    .withMottakerkode(transformMottakerKode(wsUtbetaling.getUtbetalingMottaker(), "fnr"))
                    .withPeriode(periode)
                    .withValuta(wsUtbetaling.getValuta())
                    .withStatus(wsUtbetaling.getStatusKode())
                    .withUtbetalingsDato(wsUtbetaling.getUtbetalingDato())
                    .withKontonr(wsUtbetaling.getGironr());

            if(wsUtbetaling.getKildeNavn() != null && wsUtbetaling.getKildeNavn().equalsIgnoreCase("abetal")) {
                utbetalingBuilder.withHovedytelse(wsUtbetaling.getTekstmelding());
            } else {
                utbetalingBuilder.withHovedytelse(wsUtbetaling.getBilagListe().get(0).getYtelseBeskrivelse());
            }

            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {

                utbetalingBuilder.withMelding(transformMelding(wsBilag));

                for (WSPosteringsdetaljer wsPosteringsdetalj : wsBilag.getPosteringsdetaljerListe()) {

                    Underytelse underytelse = new Underytelse(
                            transformerUnderbeskrivelse(wsPosteringsdetalj.getKontoBeskrUnder(), wsPosteringsdetalj.getKontoBeskrHoved()),
                            optional(wsPosteringsdetalj.getSpesifikasjon()).getOrElse(DEFAULT_SPESIFIKASJON),
                            optional(wsPosteringsdetalj.getAntall()).getOrElse(DEFAULT_ANTALL),
                            wsPosteringsdetalj.getBelop(),
                            optional(wsPosteringsdetalj.getSats()).getOrElse(DEFAULT_SATS));

                    utbetalingBuilder.withUnderytelse(underytelse);
                }

            }
            utbetalinger.add(utbetalingBuilder.createUtbetaling());
        }
        return utbetalinger;
    }

    private static String transformMottakerKode(WSMottaker wsMottaker, String fnr) {
        if (!fnr.equals(wsMottaker.getMottakerId())) {
            return ARBEIDSGIVER;
        }
        return BRUKER;
    }

    private static String transformMelding(WSBilag wsBilag) {
        List<WSMelding> meldingListe = wsBilag.getMeldingListe();
        List<String> strings = new ArrayList<>();
        for (WSMelding wsMelding : meldingListe) {
            strings.add(wsMelding.getMeldingtekst());
        }
        return join(strings, ", ");
    }

    private static String transformerUnderbeskrivelse(String kontoBeskrUnder, String kontoBeskrHoved) {
        return (kontoBeskrUnder != null && !kontoBeskrUnder.isEmpty() ? kontoBeskrUnder : kontoBeskrHoved);
    }
}
