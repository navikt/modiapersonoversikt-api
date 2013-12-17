package no.nav.sbl.dialogarena.utbetaling.domain.transform;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformObjekt.TransformComparator.DATO;
import static org.apache.commons.lang3.StringUtils.join;

public class UtbetalingTransformer {

    public List<Utbetaling> createUtbetalinger(List<WSUtbetaling> wsUtbetalinger) {

        // 1.  gjør om skatt til posteringsdetalj:
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                trekkUtSkatteOpplysninger(wsBilag.getPosteringsdetaljerListe());
            }
        }
        // 2. gjør om alle WSUtbetalinger til transformobjekter
        List<UtbetalingTransformObjekt> transformObjekter = createTransformObjekter(wsUtbetalinger);

        // 3. hvis to transformobjekter er like,
        // slå sammen beløp og melding. Lag nye utbetalinger
        Collections.sort(transformObjekter, DATO);

        UtbetalingTransformObjekt forrige = null;
        String delimiter = ", ";
        Set<String> meldinger = new HashSet<>();
        List<Utbetaling> nyeUtbetalinger = new ArrayList<>();

        // TODO: Legg til underytelser

        for (int i = 0; i < transformObjekter.size(); i++) {
            UtbetalingTransformObjekt dette = transformObjekter.get(i);
            if (dette.equals(forrige)) {
                meldinger.add(dette.getMelding());
                dette.setBelop(forrige.getBelop() + dette.getBelop());
            } else {
                nyeUtbetalinger.add(lagNyUtbetaling(forrige, delimiter, meldinger));
                meldinger = new HashSet<>();
            }
            if (forrige != null) {
                meldinger.add(forrige.getMelding());
            }
            if(i == transformObjekter.size()-1) {
                nyeUtbetalinger.add(lagNyUtbetaling(dette, delimiter, meldinger));
            }
            forrige = dette;
        }

        return nyeUtbetalinger;
    }

    private Utbetaling lagNyUtbetaling(UtbetalingTransformObjekt transformObjekt, String delimiter, Set<String> meldinger) {
        if (transformObjekt != null && !meldinger.isEmpty()) {
            transformObjekt.setMelding(join(meldinger, delimiter));
        }
        return buildUtbetaling(transformObjekt);
    }

    private Utbetaling buildUtbetaling(UtbetalingTransformObjekt objekt) {
        return Utbetaling.getBuilder()
                .withHovedytelse(objekt.getHovedYtelse())
                // TODO
                .createUtbetaling();
    }

    private List<UtbetalingTransformObjekt> createTransformObjekter(List<WSUtbetaling> wsUtbetalinger) {

        List<UtbetalingTransformObjekt> list = new ArrayList<>();
        for (WSUtbetaling wsUtbetaling : wsUtbetalinger) {
            WSMottaker wsMottaker = wsUtbetaling.getUtbetalingMottaker();
            String mottakerId = wsMottaker != null ? wsMottaker.getMottakerId() : "";
            String mottaker = wsMottaker != null ? wsMottaker.getNavn() : "";

            Interval periode = wsUtbetaling.getUtbetalingsPeriode() != null?
                                    new Interval(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato(), wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato())
                                  : new Interval(0,0);
            List<String> meldinger = new ArrayList<>();
            String delimiter = ", ";

            UtbetalingTransformObjekt transformObjekt = null;
            for (WSBilag wsBilag : wsUtbetaling.getBilagListe()) {
                meldinger.add(transformMelding(wsBilag, delimiter));

                for (WSPosteringsdetaljer detalj : wsBilag.getPosteringsdetaljerListe()) {
                    transformObjekt = UtbetalingTransformObjekt.getBuilder()
                            .withAntall(detalj.getAntall())
                            .withBelop(detalj.getBelop())
                            .withHovedYtelse(detalj.getKontoBeskrHoved())
                            .withKontonummer(wsUtbetaling.getGironr())
                            .withMottaker(mottaker)
                            .withMottakerId(mottakerId)
                            .withSats(detalj.getSats())
                            .withStatus(wsUtbetaling.getStatusBeskrivelse())
                            .withUnderYtelse(detalj.getKontoBeskrUnder())
                            .withUtbetalingsDato(wsUtbetaling.getUtbetalingDato())
                            .withPeriode(periode)
                            .withValuta(wsUtbetaling.getValuta())
                            .build();
                }
            }
            String melding = join(meldinger, delimiter);
            if (transformObjekt != null) {  transformObjekt.setMelding(melding);  }
            list.add(transformObjekt);
        }
        return list;
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
