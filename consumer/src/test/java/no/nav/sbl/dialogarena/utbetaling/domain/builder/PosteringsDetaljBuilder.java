package no.nav.sbl.dialogarena.utbetaling.domain.builder;

import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;

public class PosteringsDetaljBuilder {

    private String hovedBeskrivelse = "Alderspensjon";
    private String underBeskrivelse = "hovedutbetaling";
    private String kontoNr = "12345678900";

    public PosteringsDetaljBuilder setHovedBeskrivelse(String hovedBeskrivelse) {
        this.hovedBeskrivelse = hovedBeskrivelse;
        return this;
    }

    public PosteringsDetaljBuilder setUnderBeskrivelse(String underBeskrivelse) {
        this.underBeskrivelse = underBeskrivelse;
        return this;
    }

    public PosteringsDetalj createPosteringsDetalj() {
        return new PosteringsDetalj(hovedBeskrivelse, underBeskrivelse, kontoNr);
    }

}