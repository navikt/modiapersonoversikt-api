package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalToIgnoreCase;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj.POSTERINGS_DETALJ_HOVEDBESKRIVELSE_TRANSFORMER;
import static no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj.POSTERINGS_DETALJ_KONTONR_TRANSFORMER;
import static org.apache.commons.lang3.StringUtils.join;

public class Bilag implements Serializable {

    public static final String SKATT = "skatt";
    private String melding;
    private List<PosteringsDetalj> posteringsDetaljer;
    private Periode periode;

    Bilag(String melding, List<PosteringsDetalj> posteringsDetaljer, Periode periode) {
        this.melding = melding;
        this.posteringsDetaljer = posteringsDetaljer;
        this.periode = periode;
    }

    public Bilag(WSBilag wsBilag) {
        melding = transformMelding(wsBilag);
        posteringsDetaljer = new ArrayList<>();
        periode = new Periode(wsBilag.getBilagPeriode());
        transformPosteringsDetaljer(wsBilag.getPosteringsdetaljerListe());
    }

    public Map<String, Double> getBelopPerYtelse() {
        Map<String, Double> ytelsesBetaling = new HashMap<>();
        for (PosteringsDetalj detalj : posteringsDetaljer) {
            ytelsesBetaling.put(detalj.getHovedBeskrivelse(), detalj.getBelop());
        }
        return ytelsesBetaling;
    }

    public Periode getPeriode() {
        return periode;
    }

    public String getMelding() {
        return melding;
    }

    public List<PosteringsDetalj> getPosteringsDetaljer() {
        return posteringsDetaljer;
    }

    public Set<? extends String> getKontoNrFromDetaljer() {
        return on(posteringsDetaljer).map(POSTERINGS_DETALJ_KONTONR_TRANSFORMER).collectIn(new TreeSet<String>());
    }

    public Set<String> getBeskrivelserFromDetaljer() {
        return on(posteringsDetaljer)
                .filter(where(POSTERINGS_DETALJ_HOVEDBESKRIVELSE_TRANSFORMER, not(equalToIgnoreCase(SKATT))))
                .map(POSTERINGS_DETALJ_HOVEDBESKRIVELSE_TRANSFORMER).collectIn(new TreeSet<String>());
    }

    private String transformMelding(WSBilag wsBilag) {
        List<WSMelding> meldingListe = wsBilag.getMeldingListe();
        List<String> strings = new ArrayList<>();
        for (WSMelding wsMelding : meldingListe) {
            strings.add(wsMelding.getMeldingtekst());
        }
        return join(strings, ", ");
    }

    private void transformPosteringsDetaljer(List<WSPosteringsdetaljer> detaljer) {
        for (WSPosteringsdetaljer wsPosteringsdetaljer : detaljer) {
            posteringsDetaljer.add(new PosteringsDetalj(wsPosteringsdetaljer));
        }
        trekkUtSkatteOpplysninger(posteringsDetaljer);
    }


    /**
     * Kobler skattetrekket til den ytelsen som gjenstas oftest i bilaget.
     */
    private static void trekkUtSkatteOpplysninger(List<PosteringsDetalj> detaljer) {
        List<PosteringsDetalj> skatteDetaljer = new ArrayList<>();
        for (PosteringsDetalj detalj : detaljer) {
            if (detalj.isSkatt()) {
                skatteDetaljer.add(detalj);
            }
        }
        if (skatteDetaljer.isEmpty()) {
            return;
        }
        PosteringsDetalj detalj = finnVanligsteYtelse(detaljer);
        String beskrivelse = detalj.getHovedBeskrivelse();
        for (PosteringsDetalj skatt : skatteDetaljer) {
            skatt.setHovedBeskrivelse(beskrivelse);
        }
    }

    /**
     * Henter ut ytelsen med høyest frekvens i listen av posteringsdetaljer
     */
    private static PosteringsDetalj finnVanligsteYtelse(List<PosteringsDetalj> detaljer1) {
        Map<String, Integer> frekvens = new HashMap<>();
        int highestCount = 0;
        PosteringsDetalj pdetalj = null;
        for (PosteringsDetalj detalj : detaljer1) {
            String key = detalj.getHovedBeskrivelse();
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
