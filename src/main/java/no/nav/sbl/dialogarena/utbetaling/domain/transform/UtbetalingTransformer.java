package no.nav.sbl.dialogarena.utbetaling.domain.transform;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.TransformComparator.MERGEABLE_ALLE_FELTER;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.TransformComparator.MERGEABLE_DATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.getBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.MergeUtil.merge;
import static org.apache.commons.lang3.StringUtils.join;

public final class UtbetalingTransformer {

    public static List<Utbetaling> createUtbetalinger(List<WSUtbetaling> wsUtbetalinger, String fnr) {
        transformerSkatt(wsUtbetalinger);
        List<UtbetalingTransformObjekt> transformObjekter = createTransformObjekter(wsUtbetalinger, fnr);

        return transformerTilUtbetalinger(transformObjekter, MERGEABLE_ALLE_FELTER);
    }

    static List<UtbetalingTransformObjekt> createTransformObjekter(List<WSUtbetaling> wsUtbetalinger, String fnr) {

        List<UtbetalingTransformObjekt> list = new ArrayList<>();
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            WSMottaker wsMottaker = wsUtbetaling.getUtbetalingMottaker();
            String mottakerId = wsMottaker != null ? wsMottaker.getMottakerId() : "";
            String mottaker = wsMottaker != null ? wsMottaker.getNavn() : "";
            String mottakerKode = transformMottakerKode(wsMottaker, fnr);

            Interval periode = wsUtbetaling.getUtbetalingsPeriode() != null ?
                    new Interval(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato(), wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato())
                    : new Interval(0, 0);
            String delimiter = ", ";

            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                String melding = transformMelding(wsBilag, delimiter);

                for (WSPosteringsdetaljer detalj : wsBilag.getPosteringsdetaljerListe()) {
                    UtbetalingTransformObjekt transformObjekt = getBuilder()
                            .withAntall(detalj.getAntall())
                            .withBelop(detalj.getBelop())
                            .withHovedYtelse(detalj.getKontoBeskrHoved())
                            .withKontonummer(wsUtbetaling.getGironr())
                            .withMottaker(mottaker)
                            .withMottakerId(mottakerId)
                            .withMottakerKode(mottakerKode)
                            .withSats(detalj.getSats())
                            .withStatus(wsUtbetaling.getStatusBeskrivelse())
                            .withSpesifikasjon(detalj.getSpesifikasjon())
                            .withUnderYtelse(transformerUnderbeskrivelse(detalj.getKontoBeskrUnder(), detalj.getKontoBeskrHoved()))
                            .withUtbetalingsDato(wsUtbetaling.getUtbetalingDato())
                            .withPeriode(periode)
                            .withValuta(wsUtbetaling.getValuta())
                            .build();
                    transformObjekt.setMelding(melding);
                    list.add(transformObjekt);
                }
            }
        }
        return list;
    }

    /**
     * Tar inn en collection av lister av transformobjekter. I hver liste har objektene samme dato.
     */
    private static List<Utbetaling> transformerTilUtbetalinger(List<UtbetalingTransformObjekt> transformObjekter, Comparator<Mergeable<Utbetaling>> comparator) {
        return merge(new ArrayList<Mergeable<Utbetaling>>(transformObjekter), comparator, MERGEABLE_DATO);
    }


    static String transformMottakerKode(WSMottaker wsMottaker, String fnr) {
        if (wsMottaker != null && !fnr.equals(wsMottaker.getMottakerId())) {
            return ARBEIDSGIVER;
        }
        return BRUKER;
    }

    static void transformerSkatt(List<WSUtbetaling> wsUtbetalinger) {
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                trekkUtSkatteOpplysninger(wsBilag.getPosteringsdetaljerListe());
            }
        }
    }

    /**
     * Kobler skattetrekket til ytelsen i bilaget.
     * Forutsetter at alle posteringsdetaljer i et bilag har samme hovedytelse.
     */
    private static void trekkUtSkatteOpplysninger(List<WSPosteringsdetaljer> detaljer) {
        List<WSPosteringsdetaljer> skatteDetaljer = new ArrayList<>();
        for (WSPosteringsdetaljer detalj : detaljer) {
            if ("SKATT".equalsIgnoreCase(detalj.getKontoBeskrHoved())) {
                skatteDetaljer.add(detalj);
            }
        }
        if (skatteDetaljer.isEmpty() || detaljer.isEmpty()) {
            return;
        }
        WSPosteringsdetaljer detalj = detaljer.get(0);
        String beskrivelse = detalj.getKontoBeskrHoved();
        for (WSPosteringsdetaljer skatt : skatteDetaljer) {
            skatt.setKontoBeskrHoved(beskrivelse);
        }
    }


    private static String transformerUnderbeskrivelse(String kontoBeskrUnder, String kontoBeskrHoved) {
        return (kontoBeskrUnder != null && !kontoBeskrUnder.isEmpty() ? kontoBeskrUnder : kontoBeskrHoved);
    }

    private static String transformMelding(WSBilag wsBilag, String delimiter) {
        List<WSMelding> meldingListe = wsBilag.getMeldingListe();
        List<String> strings = new ArrayList<>();
        for (WSMelding wsMelding : meldingListe) {
            strings.add(wsMelding.getMeldingtekst());
        }
        return join(strings, delimiter);
    }
}
