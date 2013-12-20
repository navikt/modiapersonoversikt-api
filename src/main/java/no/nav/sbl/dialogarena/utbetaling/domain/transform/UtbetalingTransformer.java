package no.nav.sbl.dialogarena.utbetaling.domain.transform;


import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingComparator.UTBETALING_DATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.TransformComparator.DATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.UTBETALINGS_DAG;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.getBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UnderYtelseUtil.getBrutto;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UnderYtelseUtil.getTrekk;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UnderYtelseUtil.leggSammenUnderYtelser;
import static org.apache.commons.lang3.StringUtils.join;

public final class UtbetalingTransformer {

    public static List<Utbetaling> createUtbetalinger(List<WSUtbetaling> wsUtbetalinger, String fnr) {
        transformerSkatt(wsUtbetalinger);
        List<UtbetalingTransformObjekt> transformObjekter = createTransformObjekter(wsUtbetalinger, fnr);

        return transformerTilUtbetalinger(trekkUtTransformObjekterFraSammeDag(transformObjekter).values());
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

    private static List<Utbetaling> transformerTilUtbetalinger(Collection<List<UtbetalingTransformObjekt>> transformObjekter) {

        List<Utbetaling> nyeUtbetalinger = new ArrayList<>();

        // trekk ut underytelser, legg i samme utbetaling
        for (List<UtbetalingTransformObjekt> transformObjektListe : transformObjekter) {

            List<UtbetalingTransformObjekt> nyListe = on(transformObjektListe).collectIn(new ArrayList<UtbetalingTransformObjekt>());
            Collections.sort(nyListe, DATO);

            while (nyListe.size() > 1) {
                UtbetalingTransformObjekt forst = nyListe.get(0);

                UtbetalingBuilder utbetalingBuilder = lagUtbetalingBuilder(forst);
                List<Underytelse> underytelser = new ArrayList<>();
                underytelser.add(new Underytelse(forst.getUnderYtelse(), forst.getSpesifikasjon(), forst.getAntall(), forst.getBelop(), forst.getSats()));

                List<UtbetalingTransformObjekt> skalFjernes = new ArrayList<>();
                Set<String> meldinger = new HashSet<>();
                for (UtbetalingTransformObjekt objekt : nyListe.subList(1, nyListe.size())) {
                    if (forst.equals(objekt)) {
                        meldinger.add(objekt.getMelding());
                        underytelser.add(new Underytelse(objekt.getUnderYtelse(), forst.getSpesifikasjon(), objekt.getAntall(), objekt.getBelop(), objekt.getSats()));
                        skalFjernes.add(objekt);
                    }
                }
                nyListe.removeAll(skalFjernes);

                utbetalingBuilder.withMelding(join(meldinger, ". "));
                leggSammenBelop(utbetalingBuilder, underytelser);

                underytelser = leggSammenUnderYtelser(underytelser);
                nyeUtbetalinger.add(utbetalingBuilder.withUnderytelser(underytelser).createUtbetaling());
            }
        }
        Collections.sort(nyeUtbetalinger, UTBETALING_DATO);
        return nyeUtbetalinger;
    }

    static void transformerSkatt(List<WSUtbetaling> wsUtbetalinger) {
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                trekkUtSkatteOpplysninger(wsBilag.getPosteringsdetaljerListe());
            }
        }
    }

    /**
     * Kobler skattetrekket til den ytelsen som gjenstas oftest i bilaget.
     */
    private static void trekkUtSkatteOpplysninger(List<WSPosteringsdetaljer> detaljer) {
        List<WSPosteringsdetaljer> skatteDetaljer = new ArrayList<>();
        for (WSPosteringsdetaljer detalj : detaljer) {
            if ("SKATT".equalsIgnoreCase(detalj.getKontoBeskrHoved())) {
                skatteDetaljer.add(detalj);
            }
        }
        if (skatteDetaljer.isEmpty()) {
            return;
        }
        WSPosteringsdetaljer detalj = finnVanligsteYtelse(detaljer);
        String beskrivelse = detalj.getKontoBeskrHoved();
        for (WSPosteringsdetaljer skatt : skatteDetaljer) {
            skatt.setKontoBeskrHoved(beskrivelse);
        }
    }

    /**
     * Henter ut ytelsen med høyest frekvens i listen av posteringsdetaljer
     */
    private static WSPosteringsdetaljer finnVanligsteYtelse(List<WSPosteringsdetaljer> detaljer1) {
        Map<String, Integer> frekvens = new HashMap<>();
        int highestCount = 0;
        WSPosteringsdetaljer pdetalj = null;
        for (WSPosteringsdetaljer detalj : detaljer1) {
            String key = detalj.getKontoBeskrHoved();
            Integer count = 1 + (frekvens.get(key) != null ? frekvens.get(key) : 0);
            if (count > highestCount) {
                highestCount = count;
                pdetalj = detalj;
            }
            frekvens.put(key, count);
        }
        return pdetalj;
    }

    private static Map<LocalDate, List<UtbetalingTransformObjekt>> trekkUtTransformObjekterFraSammeDag(List<UtbetalingTransformObjekt> transformObjekter) {
        Collections.sort(transformObjekter, DATO);
        return on(transformObjekter).reduce(indexBy(UTBETALINGS_DAG));
    }

    private static void leggSammenBelop(UtbetalingBuilder utbetalingBuilder, List<Underytelse> underytelser) {
        Double brutto = getBrutto(underytelser);
        Double trekk = getTrekk(underytelser);
        utbetalingBuilder.withBrutto(brutto);
        utbetalingBuilder.withTrekk(trekk);
        utbetalingBuilder.withUtbetalt(brutto + trekk);
    }

    private static String transformMottakerKode(WSMottaker wsMottaker, String fnr) {
        if(wsMottaker == null) { return BRUKER; }
        if(!fnr.equals(wsMottaker.getMottakerId())) {
            return ARBEIDSGIVER;
        }
        return BRUKER;
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
