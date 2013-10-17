package no.nav.sbl.dialogarena.utbetaling.domain;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Bilag {

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
        Set<String> beskrivelser = new TreeSet<String>();
        for (PosteringsDetalj detalj : posteringsDetaljer) {
            String beskrivelse = detalj.getHovedBeskrivelse();
            beskrivelser.add(beskrivelse);
        }
        return beskrivelser;
    }
}
