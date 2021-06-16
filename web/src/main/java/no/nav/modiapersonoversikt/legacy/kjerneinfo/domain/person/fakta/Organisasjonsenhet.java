package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta;

import java.io.Serializable;

public class Organisasjonsenhet implements Serializable {

    private String geografiskOmrade;
    private String organisasjonselementNavn = "";
    private String organisasjonselementId;

    public String getOrganisasjonselementId() {
        return organisasjonselementId;
    }

    public void setOrganisasjonselementId(final String organisasjonselementId) {
        this.organisasjonselementId = organisasjonselementId;
    }

    public String getGeografiskOmrade() {
        return geografiskOmrade;
    }

    public void setGeografiskOmrade(final String geografiskOmrade) {
        this.geografiskOmrade = geografiskOmrade;
    }

    public String getOrganisasjonselementNavn() {
        return organisasjonselementNavn;
    }

    public void setOrganisasjonselementNavn(final String organisasjonselementNavn) {
        this.organisasjonselementNavn = organisasjonselementNavn;
    }

    public static class With {
        private final Organisasjonsenhet ansvarligEnhet = new Organisasjonsenhet();

        public With geografiskOmrade(final String geografiskOmrade) {
            ansvarligEnhet.setGeografiskOmrade(geografiskOmrade);
            return this;
        }

        public With organisasjonselementNavn(final String organisasjonselementNavn) {
            ansvarligEnhet.setOrganisasjonselementNavn(organisasjonselementNavn);
            return this;
        }

        public With organisasjonselementId(final String organisasjonselementId) {
            ansvarligEnhet.setOrganisasjonselementId(organisasjonselementId);
            return this;
        }

        public Organisasjonsenhet done() {
            return ansvarligEnhet;
        }
    }
}
