package no.nav.sbl.dialogarena.utbetaling.domain;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class BilagBuilder {

    private String melding = "Bilagsmelding ... Skatt 14%";
    private List<PosteringsDetalj> posteringsDetaljer = new ArrayList<>();

    public BilagBuilder() {
        posteringsDetaljer.addAll(asList(
                new PosteringsDetaljBuilder().createPosteringsDetalj(),
                new PosteringsDetaljBuilder().createPosteringsDetalj(),
                new PosteringsDetaljBuilder().createPosteringsDetalj()
        ));
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