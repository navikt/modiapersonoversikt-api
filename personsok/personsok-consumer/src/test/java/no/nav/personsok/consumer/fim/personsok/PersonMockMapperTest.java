package no.nav.personsok.consumer.fim.personsok;

import no.nav.personsok.consumer.fim.mapping.PersonMockMapper;
import no.nav.personsok.consumer.fim.personsok.mock.GateadresseMock;
import no.nav.personsok.consumer.fim.personsok.mock.PersonMock;
import no.nav.personsok.consumer.fim.personsok.mock.PersonMockFactory;
import no.nav.personsok.consumer.fim.personsok.mock.UstrukturertadresseMock;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimBostedsadresse;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimBruker;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimGateadresse;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimMidlertidigPostadresse;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimMidlertidigPostadresseUtland;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimNorskIdent;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimPerson;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimPostadresse;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimStrukturertAdresse;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimUstrukturertAdresse;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class PersonMockMapperTest {


	@Test
	public void testPersonMock() throws Exception {

		PersonMock personMock = PersonMockFactory.createPersonMock();

		FimPerson person = PersonMockMapper.map(personMock);

		comparePerson(personMock, person);
	}

	@Test
	public void testBrukerMock() {
		PersonMock personMock = PersonMockFactory.createBrukerMock();

		FimPerson person = PersonMockMapper.map(personMock);

		comparePerson(personMock, person);
	}

	@Test
	public void testBrukerUtenUtenlandsadresseMock() {
		PersonMock personMock = PersonMockFactory.createBrukerMock();
		personMock.setMidlertidigadresse(null);

		FimPerson person = PersonMockMapper.map(personMock);

		comparePerson(personMock, person);
	}

	@Test
	public void testBrukerMedUtenlandsadresseMock() {
		PersonMock personMock = PersonMockFactory.createDollyDuck();

		FimPerson person = PersonMockMapper.map(personMock);

		comparePerson(personMock, person);
	}


	private void comparePerson(PersonMock personMock, FimPerson person) {
		compareFodselsnummer(personMock.getIdnummer(), person.getIdent());
		assertEquals(personMock.getIdenttype(), person.getIdent().getType().getValue());
		assertEquals(personMock.getSammensattNavn(), person.getPersonnavn().getSammensattNavn());
		assertEquals(personMock.getFornavn(), person.getPersonnavn().getFornavn());
		assertEquals(personMock.getMellomnavn(), person.getPersonnavn().getMellomnavn());
		assertEquals(personMock.getEtternavn(), person.getPersonnavn().getEtternavn());
		compareStrukturertadresse(personMock.getBostedsadresse(), person.getBostedsadresse());
		comparePostboksadresse(personMock.getPostadresse(), person.getPostadresse());
		assertEquals(personMock.getKjonn(), person.getKjoenn().getKjoenn().getValue());

		if (person instanceof FimBruker) {
			if (((FimBruker) person).getMidlertidigPostadresse() != null) {
				compareMidlertidigadresse(personMock.getMidlertidigadresse(), ((FimBruker) person).getMidlertidigPostadresse());
			}
			if (((FimBruker) person).getHarAnsvarligEnhet() != null) {
				assertEquals(personMock.getEnhet(), ((FimBruker) person).getHarAnsvarligEnhet().getEnhet().getOrganisasjonselementID());
			}
			assertEquals(personMock.getDiskresjonskode(), person.getDiskresjonskode().getValue());
		}
	}

	private void compareMidlertidigadresse(UstrukturertadresseMock expected, FimMidlertidigPostadresse actual) {
		if(actual instanceof FimMidlertidigPostadresseUtland) {
			assertEquals(expected.getAdresseLinje1(), ((FimMidlertidigPostadresseUtland) actual).getUstrukturertAdresse().getAdresselinje1());
			assertEquals(expected.getAdresseLinje2(), ((FimMidlertidigPostadresseUtland) actual).getUstrukturertAdresse().getAdresselinje2());
			assertEquals(expected.getAdresseLinje3(), ((FimMidlertidigPostadresseUtland) actual).getUstrukturertAdresse().getAdresselinje3());
		}
	}

	private void comparePostboksadresse(UstrukturertadresseMock personExpected, FimPostadresse postadresse) {

		if (postadresse != null) {
			FimUstrukturertAdresse postboksadresseNorsk = postadresse.getUstrukturertAdresse();

			assertEquals(personExpected.getAdresseLinje1(), postboksadresseNorsk.getAdresselinje1());
			assertEquals(personExpected.getAdresseLinje2(), postboksadresseNorsk.getAdresselinje2());
			assertEquals(personExpected.getAdresseLinje3(), postboksadresseNorsk.getAdresselinje3());
		}
	}

	private void compareStrukturertadresse(GateadresseMock bostedsadresseExpected, FimBostedsadresse bostedsadresse1) {
		FimStrukturertAdresse strukturertAdresse;
		if (bostedsadresse1 != null) {
			strukturertAdresse = bostedsadresse1.getStrukturertAdresse();
			assertEquals(bostedsadresseExpected.getGatenavn(), ((FimGateadresse) strukturertAdresse).getGatenavn());
			assertEquals(BigInteger.valueOf(bostedsadresseExpected.getGatenummer()), ((FimGateadresse) strukturertAdresse).getGatenummer());
			assertEquals(BigInteger.valueOf(bostedsadresseExpected.getHusnummer()), ((FimGateadresse) strukturertAdresse).getHusnummer());
			assertEquals(bostedsadresseExpected.getHusbokstav(), ((FimGateadresse) strukturertAdresse).getHusbokstav());
		}
	}

	private void compareFodselsnummer(String fodselsnummer, FimNorskIdent ident) {
		assertNotNull(fodselsnummer);
		assertNotNull(ident);

		assertEquals(fodselsnummer, ident.getIdent());
	}

}
