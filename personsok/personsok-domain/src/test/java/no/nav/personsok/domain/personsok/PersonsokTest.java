package no.nav.personsok.domain.personsok;

import no.nav.personsok.domain.Adresse;
import no.nav.personsok.domain.Kjonn;
import no.nav.personsok.domain.Kodeverkstype;
import no.nav.personsok.domain.Person;
import no.nav.personsok.domain.UtvidetPersonsok;
import no.nav.personsok.domain.enums.AdresseType;
import no.nav.personsok.domain.enums.Diskresjonskode;
import no.nav.personsok.domain.enums.PersonstatusType;
import no.nav.personsok.domain.factory.PersonsokDoFactory;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class PersonsokTest {

	public static final String BANKKONTO_NORGE = "12010000000";
	public static final Diskresjonskode DISKRESJONSKODE = Diskresjonskode.KODE_4;
	public static final String ENHET = "0122";
	public static final String FORNAVN = "Donald";
	public static final String MELLOMNAVN = "D.";
	public static final String ETTERNAVN = "Duck";
    public static final String SAMMENSATT_NAVN = "Duck, Donald";
    public static final String FODSELSNUMMER = "22048140567";
	public static final Kodeverkstype PERSONSTATUS = new Kodeverkstype();
	public static final AdresseType ADRESSE_TYPE = AdresseType.BOLIGADRESSE;
	public static final String ADRESSE_STRING = "AdresseStringVeien 42B";
	public static final Integer ALDER_FRA = 12;
	public static final Integer ALDER_TIL = 23;
	public static final LocalDate FODSELSDATO_FRA = new LocalDate(34400000);
	public static final LocalDate FODSELSDATO_TIL = new LocalDate(34500000);
	public static final Kjonn KJONN_KVINNE = Kjonn.K;
	public static final String HUSBOKSTAV = "B";
	public static final String HUSNUMMER = "3";
	public static final String POSTNUMMER = "0562";
	public static final String GATENAVN = "GatenavnVeien";
	public static final String SEARCH_STRING_FORNAVN = "Donald";
	public static final String SEARCH_STRING_ETTERNAVN = "Donald";
	public static final String DOED = "(død)";

	@Test
	public void testPersonGetSet() {
		PERSONSTATUS.setKode(PersonstatusType.DOED.name());
		PERSONSTATUS.setKoderef(PersonstatusType.DOED.name());
		PERSONSTATUS.setKodeverkref(PersonstatusType.DOED.name());
		Person person = PersonsokDoFactory.createPerson(BANKKONTO_NORGE, DISKRESJONSKODE, ENHET,
				FORNAVN, MELLOMNAVN, ETTERNAVN, FODSELSNUMMER, PERSONSTATUS, ADRESSE_TYPE, ADRESSE_STRING, SAMMENSATT_NAVN);

		checkPerson(person);

	}

    @Test
    public void testPersonGetNavnEtternavnBlank(){
        Person person = PersonsokDoFactory.createPerson(BANKKONTO_NORGE, DISKRESJONSKODE, ENHET,
                FORNAVN, MELLOMNAVN, "", FODSELSNUMMER, PERSONSTATUS, ADRESSE_TYPE, ADRESSE_STRING, SAMMENSATT_NAVN);
        assertEquals(SAMMENSATT_NAVN, person.getNavn());
    }

	@Test
	public void testPersonConstructor() {
		PERSONSTATUS.setKode(PersonstatusType.DOED.name());
		PERSONSTATUS.setKoderef(PersonstatusType.DOED.name());
		PERSONSTATUS.setKodeverkref(PersonstatusType.DOED.name());
		Person person = PersonsokDoFactory.createPersonConstructor(BANKKONTO_NORGE, DISKRESJONSKODE, ENHET,
				FORNAVN, MELLOMNAVN, ETTERNAVN, FODSELSNUMMER, PERSONSTATUS, ADRESSE_TYPE, ADRESSE_STRING);

		checkPerson(person);

	}

	@Test
	public void testPersonBranchesNotDod() {
		PERSONSTATUS.setKode(PersonstatusType.BOSATT.toString());
		PERSONSTATUS.setKoderef(PersonstatusType.BOSATT.toString());
		PERSONSTATUS.setKodeverkref(PersonstatusType.BOSATT.toString());
		Person person = PersonsokDoFactory.createPersonConstructor(BANKKONTO_NORGE, DISKRESJONSKODE, ENHET,
				FORNAVN, null, ETTERNAVN, null, PERSONSTATUS, ADRESSE_TYPE, ADRESSE_STRING);

		assertEquals(ETTERNAVN + ", " + FORNAVN, person.getNavn());
		assertEquals("", person.getFodselsdato());

	}

	@Test
	public void testPersonBranchesDod() {
		PERSONSTATUS.setKode(PersonstatusType.DOED.name());
		PERSONSTATUS.setKoderef(PersonstatusType.DOED.name());
		PERSONSTATUS.setKodeverkref(PersonstatusType.DOED.name());
		Person person = PersonsokDoFactory.createPersonConstructor(BANKKONTO_NORGE, DISKRESJONSKODE, ENHET,
				FORNAVN, null, ETTERNAVN, null, PERSONSTATUS, ADRESSE_TYPE, ADRESSE_STRING);

		assertEquals(ETTERNAVN + ", " + FORNAVN, person.getNavn());
		assertEquals(DOED, person.getPersonstatusTegn());
		assertEquals("", person.getFodselsdato());

	}

	@Test
	public void testPersonBranchesEgenAnsatt() {
		PERSONSTATUS.setKode(PersonstatusType.BOSATT.toString());
		PERSONSTATUS.setKoderef(PersonstatusType.BOSATT.toString());
		PERSONSTATUS.setKodeverkref(PersonstatusType.BOSATT.toString());
		Person person = PersonsokDoFactory.createPersonConstructor(BANKKONTO_NORGE, Diskresjonskode.KODE_5, ENHET,
				FORNAVN, null, ETTERNAVN, null, PERSONSTATUS, ADRESSE_TYPE, ADRESSE_STRING);

		assertEquals(ETTERNAVN + ", " + FORNAVN, person.getNavn());
		assertNull(person.getPersonstatusTegn());
		assertEquals("", person.getFodselsdato());
	}

	@Test
	public void testPersonBranchesKode6() {
		PERSONSTATUS.setKode(PersonstatusType.BOSATT.toString());
		PERSONSTATUS.setKoderef(PersonstatusType.BOSATT.toString());
		PERSONSTATUS.setKodeverkref(PersonstatusType.BOSATT.toString());
		Person person = PersonsokDoFactory.createPersonConstructor(BANKKONTO_NORGE, Diskresjonskode.KODE_6, ENHET,
				FORNAVN, null, ETTERNAVN, null, PERSONSTATUS, ADRESSE_TYPE, ADRESSE_STRING);

		assertEquals(ETTERNAVN + ", " + FORNAVN, person.getNavn());
		assertNull(person.getPersonstatusTegn());

		assertEquals("", person.getFodselsdato());
	}

	@Test
	public void testPersonBranchesKode7() {
		PERSONSTATUS.setKode(PersonstatusType.BOSATT.toString());
		PERSONSTATUS.setKoderef(PersonstatusType.BOSATT.toString());
		PERSONSTATUS.setKodeverkref(PersonstatusType.BOSATT.toString());
		Person person = PersonsokDoFactory.createPersonConstructor(BANKKONTO_NORGE, Diskresjonskode.KODE_7, ENHET,
				FORNAVN, null, ETTERNAVN, null, PERSONSTATUS, ADRESSE_TYPE, ADRESSE_STRING);

		assertEquals(ETTERNAVN + ", " + FORNAVN, person.getNavn());
		assertNull(person.getPersonstatusTegn());
		assertEquals("", person.getFodselsdato());
	}

	@Test
	public void testUtvidetPersonsokGetSet() {

		UtvidetPersonsok utvidetPersonsok = PersonsokDoFactory.createUtvidetPersonsok(ALDER_FRA, ALDER_TIL, ENHET, FODSELSDATO_FRA, FODSELSDATO_TIL,
				KJONN_KVINNE, HUSBOKSTAV, HUSNUMMER, POSTNUMMER, GATENAVN, BANKKONTO_NORGE,
				SEARCH_STRING_FORNAVN, SEARCH_STRING_ETTERNAVN);

		checkUtvidetPersonsok(utvidetPersonsok);

	}

	@Test
	public void testUtvidetPersonsokBranches() {

		UtvidetPersonsok utvidetPersonsok = PersonsokDoFactory.createUtvidetPersonsok(ALDER_FRA, ALDER_TIL, ENHET, null, null,
				KJONN_KVINNE, HUSBOKSTAV, HUSNUMMER, POSTNUMMER, GATENAVN, BANKKONTO_NORGE,
				SEARCH_STRING_FORNAVN, SEARCH_STRING_ETTERNAVN);

		assertEquals(null, utvidetPersonsok.getFodselsdatoFra());
		assertEquals(null, utvidetPersonsok.getFodselsdatoTil());

	}

	@Test
	public void testAdresseCriteriaConstructor() {

		Adresse adresse = PersonsokDoFactory.createAdresseConstructor(ADRESSE_STRING, ADRESSE_TYPE);

		checkAdresse(adresse);

	}

	private void checkUtvidetPersonsok(UtvidetPersonsok utvidetPersonsok) {
		assertEquals(ALDER_FRA, utvidetPersonsok.getAlderFra());
		assertEquals(ALDER_TIL, utvidetPersonsok.getAlderTil());
		assertEquals(ENHET, utvidetPersonsok.getKommunenr());
		assertEquals(FODSELSDATO_FRA, utvidetPersonsok.getFodselsdatoFra());
		assertEquals(FODSELSDATO_TIL, utvidetPersonsok.getFodselsdatoTil());
		assertEquals(KJONN_KVINNE, utvidetPersonsok.getKjonn());
		assertEquals(HUSBOKSTAV, utvidetPersonsok.getHusbokstav());
		assertEquals(HUSNUMMER, utvidetPersonsok.getHusnummer());
		assertEquals(POSTNUMMER, utvidetPersonsok.getPostnummer());
		assertEquals(GATENAVN, utvidetPersonsok.getGatenavn());
		assertEquals(BANKKONTO_NORGE, utvidetPersonsok.getKontonummer());

		assertEquals(SEARCH_STRING_FORNAVN, utvidetPersonsok.getFornavn());
		assertEquals(SEARCH_STRING_ETTERNAVN, utvidetPersonsok.getEtternavn());
	}


	private void checkPerson(Person person) {
		PERSONSTATUS.setKode(PersonstatusType.DOED.name());
		PERSONSTATUS.setKoderef(PersonstatusType.DOED.name());
		PERSONSTATUS.setKodeverkref(PersonstatusType.DOED.name());
		assertEquals(BANKKONTO_NORGE, person.getBankkontoNorge());
		assertEquals(DISKRESJONSKODE, person.getDiskresjonskodePerson());
		assertEquals(ENHET, person.getKommunenr());
		assertEquals(FORNAVN, person.getFornavn());
		assertEquals(MELLOMNAVN, person.getMellomnavn());
		assertEquals(ETTERNAVN, person.getEtternavn());
		assertEquals(FODSELSNUMMER, person.getFodselsnummer());
		assertEquals(PERSONSTATUS, person.getPersonstatus());
		assertEquals(ETTERNAVN + ", " + FORNAVN + (MELLOMNAVN != null ? " " + MELLOMNAVN : ""), person.getNavn());
		assertEquals(DOED, person.getPersonstatusTegn());
		assertEquals(FODSELSNUMMER == null ? null :
				FODSELSNUMMER.substring(4, 6) + FODSELSNUMMER.substring(3, 5) + FODSELSNUMMER.substring(0, 2), person.getFodselsdato());


		checkAdresseListe(person.getAdresser());
	}

	private void checkAdresseListe(List<Adresse> adresser) {
		for (Adresse adresse : adresser) {
			checkAdresse(adresse);
		}
	}

	private void checkAdresse(Adresse adresse) {
		assertEquals(ADRESSE_TYPE, adresse.getAdresseType());
		assertEquals(ADRESSE_STRING, adresse.getAdresseString());
		assertEquals(ADRESSE_TYPE.toString().substring(0, 1), adresse.getAdresseType().toString());
	}


}
