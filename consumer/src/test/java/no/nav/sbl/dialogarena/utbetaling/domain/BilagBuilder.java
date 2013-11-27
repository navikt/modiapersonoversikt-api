package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class BilagBuilder {

    private String melding = "Bilagsmelding ... Skatt 14%";
    private List<PosteringsDetalj> posteringsDetaljer = new ArrayList<>();
    private Periode periode = new Periode(new DateTime().minusDays(32), new DateTime().minusDays(2));

    public BilagBuilder() {
        posteringsDetaljer.addAll(asList(
                new PosteringsDetaljBuilder().createPosteringsDetalj(),
                new PosteringsDetaljBuilder().createPosteringsDetalj(),
                new PosteringsDetaljBuilder().createPosteringsDetalj()
        ));
    }

    public BilagBuilder setPeriode(Periode periode) {
        this.periode = periode;
        return this;
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
        return new Bilag(melding, posteringsDetaljer, periode);
    }

}