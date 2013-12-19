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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
    public static final Transformer<Underytelse, Double> UTBETALT_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            double belop = underytelse.getBelop();
            return (belop >= 0.0 ? belop : 0.0);
        }
    };
    public static final Transformer<Underytelse, Double> TREKK_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            double belop = underytelse.getBelop();
            return (belop < 0.0 ? belop : 0.0);
        }
    };
    public static final Transformer<Underytelse, String> UNDERYTELSE_TITTEL = new Transformer<Underytelse, String>() {
        @Override
        public String transform(Underytelse underytelse) {
            return underytelse.getTittel();
        }
    };
    public static final Transformer<Underytelse, Double> UNDERYTELSE_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            return underytelse.getBelop();
        }
    };
//
//    public static Map<String, Map<String, Double>> summerBelopForUnderytelser(List<WSUtbetaling> utbetalinger) {
//        Map<String, List<WSPosteringsdetaljer>> perHovedYtelse =
//                on(utbetalinger)
//                        .flatmap(BILAG)
//                        .flatmap(POSTERINGSDETALJER)
//                        .reduce(indexBy(HOVEDBESKRIVELSE));
//
//        Map<String, Map<String, Double>> resultat = getSumPerHovedYtelse(perHovedYtelse);
//        return resultat;
//    }

    private static Double getBrutto(List<Underytelse> underytelser) {
        return on(underytelser).map(UTBETALT_BELOP).reduce(sumDouble);
    }

    private static Double getTrekk(List<Underytelse> underytelser) {
        return on(underytelser).map(TREKK_BELOP).reduce(sumDouble);
    }

    private static Set<String> getUnderytelsesTitler(List<Underytelse> underytelser) {
        return on(underytelser).map(UNDERYTELSE_TITTEL).collectIn(new HashSet<String>());
    }

    private static List<Underytelse> leggSammenUnderYtelser(List<Underytelse> underytelser) {
        ArrayList<Underytelse> ytelser = on(underytelser).collectIn(new ArrayList<Underytelse>());
        List<Underytelse> resultat = new ArrayList<>();

        while (ytelser.size() > 1) {
            Underytelse ytelse1 = ytelser.get(0);
            Set<Underytelse> alleredeLagtTil = new HashSet<>();
            for (Underytelse ytelse2 : ytelser.subList(1, ytelser.size())) {
                if (ytelse1.equals(ytelse2)) {
                    Underytelse underytelse = mergeLikeUnderYtelser(ytelse1.getTittel(), ytelse1, ytelse2);
                    resultat.add(underytelse);
                } else {
                    resultat.add(ytelse2);
                }
                alleredeLagtTil.add(ytelse2);
            }
            ytelser.removeAll(alleredeLagtTil);
        }
        return resultat;
    }

    private static Underytelse mergeLikeUnderYtelser(String tittel, Underytelse a, Underytelse b) {
        Double belop = a.getBelop() + b.getBelop();
        Set<String> spesifikasjoner = new HashSet<>();
        spesifikasjoner.addAll(Arrays.asList(a.getSpesifikasjon(), b.getSpesifikasjon()));
        String spesifikasjon = join(spesifikasjoner, ". ");
        return new Underytelse(tittel, spesifikasjon, a.getAntall(), belop, a.getSats());
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
                Set<String> meldinger = new HashSet<>();
                for (UtbetalingTransformObjekt objekt : transformObjektListe.subList(1, transformObjektListe.size())) {
                    if (forsteObjektIListe.equals(objekt)) {
                        meldinger.add(objekt.getMelding());
                        underytelser.add(new Underytelse(objekt.getUnderYtelse(), forsteObjektIListe.getSpesifikasjon(), objekt.getAntall(), objekt.getBelop(), objekt.getSats()));
                        skalFjernes.add(objekt);
                    }
                }
                transformObjektListe.removeAll(skalFjernes);

                utbetalingBuilder.withMelding(join(meldinger, ". "));
                leggSammenBelop(utbetalingBuilder, underytelser);

                underytelser = leggSammenUnderYtelser(underytelser);
                nyeUtbetalinger.add(utbetalingBuilder.withUnderytelser(underytelser).createUtbetaling());
            }
        }
        Collections.sort(nyeUtbetalinger, UTBETALING_DATO);
        return nyeUtbetalinger;
    }

    private static void leggSammenBelop(UtbetalingBuilder utbetalingBuilder, List<Underytelse> underytelser) {
        Double brutto = getBrutto(underytelser);
        Double trekk = getTrekk(underytelser);
        utbetalingBuilder.withBrutto(brutto);
        utbetalingBuilder.withTrekk(trekk);
        utbetalingBuilder.withUtbetalt(brutto + trekk);
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

    public static List<Utbetaling> createUtbetalinger(List<WSUtbetaling> wsUtbetalinger) {
        transformerSkatt(wsUtbetalinger);
        List<UtbetalingTransformObjekt> transformObjekter = createTransformObjekter(wsUtbetalinger);

        Map<LocalDate, List<UtbetalingTransformObjekt>> listMap = trekkUtTransformObjekterFraSammeDag(transformObjekter);
        return transformerTilUtbetalinger(listMap.values());
    }

    static Map<LocalDate, List<UtbetalingTransformObjekt>> trekkUtTransformObjekterFraSammeDag(List<UtbetalingTransformObjekt> transformObjekter) {
        Collections.sort(transformObjekter, DATO);
        return on(transformObjekter).reduce(indexBy(UTBETALINGS_DAG));
    }

    static void transformerSkatt(List<WSUtbetaling> wsUtbetalinger) {
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                trekkUtSkatteOpplysninger(wsBilag.getPosteringsdetaljerListe());
            }
        }
    }

    static List<UtbetalingTransformObjekt> createTransformObjekter(List<WSUtbetaling> wsUtbetalinger) {

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
