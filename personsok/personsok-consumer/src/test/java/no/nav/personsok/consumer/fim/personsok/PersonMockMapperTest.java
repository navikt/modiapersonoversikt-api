package no.nav.personsok.consumer.fim.personsok;

import no.nav.personsok.consumer.fim.mapping.PersonMockMapper;
import no.nav.personsok.consumer.fim.personsok.mock.GateadresseMock;
import no.nav.personsok.consumer.fim.personsok.mock.PersonMock;
import no.nav.personsok.consumer.fim.personsok.mock.PersonMockFactory;
import no.nav.personsok.consumer.fim.personsok.mock.UstrukturertadresseMock;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class PersonMockMapperTest {


	@Test
	public void testPersonMock() throws Exception {

		PersonMock personMock = PersonMockFactory.createPersonMock();

		Person person = PersonMockMapper.map(personMock);

		comparePerson(personMock, person);
	}

	@Test
	public void testBrukerMock() {
		PersonMock personMock = PersonMockFactory.createBrukerMock();

		Person person = PersonMockMapper.map(personMock);

		comparePerson(personMock, person);
	}

	@Test
	public void testBrukerUtenUtenlandsadresseMock() {
		PersonMock personMock = PersonMockFactory.createBrukerMock();
		personMock.setMidlertidigadresse(null);

		Person person = PersonMockMapper.map(personMock);

		comparePerson(personMock, person);
	}

	@Test
	public void testBrukerMedUtenlandsadresseMock() {
		PersonMock personMock = PersonMockFactory.createDollyDuck();

		Person person = PersonMockMapper.map(personMock);

		comparePerson(personMock, person);
	}


	private void comparePerson(PersonMock personMock, Person person) {
		compareFodselsnummer(personMock.getIdnummer(), person.getIdent());
		assertEquals(personMock.getIdenttype(), person.getIdent().getType().getValue());
		assertEquals(personMock.getSammensattNavn(), person.getPersonnavn().getSammensattNavn());
		assertEquals(personMock.getFornavn(), person.getPersonnavn().getFornavn());
		assertEquals(personMock.getMellomnavn(), person.getPersonnavn().getMellomnavn());
		assertEquals(personMock.getEtternavn(), person.getPersonnavn().getEtternavn());
		compareStrukturertadresse(personMock.getBostedsadresse(), person.getBostedsadresse());
		comparePostboksadresse(personMock.getPostadresse(), person.getPostadresse());
		assertEquals(personMock.getKjonn(), person.getKjoenn().getKjoenn().getValue());

		if (person instanceof Bruker) {
			if (((Bruker) person).getMidlertidigPostadresse() != null) {
				compareMidlertidigadresse(personMock.getMidlertidigadresse(), ((Bruker) person).getMidlertidigPostadresse());
			}
			if (((Bruker) person).getHarAnsvarligEnhet() != null) {
				assertEquals(personMock.getEnhet(), ((Bruker) person).getHarAnsvarligEnhet().getEnhet().getOrganisasjonselementID());
			}
			assertEquals(personMock.getDiskresjonskode(), person.getDiskresjonskode().getValue());
		}
	}

	private void compareMidlertidigadresse(UstrukturertadresseMock expected, MidlertidigPostadresse actual) {
		if(actual instanceof MidlertidigPostadresseUtland) {
			assertEquals(expected.getAdresseLinje1(), ((MidlertidigPostadresseUtland) actual).getUstrukturertAdresse().getAdresselinje1());
			assertEquals(expected.getAdresseLinje2(), ((MidlertidigPostadresseUtland) actual).getUstrukturertAdresse().getAdresselinje2());
			assertEquals(expected.getAdresseLinje3(), ((MidlertidigPostadresseUtland) actual).getUstrukturertAdresse().getAdresselinje3());
		}
	}

	private void comparePostboksadresse(UstrukturertadresseMock personExpected, Postadresse postadresse) {

		if (postadresse != null) {
			UstrukturertAdresse postboksadresseNorsk = postadresse.getUstrukturertAdresse();

			assertEquals(personExpected.getAdresseLinje1(), postboksadresseNorsk.getAdresselinje1());
			assertEquals(personExpected.getAdresseLinje2(), postboksadresseNorsk.getAdresselinje2());
			assertEquals(personExpected.getAdresseLinje3(), postboksadresseNorsk.getAdresselinje3());
		}
	}

	private void compareStrukturertadresse(GateadresseMock bostedsadresseExpected, Bostedsadresse bostedsadresse1) {
		StrukturertAdresse strukturertAdresse;
		if (bostedsadresse1 != null) {
			strukturertAdresse = bostedsadresse1.getStrukturertAdresse();
			assertEquals(bostedsadresseExpected.getGatenavn(), ((Gateadresse) strukturertAdresse).getGatenavn());
			assertEquals(BigInteger.valueOf(bostedsadresseExpected.getGatenummer()), ((Gateadresse) strukturertAdresse).getGatenummer());
			assertEquals(BigInteger.valueOf(bostedsadresseExpected.getHusnummer()), ((Gateadresse) strukturertAdresse).getHusnummer());
			assertEquals(bostedsadresseExpected.getHusbokstav(), ((Gateadresse) strukturertAdresse).getHusbokstav());
		}
	}

	private void compareFodselsnummer(String fodselsnummer, NorskIdent ident) {
		assertNotNull(fodselsnummer);
		assertNotNull(ident);

		assertEquals(fodselsnummer, ident.getIdent());
	}

}
