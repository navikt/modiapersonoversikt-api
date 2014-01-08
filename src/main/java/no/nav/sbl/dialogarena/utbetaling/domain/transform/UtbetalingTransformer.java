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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseComparator.TITTEL_ANTALL_SATS;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.TransformComparator;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.getBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UnderYtelseUtil.getBrutto;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UnderYtelseUtil.getTrekk;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UnderYtelseUtil.leggSammenUnderYtelser;
import static org.apache.commons.lang3.StringUtils.join;

public final class UtbetalingTransformer {

    public static List<Utbetaling> createUtbetalinger(List<WSUtbetaling> wsUtbetalinger, String fnr) {
        transformerSkatt(wsUtbetalinger);
        List<UtbetalingTransformObjekt> transformObjekter = createTransformObjekter(wsUtbetalinger, fnr);

        return transformerTilUtbetalinger(transformObjekter);
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
    private static List<Utbetaling> transformerTilUtbetalinger(List<UtbetalingTransformObjekt> transformObjekter) {
        List<Utbetaling> nyeUtbetalinger = new ArrayList<>();

        LinkedList<UtbetalingTransformObjekt> nyListe = on(transformObjekter).collectIn(new LinkedList<UtbetalingTransformObjekt>());
        sort(nyListe, TransformComparator.DATO);

        while (nyListe.size() >= 1) {
            // sammenlign det første elementet i lista med alle andre elementer
            UtbetalingTransformObjekt forst = nyListe.get(0);

            UtbetalingBuilder utbetalingBuilder = lagUtbetalingBuilder(forst);
            List<Underytelse> underytelser = new ArrayList<>();
            underytelser.add(new Underytelse(forst.getUnderYtelse(), forst.getSpesifikasjon(), forst.getAntall(), forst.getBelop(), forst.getSats()));

            List<UtbetalingTransformObjekt> skalMerges = new ArrayList<>();
            skalMerges.add(forst);

            for (UtbetalingTransformObjekt objekt : nyListe.subList(1, nyListe.size())) {
                if (forst.equals(objekt)) {
                    underytelser.add(new Underytelse(objekt.getUnderYtelse(), forst.getSpesifikasjon(), objekt.getAntall(), objekt.getBelop(), objekt.getSats()));
                    skalMerges.add(objekt);
                }
            }
            mergeLikeObjekter(skalMerges, utbetalingBuilder, underytelser);
            nyListe.removeAll(skalMerges);

            nyeUtbetalinger.add(utbetalingBuilder.createUtbetaling());
        }

        return nyeUtbetalinger;
    }

    /**
     * Setter sammen meldinger, underytelser og belop
     */
    private static void mergeLikeObjekter(List<UtbetalingTransformObjekt> skalMerges, UtbetalingBuilder utbetalingBuilder, List<Underytelse> underytelser) {
        Set<String> meldinger = on(skalMerges).map(UtbetalingTransformObjekt.MELDING).collectIn(new HashSet<String>());
        String melding = join(meldinger, ". ");

        LinkedList<Underytelse> list = new LinkedList<>(underytelser);
        leggSammenBelop(utbetalingBuilder, underytelser);

        List<Underytelse> sammenlagteUnderytelser = leggSammenUnderYtelser(list, TITTEL_ANTALL_SATS);
        utbetalingBuilder.withUnderytelser(sammenlagteUnderytelser).withMelding(melding);
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

    private static void leggSammenBelop(UtbetalingBuilder utbetalingBuilder, List<Underytelse> underytelser) {
        Double brutto = getBrutto(underytelser);
        Double trekk = getTrekk(underytelser);
        utbetalingBuilder.withBrutto(brutto);
        utbetalingBuilder.withTrekk(trekk);
        utbetalingBuilder.withUtbetalt(brutto + trekk);
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

    private static UtbetalingBuilder lagUtbetalingBuilder(UtbetalingTransformObjekt objekt) {
        return Utbetaling.getBuilder()
                .withHovedytelse(objekt.getHovedYtelse())
                .withKontonr(objekt.getKontonummer())
                .withMelding(objekt.getMelding())
                .withStatus(objekt.getStatus())
                .withPeriode(objekt.getPeriode())
                .withValuta(objekt.getValuta())
                .withMottakernavn(objekt.getMottaker())
                .withMottakerId(objekt.getMottakerId())
                .withMottakerkode(objekt.getMottakerKode())
                .withUtbetalingsDato(objekt.getUtbetalingsdato());
    }


}
