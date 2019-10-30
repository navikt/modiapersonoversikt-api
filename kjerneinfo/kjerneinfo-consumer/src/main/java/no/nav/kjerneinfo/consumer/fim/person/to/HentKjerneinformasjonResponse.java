package no.nav.kjerneinfo.consumer.fim.person.to;

import no.nav.kjerneinfo.domain.person.Person;

import java.io.Serializable;

public class HentKjerneinformasjonResponse implements Serializable {

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
