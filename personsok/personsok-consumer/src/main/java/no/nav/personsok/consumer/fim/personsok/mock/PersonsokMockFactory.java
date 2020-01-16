package no.nav.personsok.consumer.fim.personsok.mock;

import no.nav.personsok.domain.Kjonn;
import no.nav.personsok.domain.UtvidetPersonsok;
import no.nav.personsok.domain.enums.AdresseType;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*;
import org.joda.time.LocalDate;

import java.math.BigInteger;

public final class PersonsokMockFactory {

	private static final Diskresjonskoder DISKRESJONSKODE1 = new Diskresjonskoder();
	private static final Diskresjonskoder DISKRESJONSKODE7 = new Diskresjonskoder();
	public static final String ADRESSELINJE_1 = "Adresselinje1";
	public static final String ADRESSELINJE_2 = "Adresselinje2";
	public static final String ADRESSELINJE_3 = "Adresselinje3";
	public static final String ADRESSELINJE_4 = "Adresselinje4";

	public static final NorskIdent PERSONTYPEIDENT_F = new NorskIdent();
	public static final String IDENT_FODSELSNUMMER = "10101098980";
	public static final String IDENT_FODSELSNUMMER2 = "15128898980";
	public static final Personidenter IDENTTYPE_FODSELSNUMMER = new Personidenter();
	public static final NorskIdent PERSONTYPEIDENT_D = new NorskIdent();
	public static final String IDENT_DNUMMER = "40000000000";
	public static final Personidenter IDENTTYPE_DNUMMER = new Personidenter();
	public static final NorskIdent PERSONTYPEIDENT_D2 = new NorskIdent();
	public static final String GATENAVN = "Gatenavn";
	public static final String GATENUMMER = "4";
	public static final String HUSBOKSTAV = "A";
	public static final String BOLIGNR = "Gatenavn";
	public static final String KOMMUNENR = "4";
	public static final String TILLEGGSADRESSE = "A";
	public static final Postnummer FIMPOSTNR = new Postnummer();
	public static final String POSTNUMMER = "0562";
	public static final String PERSONSTATUS_DOD_KODE = "DØD";
	public static final String PERSONSTATUS_DOD_KODEREF = "D";
	public static final Personstatuser PERSONSTATUS_DOD = new Personstatuser();
	public static final String SAMMENSATT_NAVN = "Donald Duck";
	public static final String ETTERNAVN = "Duck";
	public static final String FORNAVN = "Donald";
	public static final String MELLOMNAVN = "D.";
	public static final int ALDER_FRA = 23;
	public static final int ALDER_TIL = 34;
	public static final String SOKKOMMUNENR = "0122";
	public static final LocalDate FODSELSDATO_FRA = new LocalDate(34400000);
	public static final LocalDate FODSELSDATO_TIL = new LocalDate(34500000);
	public static final Kjonn KVINNE = Kjonn.K;
	public static final String HUSNUMMER = "1";
	public static final String KONTONUMMER = "12012022880";
	public static final Postadressetyper FIMPOSTADRESSETYPERN = new Postadressetyper();
	public static final Postadressetyper FIMPOSTADRESSETYPERU = new Postadressetyper();
	public static final Postadressetyper FIMPOSTADRESSETYPERP = new Postadressetyper();
	public static final Integer HUSNUMMER2 = 25;
	public static final String GATENAVN2 = "Gatenavn2";
	public static final Integer GATENUMMER2 = 45;
	public static final String HUSBOKSTAV2 = "J";
	public static final String MATRIKKELNUMMER = "123";
	public static final String EIENDOMSNAVN = "Eiendom";
	public static final String POSTBOKSANLEGG = "Anlegg 12";
	public static final String POSTSTED = "0135";

	private PersonsokMockFactory() {
		//Do not allow instantiation of utility class.
	}

	public static Person getPerson() {
		return createNewPerson();
	}

	public static Bruker getBruker() {
		return createNewBruker();
	}

	public static Bruker getBruker2() {
		return createNewBruker2();
	}

	public static Bruker getBruker3() {
		return createNewBruker3();
	}

	public static Bruker getBruker4() {
		return createNewBruker4();
	}

    public static Bruker getBruker5() {
        return createNewBruker5();
    }

	public static Bruker getBrukerForBranchTest() {
		return createNewBrukerForBranchTest();
	}

	public static UtvidetPersonsok getUtvidetPersonsok() {
		return createUtvidetPersonsok();
	}

	private static Person createNewPerson() {
		IDENTTYPE_FODSELSNUMMER.setValue("F");
		Person person = new Person();
		PERSONTYPEIDENT_F.setIdent(IDENT_FODSELSNUMMER);
		PERSONTYPEIDENT_F.setType(IDENTTYPE_FODSELSNUMMER);
		person.setIdent(PERSONTYPEIDENT_F);
		person.setBostedsadresse(createBostedsadresseG());
		person.setPersonnavn(createGjeldendePersonnavn());
		person.setPersonstatus(createPersonstatus());
		person.setPostadresse(createPostadresse());
		return person;
	}

	private static Bruker createNewBruker() {
		DISKRESJONSKODE1.setValue("1");
		IDENTTYPE_DNUMMER.setValue("D");
		FIMPOSTADRESSETYPERN.setValue(AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE.name());
		Bruker bruker = new Bruker();
		PERSONTYPEIDENT_D.setIdent(IDENT_DNUMMER);
		PERSONTYPEIDENT_D.setType(IDENTTYPE_DNUMMER);
		bruker.setIdent(PERSONTYPEIDENT_D);
		bruker.setBostedsadresse(createBostedsadresseG());
		bruker.setPersonnavn(createGjeldendePersonnavn());
		bruker.setPersonstatus(createPersonstatus());
		bruker.setPostadresse(createPostadresse());
		bruker.setDiskresjonskode(DISKRESJONSKODE1);
		bruker.setGjeldendePostadresseType(FIMPOSTADRESSETYPERN);
		bruker.setMidlertidigPostadresse(createMidlertidigPostadresseNorge());
		bruker.setHarAnsvarligEnhet(createAnsvarligEnhet());
		return bruker;
	}

	private static Bruker createNewBruker2() {
		DISKRESJONSKODE1.setValue("1");
		IDENTTYPE_FODSELSNUMMER.setValue("F");
		FIMPOSTADRESSETYPERU.setValue(AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND.name());
		Bruker bruker = new Bruker();
		PERSONTYPEIDENT_F.setIdent(IDENT_FODSELSNUMMER);
		PERSONTYPEIDENT_F.setType(IDENTTYPE_FODSELSNUMMER);
		bruker.setIdent(PERSONTYPEIDENT_F);
		bruker.setBostedsadresse(createBostedsadresseS());
		bruker.setPersonnavn(createGjeldendePersonnavn());
		bruker.setPersonstatus(createPersonstatus());
		bruker.setPostadresse(createPostadresse());
		bruker.setDiskresjonskode(DISKRESJONSKODE1);
		bruker.setGjeldendePostadresseType(FIMPOSTADRESSETYPERU);
		bruker.setMidlertidigPostadresse(createMidlertidigPostadresseUtland());
		bruker.setHarAnsvarligEnhet(createAnsvarligEnhet());
		return bruker;
	}

	private static Bruker createNewBruker3() {
		DISKRESJONSKODE7.setValue("1");
		IDENTTYPE_FODSELSNUMMER.setValue("F");
		FIMPOSTADRESSETYPERP.setValue(AdresseType.POSTADRESSE.name());
		Bruker bruker = new Bruker();
		PERSONTYPEIDENT_F.setIdent(IDENT_FODSELSNUMMER2);
		PERSONTYPEIDENT_F.setType(IDENTTYPE_FODSELSNUMMER);
		bruker.setIdent(PERSONTYPEIDENT_F);
		bruker.setBostedsadresse(createBostedsadresseM());
		bruker.setPersonnavn(createGjeldendePersonnavn());
		bruker.setPersonstatus(createPersonstatus());
		bruker.setPostadresse(createPostadresse());
		bruker.setDiskresjonskode(DISKRESJONSKODE7);
		bruker.setGjeldendePostadresseType(FIMPOSTADRESSETYPERU);
		bruker.setMidlertidigPostadresse(createMidlertidigPostadresseUtland());
		bruker.setHarAnsvarligEnhet(createAnsvarligEnhet());
		return bruker;
	}

	private static Bruker createNewBruker4() {
		DISKRESJONSKODE7.setValue("1");
		IDENTTYPE_FODSELSNUMMER.setValue("F");
		FIMPOSTADRESSETYPERP.setValue(AdresseType.BOLIGADRESSE.name());
		Bruker bruker = new Bruker();
		PERSONTYPEIDENT_F.setIdent(IDENT_FODSELSNUMMER2);
		PERSONTYPEIDENT_F.setType(IDENTTYPE_FODSELSNUMMER);
		bruker.setIdent(PERSONTYPEIDENT_F);
		bruker.setBostedsadresse(createBostedsadresseP());
		bruker.setPersonnavn(createGjeldendePersonnavn());
		bruker.setPersonstatus(createPersonstatus());
		bruker.setPostadresse(createPostadresse());
		bruker.setDiskresjonskode(DISKRESJONSKODE7);
		bruker.setGjeldendePostadresseType(FIMPOSTADRESSETYPERU);
		bruker.setMidlertidigPostadresse(createMidlertidigPostadresseUtland());
		bruker.setHarAnsvarligEnhet(createAnsvarligEnhet());
		return bruker;
	}

    private static Bruker createNewBruker5() {
        Bruker bruker = new Bruker();
        bruker.setIdent(PERSONTYPEIDENT_F);
        bruker.setDiskresjonskode(DISKRESJONSKODE1);
        bruker.setPersonnavn(new Personnavn());

		Personstatuser personstatuser = new Personstatuser();
		Personstatus personstatus = new Personstatus();
		personstatus.setPersonstatus(personstatuser);
		bruker.setPersonstatus(personstatus);

		UstrukturertAdresse postadresseUstrukturert = new UstrukturertAdresse();
		Postadresse postadresse = new Postadresse();
		postadresse.setUstrukturertAdresse(postadresseUstrukturert);
		bruker.setPostadresse(postadresse);

		StrukturertAdresse bostedsAdresseStrukturert = new StrukturertAdresse() {};
		Bostedsadresse bostedsadresse = new Bostedsadresse();
		bostedsadresse.setStrukturertAdresse(bostedsAdresseStrukturert);
		bruker.setBostedsadresse(bostedsadresse);

		Organisasjonsenhet organisasjonsenhet = new Organisasjonsenhet();
		AnsvarligEnhet ansvarligEnhet = new AnsvarligEnhet();
		ansvarligEnhet.setEnhet(organisasjonsenhet);
		bruker.setHarAnsvarligEnhet(ansvarligEnhet);


		Postadressetyper postadressetyper = new Postadressetyper();
		postadressetyper.setValue(AdresseType.BOLIGADRESSE.name());
		bruker.setGjeldendePostadresseType(postadressetyper);

        return bruker;
    }

	private static AnsvarligEnhet createAnsvarligEnhet() {
		AnsvarligEnhet fimAnsvarligEnhet = new AnsvarligEnhet();
		fimAnsvarligEnhet.setEnhet(createOrganisasjonsenhet());
		return fimAnsvarligEnhet;
	}

	private static Organisasjonsenhet createOrganisasjonsenhet() {
		Organisasjonsenhet fimOrganisasjonsenhet = new Organisasjonsenhet();
		fimOrganisasjonsenhet.setOrganisasjonselementID(KOMMUNENR);
		return fimOrganisasjonsenhet;
	}


	private static Bruker createNewBrukerForBranchTest() {
		IDENTTYPE_DNUMMER.setValue("D");
		Bruker bruker = new Bruker();
		PERSONTYPEIDENT_D2.setType(IDENTTYPE_DNUMMER);
		bruker.setIdent(PERSONTYPEIDENT_D2);
		bruker.setBostedsadresse(null);
		return bruker;
	}

	private static MidlertidigPostadresse createMidlertidigPostadresseNorge() {
		MidlertidigPostadresseNorge midlertidigPostadresse = new MidlertidigPostadresseNorge();
		midlertidigPostadresse.setUstrukturertAdresse(createUstrukturertAdresse3());
		return midlertidigPostadresse;
	}

	private static MidlertidigPostadresse createMidlertidigPostadresseUtland() {
		MidlertidigPostadresseUtland postadresseUtland = new MidlertidigPostadresseUtland();
		postadresseUtland.setUstrukturertAdresse(createUstrukturertAdresse2());
		return postadresseUtland;
	}


	private static StedsadresseNorge createStedsadresseNorge() {
		FIMPOSTNR.setValue(POSTNUMMER);
		StedsadresseNorge fimStedsadresseNorge = new StedsadresseNorge();
		fimStedsadresseNorge.setBolignummer(BOLIGNR);
		fimStedsadresseNorge.setKommunenummer(KOMMUNENR);
		fimStedsadresseNorge.setPoststed(FIMPOSTNR);
		fimStedsadresseNorge.setTilleggsadresse(TILLEGGSADRESSE);
		fimStedsadresseNorge.setTilleggsadresseType(AdresseType.BOLIGADRESSE.name());
		return fimStedsadresseNorge;
	}

	private static Postadresse createPostadresse() {
		Postadresse postadresse = new Postadresse();
		postadresse.setUstrukturertAdresse(createUstrukturertAdresse());
		return postadresse;
	}

	private static Bostedsadresse createBostedsadresseG() {
		Bostedsadresse bostedsadresse = new Bostedsadresse();
		bostedsadresse.setStrukturertAdresse(createGateadresse());
		return bostedsadresse;
	}

	private static Bostedsadresse createBostedsadresseS() {
		Bostedsadresse bostedsadresse = new Bostedsadresse();
		bostedsadresse.setStrukturertAdresse(createStedsadresseNorge());
		return bostedsadresse;
	}

	private static Bostedsadresse createBostedsadresseM() {
		Bostedsadresse bostedsadresse = new Bostedsadresse();
		bostedsadresse.setStrukturertAdresse(createMatrikkeladresse());
		return bostedsadresse;
	}

	private static Bostedsadresse createBostedsadresseP() {
		Bostedsadresse bostedsadresse = new Bostedsadresse();
		bostedsadresse.setStrukturertAdresse(createPostboksadresse());
		return bostedsadresse;
	}

	private static Gateadresse createGateadresse() {
		Gateadresse fimGateadresse = new Gateadresse();
		fimGateadresse.setHusbokstav(HUSBOKSTAV2);
		fimGateadresse.setHusnummer(BigInteger.valueOf(HUSNUMMER2));
		fimGateadresse.setGatenummer(BigInteger.valueOf(GATENUMMER2));
		fimGateadresse.setGatenavn(GATENAVN2);

		return fimGateadresse;
	}

	private static Matrikkeladresse createMatrikkeladresse() {
		Matrikkeladresse fimMatrikkeladresse = new Matrikkeladresse();
		fimMatrikkeladresse.setEiendomsnavn(EIENDOMSNAVN);
		Matrikkelnummer fimMatrikkelnummer = new Matrikkelnummer();
		fimMatrikkelnummer.setBruksnummer(MATRIKKELNUMMER);
		fimMatrikkeladresse.setMatrikkelnummer(fimMatrikkelnummer);

		return fimMatrikkeladresse;
	}

	private static PostboksadresseNorsk createPostboksadresse() {
		PostboksadresseNorsk fimPostboksadresseNorsk = new PostboksadresseNorsk();
		fimPostboksadresseNorsk.setPostboksanlegg(POSTBOKSANLEGG);
		Postnummer fimPostnummer = new Postnummer();
		fimPostnummer.setValue(POSTSTED);
		fimPostboksadresseNorsk.setPoststed(fimPostnummer);

		return fimPostboksadresseNorsk;
	}

	private static UstrukturertAdresse createUstrukturertAdresse() {
		UstrukturertAdresse ustrukturertAdresse = new UstrukturertAdresse();
		ustrukturertAdresse.setAdresselinje1(GATENAVN);
		ustrukturertAdresse.setAdresselinje2(GATENUMMER);
		ustrukturertAdresse.setAdresselinje3(HUSBOKSTAV);
		return ustrukturertAdresse;
	}

	private static UstrukturertAdresse createUstrukturertAdresse2() {
		UstrukturertAdresse ustrukturertAdresse = new UstrukturertAdresse();
		ustrukturertAdresse.setAdresselinje1(ADRESSELINJE_1);
		return ustrukturertAdresse;
	}

	private static UstrukturertAdresse createUstrukturertAdresse3() {
		UstrukturertAdresse ustrukturertAdresse = new UstrukturertAdresse();
		ustrukturertAdresse.setAdresselinje1(ADRESSELINJE_2);
		ustrukturertAdresse.setAdresselinje1(ADRESSELINJE_3);
		ustrukturertAdresse.setAdresselinje1(ADRESSELINJE_4);
		return ustrukturertAdresse;
	}

	private static Personstatus createPersonstatus() {
		Personstatus personstatus = new Personstatus();
		PERSONSTATUS_DOD.setValue(PERSONSTATUS_DOD_KODE);
		PERSONSTATUS_DOD.setKodeRef(PERSONSTATUS_DOD_KODEREF);
		personstatus.setPersonstatus(PERSONSTATUS_DOD);
		return personstatus;
	}

	private static Personnavn createGjeldendePersonnavn() {
		Personnavn personnavn = new Personnavn();
		personnavn.setSammensattNavn(SAMMENSATT_NAVN);
		personnavn.setEtternavn(ETTERNAVN);
		personnavn.setFornavn(FORNAVN);
		personnavn.setMellomnavn(MELLOMNAVN);
		return personnavn;
	}

	private static UtvidetPersonsok createUtvidetPersonsok() {
		UtvidetPersonsok utvidetPersonsok = new UtvidetPersonsok();
		utvidetPersonsok.setAlderFra(ALDER_FRA);
		utvidetPersonsok.setAlderTil(ALDER_TIL);
		utvidetPersonsok.setKommunenr(SOKKOMMUNENR);
		utvidetPersonsok.setFodselsdatoFra(FODSELSDATO_FRA);
		utvidetPersonsok.setFodselsdatoTil(FODSELSDATO_TIL);
		utvidetPersonsok.setKjonn(KVINNE);
		utvidetPersonsok.setHusbokstav(HUSBOKSTAV);
		utvidetPersonsok.setHusnummer(HUSNUMMER);
		utvidetPersonsok.setPostnummer(POSTNUMMER);
		utvidetPersonsok.setFornavn(FORNAVN);
		utvidetPersonsok.setEtternavn(ETTERNAVN);
		utvidetPersonsok.setGatenavn(GATENAVN);
		utvidetPersonsok.setKontonummer(KONTONUMMER);

		return utvidetPersonsok;
	}

}
