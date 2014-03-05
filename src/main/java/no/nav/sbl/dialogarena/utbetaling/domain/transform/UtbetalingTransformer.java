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

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UNDERYTELSE_COMPARE_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UNDERYTELSE_SKATT_NEDERST;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.Mottaktertype;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static org.apache.commons.lang3.StringUtils.join;

public class UtbetalingTransformer {

    private static final List<Integer> TILLATTE_STATUSER = asList(3, 6, 10, 11, 18);

    public static List<Utbetaling> lagUtbetalinger(List<WSUtbetaling> wsUtbetalinger, String fnr) {

        List<Utbetaling> utbetalinger = new ArrayList<>();

        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            //Statuskoder skal ikke være null fra tjenesten. Håndterer med optional midlertidig.
            int statusKode = parseInt(optional(wsUtbetaling.getStatusKode()).getOrElse("3"));
            if (!TILLATTE_STATUSER.contains(statusKode)) { break; }

            UtbetalingBuilder utbetalingBuilder = new UtbetalingBuilder(wsUtbetaling.getUtbetalingId())

                    .withMottakerId(wsUtbetaling.getUtbetalingMottaker().getMottakerId())
                    .withMottakernavn(wsUtbetaling.getUtbetalingMottaker().getNavn())
                    .withMottakertype(transformerMottakertype(wsUtbetaling.getUtbetalingMottaker(), fnr))
                    .withPeriode(getPeriode(wsUtbetaling))
                    .withValuta(wsUtbetaling.getValuta())
                    .withStatus(wsUtbetaling.getStatusBeskrivelse().toLowerCase())
                    .withUtbetalingsDato(wsUtbetaling.getUtbetalingDato())
                    .withKontonr(wsUtbetaling.getGironr())
                    .withHovedytelse(getHovedytelseBeskrivelse(wsUtbetaling));

            Set<String> meldinger = new LinkedHashSet<>();
            List<Underytelse> underytelser = new ArrayList<>();
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {

                meldinger.add(transformerMelding(wsBilag));

                for (WSPosteringsdetaljer wsPosteringsdetalj : wsBilag.getPosteringsdetaljerListe()) {

                    underytelser.add(new Underytelse(
                            transformerUnderbeskrivelse(wsPosteringsdetalj.getKontoBeskrUnder(), wsPosteringsdetalj.getKontoBeskrHoved()),
                            optional(wsPosteringsdetalj.getSpesifikasjon()).getOrElse(""),
                            optional(wsPosteringsdetalj.getAntall()),
                            wsPosteringsdetalj.getBelop(),
                            optional(wsPosteringsdetalj.getSats())));
                }
            }

            utbetalingBuilder.withMelding(join(meldinger, "\n"));

            double brutto = on(underytelser).map(Underytelse.UTBETALT_BELOP).reduce(sumDouble);
            double trekk = on(underytelser).map(Underytelse.TREKK_BELOP).reduce(sumDouble);
            utbetalingBuilder.withBrutto(brutto);
            utbetalingBuilder.withTrekk(trekk);
            utbetalingBuilder.withUtbetalt(brutto + trekk);

            sort(underytelser, UNDERYTELSE_COMPARE_BELOP);
            sort(underytelser, UNDERYTELSE_SKATT_NEDERST);
            utbetalingBuilder.withUnderytelser(underytelser);

            utbetalinger.add(utbetalingBuilder.build());
        }
        return utbetalinger;
    }

    private static Interval getPeriode(WSUtbetaling wsUtbetaling) {
        return wsUtbetaling.getUtbetalingsPeriode() != null ?
                new Interval(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato(), wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato())
                : new Interval(0,0);
    }

    /**
     * Henter hovedytelsebeskrivelsen basert på hvilket baksystem utbetalingen kommer fra
     * @param wsUtbetaling wsutbetalingen
     * @return hovedytelsebeskrivelsen
     */
    private static String getHovedytelseBeskrivelse(WSUtbetaling wsUtbetaling) {
        if(wsUtbetaling.getKildeNavn() != null && wsUtbetaling.getKildeNavn().equalsIgnoreCase("abetal")) {
            return wsUtbetaling.getTekstmelding();
        } else {
            return wsUtbetaling.getBilagListe().get(0).getYtelseBeskrivelse();
        }
    }

    private static Mottaktertype transformerMottakertype(WSMottaker wsMottaker, String fnr) {
        if (!fnr.equals(wsMottaker.getMottakerId())) {
            return Mottaktertype.ANNEN_MOTTAKER;
        }
        return Mottaktertype.BRUKER;
    }

    private static String transformerMelding(WSBilag wsBilag) {
        List<String> strings = new ArrayList<>();
        for (WSMelding wsMelding : wsBilag.getMeldingListe()) {
            strings.add(wsMelding.getMeldingtekst());
        }
        return join(strings, " ");
    }

    private static String transformerUnderbeskrivelse(String kontoBeskrUnder, String kontoBeskrHoved) {
        return (kontoBeskrUnder != null && !kontoBeskrUnder.isEmpty() ? kontoBeskrUnder : kontoBeskrHoved);
    }
}
