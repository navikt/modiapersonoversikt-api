package no.nav.personsok.consumer.fim.personsok;

import no.nav.personsok.consumer.fim.kodeverk.support.MockKodeverkManager;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.mapping.PersonMockMapper;
import no.nav.personsok.consumer.fim.personsok.mock.PersonMock;
import no.nav.personsok.consumer.fim.personsok.mock.PersonMockFactory;
import no.nav.personsok.consumer.fim.personsok.mock.PersonsokServiceMock;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.personsok.domain.UtvidetPersonsok;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonForMangeForekomster;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonUgyldigInput;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimPerson;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Tester at mocktjenesten er riktig satt opp returnerer forventet svar
 */
public class PersonsokServiceMockTest {

    private PersonsokServiceMock serviceMock;
    private static final String FORNAVN = "Fornavn";
    private static final String MELLOMNAVN = "Mellomnavn";
    private static final String ETTERNAVN = "Etternavn";

    @Before
    public void setUp() {
        serviceMock = new PersonsokServiceMock();
        serviceMock.setMapper(new FIMMapper(new MockKodeverkManager()));
    }
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testServiceMock() throws FinnPersonForMangeForekomster, FinnPersonUgyldigInput {

        PersonMock fromPerson = PersonMockFactory.createPersonMock();
        FimPerson toPerson = PersonMockMapper.map(fromPerson);

        serviceMock.addPerson(toPerson);

        UtvidetPersonsok utvidetPersonsok = new UtvidetPersonsok();
        utvidetPersonsok.setFornavn(FORNAVN);
        FinnPersonRequest finnPersonRequest = new FinnPersonRequest();
        finnPersonRequest.setUtvidetPersonsok(utvidetPersonsok);

        FinnPersonResponse response = serviceMock.finnPerson(finnPersonRequest);

        assertEquals(1, response.getTotaltAntallTreff());
        assertEquals(ETTERNAVN + ", " + FORNAVN + " " + MELLOMNAVN, response.getPersonListe().get(0).getNavn());
    }

    @Test
    public void testTooManyResults() throws FinnPersonForMangeForekomster, FinnPersonUgyldigInput {
        PersonMock fromPerson = PersonMockFactory.createPersonMock();
        FimPerson toPerson = PersonMockMapper.map(fromPerson);

        serviceMock.addPerson(toPerson);

        UtvidetPersonsok utvidetPersonsok = new UtvidetPersonsok();
        utvidetPersonsok.setFornavn(FORNAVN);
        utvidetPersonsok.setEtternavn("Max");
        FinnPersonRequest finnPersonRequest = new FinnPersonRequest();
        finnPersonRequest.setUtvidetPersonsok(utvidetPersonsok);

        thrown.expect(FinnPersonForMangeForekomster.class);
        FinnPersonResponse response = serviceMock.finnPerson(finnPersonRequest);
        thrown = ExpectedException.none();
    }
}
