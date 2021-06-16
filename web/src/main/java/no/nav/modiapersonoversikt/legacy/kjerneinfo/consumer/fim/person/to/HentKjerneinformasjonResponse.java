package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Person;

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
