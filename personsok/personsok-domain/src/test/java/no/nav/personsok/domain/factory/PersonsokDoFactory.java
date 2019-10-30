package no.nav.personsok.domain.factory;

import no.nav.personsok.domain.*;
import no.nav.personsok.domain.enums.AdresseType;
import no.nav.personsok.domain.enums.Diskresjonskode;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class PersonsokDoFactory {


	public static Person createPerson(String bankkontoNorge, Diskresjonskode diskresjonskode, String enhet, String fornavn,
                                      String mellomnavn, String etternavn, String fodselsnummer, Kodeverkstype personstatus,
                                      AdresseType adresseType, String adresseString, String sammensattNavn) {

		Person person = new Person();
		person.setAdresser(createAdresser(adresseType, adresseString));
		person.setBankkontoNorge(bankkontoNorge);
		person.setDiskresjonskodePerson(diskresjonskode);
		person.setKommunenr(enhet);
		person.setFornavn(fornavn);
		person.setMellomnavn(mellomnavn);
		person.setEtternavn(etternavn);
		person.setFodselsnummer(fodselsnummer);
		person.setPersonstatus(personstatus);
        person.setSammensattNavn(sammensattNavn);
		return person;
	}

	public static Person createPersonConstructor(String bankkontoNorge, Diskresjonskode diskresjonskode, String enhet, String fornavn,
												 String mellomnavn, String etternavn, String fodselsnummer,
												 Kodeverkstype personstatus, AdresseType adresseType, String adresseString) {

		Person person = new Person(fodselsnummer, fornavn, mellomnavn, etternavn, createAdresser(adresseType, adresseString),
				enhet);
		person.setDiskresjonskodePerson(diskresjonskode);
		person.setBankkontoNorge(bankkontoNorge);
		person.setPersonstatus(personstatus);
		return person;
	}

	private static List<Adresse> createAdresser(AdresseType adresseType, String adresseString) {
		List<Adresse> adresseList = new ArrayList<>();
		adresseList.add(createAdresse(adresseType, adresseString));
		return adresseList;
	}

	private static Adresse createAdresse(AdresseType adresseType, String adresseString) {
		Adresse adresse = new Adresse();
		adresse.setAdresseType(adresseType);
		adresse.setAdresseString(adresseString);
		return adresse;
	}


	public static UtvidetPersonsok createUtvidetPersonsok(Integer alderFra, Integer alderTil, String enhet,
														  LocalDate fodselsdatoFra, LocalDate fodselsdatoTil, Kjonn kjonn,
														  String husbokstav, String husnummer, String postnummer,
														  String gatenavn, String kontonummer,
														  String searchStringFornavn,
														  String searchStringEtternavn) {
		UtvidetPersonsok utvidetPersonsok = new UtvidetPersonsok();
		utvidetPersonsok.setAlderFra(alderFra);
		utvidetPersonsok.setAlderTil(alderTil);
		utvidetPersonsok.setKommunenr(enhet);
		utvidetPersonsok.setFodselsdatoFra(fodselsdatoFra);
		utvidetPersonsok.setFodselsdatoTil(fodselsdatoTil);
		utvidetPersonsok.setKjonn(kjonn);
		utvidetPersonsok.setHusbokstav(husbokstav);
		utvidetPersonsok.setHusnummer(husnummer);
		utvidetPersonsok.setPostnummer(postnummer);
		utvidetPersonsok.setFornavn(searchStringFornavn);
		utvidetPersonsok.setEtternavn(searchStringEtternavn);
		utvidetPersonsok.setGatenavn(gatenavn);
		utvidetPersonsok.setKontonummer(kontonummer);

		return utvidetPersonsok;
	}


	public static Adresse createAdresseConstructor(String adresseString, AdresseType adresseType) {
		Adresse adresse = new Adresse(adresseString, adresseType);
		return adresse;
	}
}
