package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.modig.lang.collections.PredicateUtils;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalToIgnoreCase;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj.POSTERINGS_DETALJ_HOVEDBESKRIVELSE_TRANSFORMER;
import static no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj.POSTERINGS_DETALJ_KONTONR_TRANSFORMER;

public class Bilag implements Serializable {

    public static final String SKATT = "skatt";
    private String melding;
    private List<PosteringsDetalj> posteringsDetaljer;

    public Bilag(String melding, List<PosteringsDetalj> posteringsDetaljer) {
        this.melding = melding;
        this.posteringsDetaljer = posteringsDetaljer;
    }

    public Bilag(WSBilag wsBilag) {
        melding = transformMelding(wsBilag);
        posteringsDetaljer = new ArrayList<>();
        for (WSPosteringsdetaljer wsPosteringsdetaljer : wsBilag.getPosteringsdetaljerListe()) {
            posteringsDetaljer.add(new PosteringsDetalj(wsPosteringsdetaljer));
        }
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


    // CHECKSTYLE:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bilag bilag = (Bilag) o;

        if (melding != null ? !melding.equals(bilag.melding) : bilag.melding != null) return false;
        if (posteringsDetaljer != null ? !posteringsDetaljer.equals(bilag.posteringsDetaljer) : bilag.posteringsDetaljer != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = melding != null ? melding.hashCode() : 0;
        result = 31 * result + (posteringsDetaljer != null ? posteringsDetaljer.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Bilag{" +
                "melding='" + melding + '\'' +
                ", posteringsDetaljer=" + posteringsDetaljer +
                '}';
    }

    // CHECKSTYLE:ON

    private String transformMelding(WSBilag wsBilag) {
        List<WSMelding> meldingListe = wsBilag.getMeldingListe();
        List<String> strings = new ArrayList<>();
        for (WSMelding wsMelding : meldingListe) {
            strings.add(wsMelding.getMeldingtekst());
        }

       return StringUtils.join(strings, ", ");
    }
}
