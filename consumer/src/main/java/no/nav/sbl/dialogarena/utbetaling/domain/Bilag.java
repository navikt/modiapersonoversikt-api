package no.nav.sbl.dialogarena.utbetaling.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Bilag  implements Serializable {

    private String melding;
    private List<PosteringsDetalj> posteringsDetaljer;

    public Bilag(String melding, List<PosteringsDetalj> posteringsDetaljer) {
        this.melding = melding;
        this.posteringsDetaljer = posteringsDetaljer;
    }


    public String getMelding() {
        return melding;
    }

    public List<PosteringsDetalj> getPosteringsDetaljer() {
        return posteringsDetaljer;
    }


    public Set<String> getBeskrivelser() {
        Set<String> beskrivelser = new TreeSet<>();
        for (PosteringsDetalj detalj : posteringsDetaljer) {
            beskrivelser.add(detalj.getHovedBeskrivelse());
        }
        return beskrivelser;
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
}
