package no.nav.kjerneinfo.domain.person;

import no.nav.kjerneinfo.common.domain.Kodeverdi;

import java.io.Serializable;

public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    private int personId;
    private Fodselsnummer fodselsnummer;
    private Personfakta personfakta;
    private boolean hideFodselsnummerOgNavn;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public Fodselsnummer getFodselsnummer() {
        return fodselsnummer;
    }

    public void setFodselsnummer(Fodselsnummer fodselsnummer) {
        this.fodselsnummer = fodselsnummer;
    }

    public Personfakta getPersonfakta() {
        return personfakta;
    }

    public void setPersonfakta(Personfakta personfaktaGruppe) {
        this.personfakta = personfaktaGruppe;
    }

    /**
     * Brukes for sjule når saksbehandler ikke har tilgang til familierelasjon med diskresjon.
     *
     * @return
     */
    public boolean isHideFodselsnummerOgNavn() {
        return hideFodselsnummerOgNavn;
    }

    public void setHideFodselsnummerOgNavn(boolean hideFodselsnummerOgNavn) {
        this.hideFodselsnummerOgNavn = hideFodselsnummerOgNavn;
    }

    public boolean kanEndreNavn() {
        if (fodselsnummer.isDnummer()) {
            return true;
        }

        Kodeverdi bostatus = personfakta.getBostatus();

        return bostatus != null && "UTVA".equals(personfakta.getBostatus().getKodeRef());
    }

    @Override
    public String toString() {
        return "Person ["
                + "personId=" + personId + ", "
                + "fodselsnummer=" + fodselsnummer + ", "
                + "personfakta=" + personfakta
                + "]";
    }

    public static class With {

        private final Person person = new Person();

        public With() {
            person.setFodselsnummer(new Fodselsnummer());
            person.setPersonfakta(new Personfakta());
        }

        public With fodselsnummer(String fodselsnummer) {
            person.fodselsnummer = new Fodselsnummer(fodselsnummer);
            return this;
        }

        public With personfakta(Personfakta fakta) {
            person.setPersonfakta(fakta);
            return this;
        }

        public Person done() {
            return person;
        }
    }
}
