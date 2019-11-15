package no.nav.kjerneinfo.consumer;

import no.nav.kjerneinfo.consumer.stub.PersonServiceStub;
import no.nav.kjerneinfo.domain.person.Fodselsnummer;
import no.nav.kjerneinfo.domain.person.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PersonTjenesteTest {

    @Test
    public void testStub() {
        PersonServiceStub tjeneste = new PersonServiceStub();
        String fnr = "12346578910";
        Person stub = tjeneste.hentPerson(new Fodselsnummer(fnr));

        assertNotNull(stub);
        assertNotNull(stub.getFodselsnummer());
        assertNotNull(stub.getPersonfakta());
        assertNotNull(stub.getPersonId());

        Fodselsnummer stubFnr = stub.getFodselsnummer();
        assertEquals(fnr, stubFnr.getNummer());

    }
}
