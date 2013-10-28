package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;

import java.io.Serializable;

public class PosteringsDetalj implements Serializable {
    private String hovedBeskrivelse;
    private String underBeskrivelse;
    private String kontoNr;

    public PosteringsDetalj(String hovedBeskrivelse, String underBeskrivelse, String kontoNr) {
        this.hovedBeskrivelse = hovedBeskrivelse;
        this.underBeskrivelse = underBeskrivelse;
        this.kontoNr = kontoNr;
    }

    public PosteringsDetalj(WSPosteringsdetaljer wsPosteringsdetaljer) {
        this.hovedBeskrivelse = wsPosteringsdetaljer.getKontoBeskrHoved();
        this.underBeskrivelse = wsPosteringsdetaljer.getKontoBeskrUnder();
        this.kontoNr = wsPosteringsdetaljer.getKontonr();

    }

    public String getHovedBeskrivelse() {
        return hovedBeskrivelse;
    }

    public String getUnderBeskrivelse() {
        return underBeskrivelse;
    }

    public String getKontoNr() {
        return kontoNr;
    }

    // CHECKSTYLE:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PosteringsDetalj that = (PosteringsDetalj) o;

        if (hovedBeskrivelse != null ? !hovedBeskrivelse.equals(that.hovedBeskrivelse) : that.hovedBeskrivelse != null)
            return false;
        if (underBeskrivelse != null ? !underBeskrivelse.equals(that.underBeskrivelse) : that.underBeskrivelse != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hovedBeskrivelse != null ? hovedBeskrivelse.hashCode() : 0;
        result = 31 * result + (underBeskrivelse != null ? underBeskrivelse.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PosteringsDetalj{" +
                "hovedBeskrivelse='" + hovedBeskrivelse + '\'' +
                ", underBeskrivelse='" + underBeskrivelse + '\'' +
                '}';
    }
    // CHECKSTYLE:ON
}
