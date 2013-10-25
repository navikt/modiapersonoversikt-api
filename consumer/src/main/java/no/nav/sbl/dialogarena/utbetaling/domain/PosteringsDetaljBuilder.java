package no.nav.sbl.dialogarena.utbetaling.domain;

public class PosteringsDetaljBuilder {

    private String hovedBeskrivelse = "Alderspensjon";
    private String underBeskrivelse = "hovedutbetaling";
    private String kontoNr = "***REMOVED***";

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