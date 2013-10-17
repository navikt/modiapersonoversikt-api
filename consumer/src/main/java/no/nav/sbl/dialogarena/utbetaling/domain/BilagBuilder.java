package no.nav.sbl.dialogarena.utbetaling.domain;

import java.util.ArrayList;
import java.util.List;

public class BilagBuilder {

    private String melding = "Bilagsmelding ... Skatt 14%";
    private List<PosteringsDetalj> posteringsDetaljer = new ArrayList<PosteringsDetalj>() { };

    public BilagBuilder() {
        PosteringsDetalj posteringsDetalj = new PosteringsDetaljBuilder().createPosteringsDetalj();
        PosteringsDetalj posteringsDetalj1 = new PosteringsDetaljBuilder().createPosteringsDetalj();
        PosteringsDetalj posteringsDetalj2 = new PosteringsDetaljBuilder().createPosteringsDetalj();

        posteringsDetaljer.add(posteringsDetalj);
        posteringsDetaljer.add(posteringsDetalj1);
        posteringsDetaljer.add(posteringsDetalj2);
    }

    public BilagBuilder setMelding(String melding) {
        this.melding = melding;
        return this;
    }

    public BilagBuilder setPosteringsDetaljer(List<PosteringsDetalj> posteringsDetaljer) {
        this.posteringsDetaljer = posteringsDetaljer;
        return this;
    }

    public Bilag createBilag() {
        return new Bilag(melding, posteringsDetaljer);
    }
}