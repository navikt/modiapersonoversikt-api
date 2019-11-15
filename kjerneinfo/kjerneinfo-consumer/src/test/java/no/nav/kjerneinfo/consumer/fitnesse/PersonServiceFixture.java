package no.nav.kjerneinfo.consumer.fitnesse;

import no.nav.kjerneinfo.consumer.stub.PersonServiceStub;
import no.nav.kjerneinfo.domain.person.Fodselsnummer;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;

public class PersonServiceFixture {

    private String fnr;

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public String hentFornavn() {
        PersonServiceStub tjeneste = new PersonServiceStub();

        Person stub = tjeneste.hentPerson(new Fodselsnummer(fnr));
        Personfakta personfakta = stub.getPersonfakta();
        String navn = personfakta.getPersonnavn().getFornavn();
        return String.format("%s", navn);
    }

}
