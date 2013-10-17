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

    // CHECKSTYLE:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PosteringsDetalj that = (PosteringsDetalj) o;

        if (hovedBeskrivelse != null ? !hovedBeskrivelse.equals(that.hovedBeskrivelse) : that.hovedBeskrivelse != null)
            return false;
        if (underdBeskrivelse != null ? !underdBeskrivelse.equals(that.underdBeskrivelse) : that.underdBeskrivelse != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hovedBeskrivelse != null ? hovedBeskrivelse.hashCode() : 0;
        result = 31 * result + (underdBeskrivelse != null ? underdBeskrivelse.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PosteringsDetalj{" +
                "hovedBeskrivelse='" + hovedBeskrivelse + '\'' +
                ", underdBeskrivelse='" + underdBeskrivelse + '\'' +
                '}';
    }
    // CHECKSTYLE:ON
}
