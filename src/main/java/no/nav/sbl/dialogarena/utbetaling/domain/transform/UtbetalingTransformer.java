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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UNDERYTELSE_COMPARE_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UNDERYTELSE_SKATT_NEDERST;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static org.apache.commons.lang3.StringUtils.join;

public class UtbetalingTransformer {

    public static final int DEFAULT_ANTALL = 1;
    public static final double DEFAULT_SATS = 1.0;
    public static final String DEFAULT_SPESIFIKASJON = "";

    public static List<Utbetaling> lagUtbetalinger(List<WSUtbetaling> wsUtbetalinger, String fnr) {

        List<Utbetaling> utbetalinger = new ArrayList<>();

        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {

            //TODO: Fornuftig håndtering av manglende periode
            Interval periode = wsUtbetaling.getUtbetalingsPeriode() != null ?
                    new Interval(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato(), wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato())
                    : new Interval(0,0);

            UtbetalingBuilder utbetalingBuilder = new UtbetalingBuilder()
                    .withMottakerId(wsUtbetaling.getUtbetalingMottaker().getMottakerId())
                    .withMottakernavn(wsUtbetaling.getUtbetalingMottaker().getNavn())
                    .withMottakerkode(transformerMottakerKode(wsUtbetaling.getUtbetalingMottaker(), fnr))
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

            Set<String> meldinger = new LinkedHashSet<>();
            List<Underytelse> underytelser = new ArrayList<>();
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {

                meldinger.add(transformerMelding(wsBilag));

                for (WSPosteringsdetaljer wsPosteringsdetalj : wsBilag.getPosteringsdetaljerListe()) {

                    Double belop = wsPosteringsdetalj.getBelop();
                    String hovedBeskrivelse = wsPosteringsdetalj.getKontoBeskrHoved().toLowerCase();
                    if (hovedBeskrivelse.contains("trekk") || hovedBeskrivelse.contains("skatt")) {
                        belop = belop > 0 ? -belop : belop;
                    }

                    underytelser.add(new Underytelse(
                            transformerUnderbeskrivelse(wsPosteringsdetalj.getKontoBeskrUnder(), wsPosteringsdetalj.getKontoBeskrHoved()),
                            optional(wsPosteringsdetalj.getSpesifikasjon()).getOrElse(DEFAULT_SPESIFIKASJON),
                            optional(wsPosteringsdetalj.getAntall()).getOrElse(DEFAULT_ANTALL),
                            belop,
                            optional(wsPosteringsdetalj.getSats()).getOrElse(DEFAULT_SATS)));
                }
            }

            double brutto = on(underytelser).map(Underytelse.UTBETALT_BELOP).reduce(sumDouble);
            double trekk = on(underytelser).map(Underytelse.TREKK_BELOP).reduce(sumDouble);
            utbetalingBuilder.withBrutto(brutto);
            utbetalingBuilder.withTrekk(trekk);
            utbetalingBuilder.withUtbetalt(brutto + trekk);

            utbetalingBuilder.withMelding(join(meldinger, ". "));

            sort(underytelser, UNDERYTELSE_COMPARE_BELOP);
            sort(underytelser, UNDERYTELSE_SKATT_NEDERST);
            utbetalingBuilder.withUnderytelser(underytelser);

            utbetalinger.add(utbetalingBuilder.createUtbetaling());
        }
        return utbetalinger;
    }

    private static String transformerMottakerKode(WSMottaker wsMottaker, String fnr) {
        if (!fnr.equals(wsMottaker.getMottakerId())) {
            return ARBEIDSGIVER;
        }
        return BRUKER;
    }

    private static String transformerMelding(WSBilag wsBilag) {
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
