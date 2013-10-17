package no.nav.sbl.dialogarena.utbetaling.domain;

public class PosteringsDetalj {
    private String hovedBeskrivelse;
    private String underdBeskrivelse;

    public PosteringsDetalj(String hovedBeskrivelse, String underdBeskrivelse) {
        this.hovedBeskrivelse = hovedBeskrivelse;
        this.underdBeskrivelse = underdBeskrivelse;
    }

    public String getHovedBeskrivelse() {
        return hovedBeskrivelse;
    }

    public String getUnderdBeskrivelse() {
        return underdBeskrivelse;
    }


}
