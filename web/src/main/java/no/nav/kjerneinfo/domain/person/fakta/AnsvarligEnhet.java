package no.nav.kjerneinfo.domain.person.fakta;

import java.io.Serializable;

public class AnsvarligEnhet implements Serializable {

    private Organisasjonsenhet organisasjonsenhet;

    public Organisasjonsenhet getOrganisasjonsenhet() {
        return organisasjonsenhet;
    }

    public void setOrganisasjonsenhet(Organisasjonsenhet organisasjonsenhet) {
        this.organisasjonsenhet = organisasjonsenhet;
    }

    public static class With {
        private final AnsvarligEnhet ansvarligEnhet = new AnsvarligEnhet();

        public With organisasjonsenhet(Organisasjonsenhet organisasjonsenhet) {
            ansvarligEnhet.organisasjonsenhet = organisasjonsenhet;
            return this;
        }

        public AnsvarligEnhet done() {
            return ansvarligEnhet;
        }
    }
}
