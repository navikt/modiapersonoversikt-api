package no.nav.sbl.dialogarena.utbetaling.domain.transform;


import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder.UtbetalingComparator.UTBETALING_DATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.TransformComparator.DATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.UTBETALINGS_DAG;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.getBuilder;
import static org.apache.commons.lang3.StringUtils.join;

public class UtbetalingTransformer {

    public static final Transformer<WSUtbetaling, List<WSBilag>> BILAG = new Transformer<WSUtbetaling, List<WSBilag>>() {
        @Override
        public List<WSBilag> transform(WSUtbetaling wsUtbetaling) {
            return wsUtbetaling.getBilagListe();
        }
    };
    public static final Transformer<WSBilag, List<WSPosteringsdetaljer>> POSTERINGSDETALJER = new Transformer<WSBilag, List<WSPosteringsdetaljer>>() {
        @Override
        public List<WSPosteringsdetaljer> transform(WSBilag wsBilag) {
            return wsBilag.getPosteringsdetaljerListe();
        }
    };
    public static final Transformer<WSPosteringsdetaljer, String> HOVEDBESKRIVELSE = new Transformer<WSPosteringsdetaljer, String>() {
        @Override
        public String transform(WSPosteringsdetaljer wsPosteringsdetaljer) {
            return wsPosteringsdetaljer.getKontoBeskrHoved();
        }
    };
    public static final Transformer<WSPosteringsdetaljer, String> UNDERBESKRIVELSE = new Transformer<WSPosteringsdetaljer, String>() {
        @Override
        public String transform(WSPosteringsdetaljer wsPosteringsdetaljer) {
            return wsPosteringsdetaljer.getKontoBeskrUnder();
        }
    };
    public static final Transformer<WSPosteringsdetaljer, Double> BELOP = new Transformer<WSPosteringsdetaljer, Double>() {
        @Override
        public Double transform(WSPosteringsdetaljer wsPosteringsdetaljer) {
            return wsPosteringsdetaljer.getBelop();
        }
    };

    public static Map<String, Map<String, Double>> summerBelopForUnderytelser(List<WSUtbetaling> utbetalinger) {
        Map<String, List<WSPosteringsdetaljer>> perHovedYtelse =
                on(utbetalinger)
                        .flatmap(BILAG)
                        .flatmap(POSTERINGSDETALJER)
                        .reduce(indexBy(HOVEDBESKRIVELSE));

        Map<String, Map<String, Double>> resultat = getSumPerHovedYtelse(perHovedYtelse);
        return resultat;
    }

    private static Map<String, Map<String, Double>> getSumPerHovedYtelse(Map<String, List<WSPosteringsdetaljer>> perHovedYtelse) {
        Map<String, Map<String, Double>> resultat = new HashMap<>();
        for (Map.Entry<String, List<WSPosteringsdetaljer>> hovedytelse : perHovedYtelse.entrySet()) {
            Map<String, List<WSPosteringsdetaljer>> perUnderytelse = on(hovedytelse.getValue()).reduce(indexBy(UNDERBESKRIVELSE));
            resultat.put(hovedytelse.getKey(), getSumPerUnderYtelse(perUnderytelse));
        }
        return resultat;
    }

    private static Map<String, Double> getSumPerUnderYtelse(Map<String, List<WSPosteringsdetaljer>> underytelser) {
        Map<String, Double> belopPerUnderytelse = new HashMap<>();
        for (Map.Entry<String, List<WSPosteringsdetaljer>> underytelse : underytelser.entrySet()) {
            Double sumBelop = on(underytelse.getValue()).map(BELOP).reduce(sumDouble);
            belopPerUnderytelse.put(underytelse.getKey(), sumBelop);
        }
        return belopPerUnderytelse;
    }

    public List<Utbetaling> createUtbetalinger(List<WSUtbetaling> wsUtbetalinger) {
        transformerSkatt(wsUtbetalinger);
        List<UtbetalingTransformObjekt> transformObjekter = createTransformObjekter(wsUtbetalinger);

        Map<LocalDate, List<UtbetalingTransformObjekt>> listMap = trekkUtTransformObjekterFraSammeDag(transformObjekter);
        return transformerTilUtbetalinger(listMap.values());
    }

    Map<LocalDate, List<UtbetalingTransformObjekt>> trekkUtTransformObjekterFraSammeDag(List<UtbetalingTransformObjekt> transformObjekter) {
        Collections.sort(transformObjekter, DATO);
        Map<LocalDate, List<UtbetalingTransformObjekt>> map = on(transformObjekter).reduce(indexBy(UTBETALINGS_DAG));
        return map;
    }


    static List<Utbetaling> transformerTilUtbetalinger(Collection<List<UtbetalingTransformObjekt>> transformObjekter) {
        List<Utbetaling> nyeUtbetalinger = new ArrayList<>();

        // trekk ut underytelser, legg i samme utbetaling
        for (List<UtbetalingTransformObjekt> transformObjektListe : transformObjekter) {
            while (transformObjektListe.size() > 1) {
                UtbetalingTransformObjekt forsteObjektIListe = transformObjektListe.get(0);
                UtbetalingBuilder utbetalingBuilder = lagUtbetalingBuilder(forsteObjektIListe);
                List<Underytelse> underytelser = new ArrayList<>();
                underytelser.add(new Underytelse(forsteObjektIListe.getUnderYtelse(), forsteObjektIListe.getSpesifikasjon(), forsteObjektIListe.getAntall(), forsteObjektIListe.getBelop(), forsteObjektIListe.getSats()));

                List<UtbetalingTransformObjekt> skalFjernes = new ArrayList<>();
                for (UtbetalingTransformObjekt objekt : transformObjektListe.subList(1, transformObjektListe.size())) {
                    if (forsteObjektIListe.equals(objekt)) {
                        underytelser.add(new Underytelse(objekt.getUnderYtelse(), forsteObjektIListe.getSpesifikasjon(), objekt.getAntall(), objekt.getBelop(), objekt.getSats()));
                        skalFjernes.add(objekt);
                    }
                }
                transformObjektListe.removeAll(skalFjernes);

                // TODO: slå sammen meldinger
                // TODO: slå sammen underytelser
                // TODO: regn ut brutto, trekk, utbetalt
                nyeUtbetalinger.add(utbetalingBuilder.withUnderytelser(underytelser).createUtbetaling());
            }
        }
        Collections.sort(nyeUtbetalinger, UTBETALING_DATO);
        return nyeUtbetalinger;
    }

    void transformerSkatt(List<WSUtbetaling> wsUtbetalinger) {
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                trekkUtSkatteOpplysninger(wsBilag.getPosteringsdetaljerListe());
            }
        }
    }

    List<UtbetalingTransformObjekt> createTransformObjekter(List<WSUtbetaling> wsUtbetalinger) {

        List<UtbetalingTransformObjekt> list = new ArrayList<>();
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            WSMottaker wsMottaker = wsUtbetaling.getUtbetalingMottaker();
            String mottakerId = wsMottaker != null ? wsMottaker.getMottakerId() : "";
            String mottaker = wsMottaker != null ? wsMottaker.getNavn() : "";

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
                            .withSats(detalj.getSats())
                            .withStatus(wsUtbetaling.getStatusBeskrivelse())
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
                .withUtbetalingsDato(objekt.getUtbetalingsDato());
    }

    private String transformerUnderbeskrivelse(String kontoBeskrUnder, String kontoBeskrHoved) {
        return (kontoBeskrUnder != null && !kontoBeskrUnder.isEmpty() ? kontoBeskrUnder : kontoBeskrHoved);
    }

    private void setMeldinger(UtbetalingTransformObjekt transformObjekt, String delimiter, Set<String> meldinger) {
        if (transformObjekt != null && !meldinger.isEmpty()) {
            transformObjekt.setMelding(join(meldinger, delimiter));
        }
    }

    private String transformMelding(WSBilag wsBilag, String delimiter) {
        List<WSMelding> meldingListe = wsBilag.getMeldingListe();
        List<String> strings = new ArrayList<>();
        for (WSMelding wsMelding : meldingListe) {
            strings.add(wsMelding.getMeldingtekst());
        }
        return join(strings, delimiter);
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
}
