package no.nav.sbl.dialogarena.utbetaling.domain;

public class PosteringsDetaljBuilder {

    private String hovedBeskrivelse = "Alderspensjon";
    private String underdBeskrivelse = "hovedutbetaling";

    public PosteringsDetaljBuilder setHovedBeskrivelse(String hovedBeskrivelse) {
        this.hovedBeskrivelse = hovedBeskrivelse;
        return this;
    }

    public PosteringsDetaljBuilder setUnderdBeskrivelse(String underdBeskrivelse) {
        this.underdBeskrivelse = underdBeskrivelse;
        return this;
    }

    public PosteringsDetalj createPosteringsDetalj() {
        return new PosteringsDetalj(hovedBeskrivelse, underdBeskrivelse);
    }
}