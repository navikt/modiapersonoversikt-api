package no.nav.sbl.dialogarena.utbetaling.domain;

import java.util.List;

public class Bilag {
    public Bilag(String melding, List<PosteringsDetalj> posteringsDetaljer) {
        this.melding = melding;
        this.posteringsDetaljer = posteringsDetaljer;
    }

    private String melding;
    private List<PosteringsDetalj> posteringsDetaljer;




}
