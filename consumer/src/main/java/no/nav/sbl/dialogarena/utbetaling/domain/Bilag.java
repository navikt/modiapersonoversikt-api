package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

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
import static no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj.HOVEDBESKRIVELSE;
import static no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj.KONTONR;
import static org.apache.commons.lang3.StringUtils.join;

public class Bilag implements Serializable {

    public static final String SKATT = "skatt";
    private String melding;
    private List<PosteringsDetalj> posteringsDetaljer;
    private DateTime startDato;
    private DateTime sluttDato;

    public Bilag(WSBilag wsBilag) {
        this.melding = transformMelding(wsBilag);
        this.posteringsDetaljer = new ArrayList<>();
        this.startDato = wsBilag.getBilagPeriode().getPeriodeFomDato();
        this.sluttDato = wsBilag.getBilagPeriode().getPeriodeTomDato();
        transformPosteringsDetaljer(wsBilag.getPosteringsdetaljerListe());
    }

    public DateTime getStartDato() {
        return startDato;
    }

    public DateTime getSluttDato() {
        return sluttDato;
    }

    public String getMelding() {
        return melding;
    }

    public List<PosteringsDetalj> getPosteringsDetaljer() {
        return posteringsDetaljer;
    }

    public Set<? extends String> getKontoNrFromDetaljer() {
        return on(posteringsDetaljer).map(KONTONR).collectIn(new TreeSet<String>());
    }

    public Set<String> getBeskrivelserFromDetaljer() {
        return on(posteringsDetaljer)
                .filter(where(HOVEDBESKRIVELSE, not(equalToIgnoreCase(SKATT))))
                .map(HOVEDBESKRIVELSE).collectIn(new TreeSet<String>());
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

    public static final Transformer<Bilag, List<PosteringsDetalj>> POSTERINGSDETALJER = new Transformer<Bilag, List<PosteringsDetalj>>() {
        @Override
        public List<PosteringsDetalj> transform(Bilag bilag) {
            return bilag.posteringsDetaljer;
        }
    };

}
