package no.nav.kjerneinfo.domain.person.fakta;

import no.nav.kjerneinfo.domain.person.Person;

import java.io.Serializable;

public class Familierelasjon implements Serializable {

    private String tilRolle;
    private Person tilPerson;
    private boolean harSammeBosted;

    public String getTilRolle() {
        return tilRolle;
    }

    public void setTilRolle(String value) {
        this.tilRolle = value;
    }

    public Person getTilPerson() {
        return tilPerson;
    }

    public void setTilPerson(Person tilPerson) {
        this.tilPerson = tilPerson;
    }

    public boolean getHarSammeBosted() {
        return harSammeBosted;
    }

    public void setHarSammeBosted(boolean harSammeBosted) {
        this.harSammeBosted = harSammeBosted;
    }
}
