package no.nav.sbl.dialogarena.utbetaling.domain;

public class PosteringsDetaljBuilder {

    private String hovedBeskrivelse = "Alderspensjon";
    private String underBeskrivelse = "hovedutbetaling";
    private Double sats = 150.0;
    private Integer antall = 2;
    private String kontoNr = "12345678900";
    private Double belop = 1000.0;

    public PosteringsDetaljBuilder setKontoNr(String kontoNr) {
        this.kontoNr = kontoNr;
        return this;
    }

    public PosteringsDetaljBuilder setSats(Double sats) {
        this.sats = sats;
        return this;
    }

    public PosteringsDetaljBuilder setAntall(Integer antall) {
        this.antall = antall;
        return this;
    }

    public PosteringsDetaljBuilder setHovedBeskrivelse(String hovedBeskrivelse) {
        this.hovedBeskrivelse = hovedBeskrivelse;
        return this;
    }

    public PosteringsDetaljBuilder setUnderBeskrivelse(String underBeskrivelse) {
        this.underBeskrivelse = underBeskrivelse;
        return this;
    }

    public PosteringsDetalj createPosteringsDetalj() {
        return new PosteringsDetalj(hovedBeskrivelse, underBeskrivelse, kontoNr, sats, antall, belop);
    }

}