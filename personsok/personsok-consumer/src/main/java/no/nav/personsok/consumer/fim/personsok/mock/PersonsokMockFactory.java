package no.nav.personsok.consumer.fim.personsok.mock;

import no.nav.personsok.domain.Kjonn;
import no.nav.personsok.domain.UtvidetPersonsok;
import no.nav.personsok.domain.enums.AdresseType;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*;
import org.joda.time.LocalDate;

import java.math.BigInteger;

public final class PersonsokMockFactory {

	private static final FimDiskresjonskoder DISKRESJONSKODE1 = new FimDiskresjonskoder();
	private static final FimDiskresjonskoder DISKRESJONSKODE7 = new FimDiskresjonskoder();
	public static final String ADRESSELINJE_1 = "Adresselinje1";
	public static final String ADRESSELINJE_2 = "Adresselinje2";
	public static final String ADRESSELINJE_3 = "Adresselinje3";
	public static final String ADRESSELINJE_4 = "Adresselinje4";

	public static final FimNorskIdent PERSONTYPEIDENT_F = new FimNorskIdent();
	public static final String IDENT_FODSELSNUMMER = "10101098980";
	public static final String IDENT_FODSELSNUMMER2 = "15128898980";
	public static final FimPersonidenter IDENTTYPE_FODSELSNUMMER = new FimPersonidenter();
	public static final FimNorskIdent PERSONTYPEIDENT_D = new FimNorskIdent();
	public static final String IDENT_DNUMMER = "40000000000";
	public static final FimPersonidenter IDENTTYPE_DNUMMER = new FimPersonidenter();
	public static final FimNorskIdent PERSONTYPEIDENT_D2 = new FimNorskIdent();
	public static final String GATENAVN = "Gatenavn";
	public static final String GATENUMMER = "4";
	public static final String HUSBOKSTAV = "A";
	public static final String BOLIGNR = "Gatenavn";
	public static final String KOMMUNENR = "4";
	public static final String TILLEGGSADRESSE = "A";
	public static final FimPostnummer FIMPOSTNR = new FimPostnummer();
	public static final String POSTNUMMER = "0562";
	public static final String PERSONSTATUS_DOD_KODE = "DØD";
	public static final String PERSONSTATUS_DOD_KODEREF = "D";
	public static final FimPersonstatuser PERSONSTATUS_DOD = new FimPersonstatuser();
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
	public static final FimPostadressetyper FIMPOSTADRESSETYPERN = new FimPostadressetyper();
	public static final FimPostadressetyper FIMPOSTADRESSETYPERU = new FimPostadressetyper();
	public static final FimPostadressetyper FIMPOSTADRESSETYPERP = new FimPostadressetyper();
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

	public static FimPerson getPerson() {
		return createNewPerson();
	}

	public static FimBruker getBruker() {
		return createNewBruker();
	}

	public static FimBruker getBruker2() {
		return createNewBruker2();
	}

	public static FimBruker getBruker3() {
		return createNewBruker3();
	}

	public static FimBruker getBruker4() {
		return createNewBruker4();
	}

    public static FimBruker getBruker5() {
        return createNewBruker5();
    }

	public static FimBruker getBrukerForBranchTest() {
		return createNewBrukerForBranchTest();
	}

	public static UtvidetPersonsok getUtvidetPersonsok() {
		return createUtvidetPersonsok();
	}

	private static FimPerson createNewPerson() {
		IDENTTYPE_FODSELSNUMMER.setValue("F");
		FimPerson person = new FimPerson();
		PERSONTYPEIDENT_F.setIdent(IDENT_FODSELSNUMMER);
		PERSONTYPEIDENT_F.setType(IDENTTYPE_FODSELSNUMMER);
		person.setIdent(PERSONTYPEIDENT_F);
		person.setBostedsadresse(createBostedsadresseG());
		person.setPersonnavn(createGjeldendePersonnavn());
		person.setPersonstatus(createPersonstatus());
		person.setPostadresse(createPostadresse());
		return person;
	}

	private static FimBruker createNewBruker() {
		DISKRESJONSKODE1.setValue("1");
		IDENTTYPE_DNUMMER.setValue("D");
		FIMPOSTADRESSETYPERN.setValue(AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE.name());
		FimBruker bruker = new FimBruker();
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

	private static FimBruker createNewBruker2() {
		DISKRESJONSKODE1.setValue("1");
		IDENTTYPE_FODSELSNUMMER.setValue("F");
		FIMPOSTADRESSETYPERU.setValue(AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND.name());
		FimBruker bruker = new FimBruker();
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

	private static FimBruker createNewBruker3() {
		DISKRESJONSKODE7.setValue("1");
		IDENTTYPE_FODSELSNUMMER.setValue("F");
		FIMPOSTADRESSETYPERP.setValue(AdresseType.POSTADRESSE.name());
		FimBruker bruker = new FimBruker();
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

	private static FimBruker createNewBruker4() {
		DISKRESJONSKODE7.setValue("1");
		IDENTTYPE_FODSELSNUMMER.setValue("F");
		FIMPOSTADRESSETYPERP.setValue(AdresseType.BOLIGADRESSE.name());
		FimBruker bruker = new FimBruker();
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

    private static FimBruker createNewBruker5() {
        FimBruker bruker = new FimBruker();
        bruker.setIdent(PERSONTYPEIDENT_F);
        bruker.setDiskresjonskode(DISKRESJONSKODE1);
        bruker.setPersonnavn(new FimPersonnavn());
        bruker.setPersonstatus(new FimPersonstatus().withPersonstatus(new FimPersonstatuser()));
        bruker.setPostadresse(new FimPostadresse().withUstrukturertAdresse(new FimUstrukturertAdresse()));
        bruker.setBostedsadresse(new FimBostedsadresse().withStrukturertAdresse(new FimStrukturertAdresse() {}));
        bruker.setHarAnsvarligEnhet(new FimAnsvarligEnhet().withEnhet(new FimOrganisasjonsenhet()));
        bruker.setGjeldendePostadresseType(new FimPostadressetyper().withValue(AdresseType.BOLIGADRESSE.name()));
        return bruker;
    }

	private static FimAnsvarligEnhet createAnsvarligEnhet() {
		FimAnsvarligEnhet fimAnsvarligEnhet = new FimAnsvarligEnhet();
		fimAnsvarligEnhet.setEnhet(createOrganisasjonsenhet());
		return fimAnsvarligEnhet;
	}

	private static FimOrganisasjonsenhet createOrganisasjonsenhet() {
		FimOrganisasjonsenhet fimOrganisasjonsenhet = new FimOrganisasjonsenhet();
		fimOrganisasjonsenhet.setOrganisasjonselementID(KOMMUNENR);
		return fimOrganisasjonsenhet;
	}


	private static FimBruker createNewBrukerForBranchTest() {
		IDENTTYPE_DNUMMER.setValue("D");
		FimBruker bruker = new FimBruker();
		PERSONTYPEIDENT_D2.setType(IDENTTYPE_DNUMMER);
		bruker.setIdent(PERSONTYPEIDENT_D2);
		bruker.setBostedsadresse(null);
		return bruker;
	}

	private static FimMidlertidigPostadresse createMidlertidigPostadresseNorge() {
		FimMidlertidigPostadresseNorge midlertidigPostadresse = new FimMidlertidigPostadresseNorge();
		midlertidigPostadresse.setUstrukturertAdresse(createUstrukturertAdresse3());
		return midlertidigPostadresse;
	}

	private static FimMidlertidigPostadresse createMidlertidigPostadresseUtland() {
		FimMidlertidigPostadresseUtland postadresseUtland = new FimMidlertidigPostadresseUtland();
		postadresseUtland.setUstrukturertAdresse(createUstrukturertAdresse2());
		return postadresseUtland;
	}


	private static FimStedsadresseNorge createStedsadresseNorge() {
		FIMPOSTNR.setValue(POSTNUMMER);
		FimStedsadresseNorge fimStedsadresseNorge = new FimStedsadresseNorge();
		fimStedsadresseNorge.setBolignummer(BOLIGNR);
		fimStedsadresseNorge.setKommunenummer(KOMMUNENR);
		fimStedsadresseNorge.setPoststed(FIMPOSTNR);
		fimStedsadresseNorge.setTilleggsadresse(TILLEGGSADRESSE);
		fimStedsadresseNorge.setTilleggsadresseType(AdresseType.BOLIGADRESSE.name());
		return fimStedsadresseNorge;
	}

	private static FimPostadresse createPostadresse() {
		FimPostadresse postadresse = new FimPostadresse();
		postadresse.setUstrukturertAdresse(createUstrukturertAdresse());
		return postadresse;
	}

	private static FimBostedsadresse createBostedsadresseG() {
		FimBostedsadresse bostedsadresse = new FimBostedsadresse();
		bostedsadresse.setStrukturertAdresse(createGateadresse());
		return bostedsadresse;
	}

	private static FimBostedsadresse createBostedsadresseS() {
		FimBostedsadresse bostedsadresse = new FimBostedsadresse();
		bostedsadresse.setStrukturertAdresse(createStedsadresseNorge());
		return bostedsadresse;
	}

	private static FimBostedsadresse createBostedsadresseM() {
		FimBostedsadresse bostedsadresse = new FimBostedsadresse();
		bostedsadresse.setStrukturertAdresse(createMatrikkeladresse());
		return bostedsadresse;
	}

	private static FimBostedsadresse createBostedsadresseP() {
		FimBostedsadresse bostedsadresse = new FimBostedsadresse();
		bostedsadresse.setStrukturertAdresse(createPostboksadresse());
		return bostedsadresse;
	}

	private static FimGateadresse createGateadresse() {
		FimGateadresse fimGateadresse = new FimGateadresse();
		fimGateadresse.setHusbokstav(HUSBOKSTAV2);
		fimGateadresse.setHusnummer(BigInteger.valueOf(HUSNUMMER2));
		fimGateadresse.setGatenummer(BigInteger.valueOf(GATENUMMER2));
		fimGateadresse.setGatenavn(GATENAVN2);

		return fimGateadresse;
	}

	private static FimMatrikkeladresse createMatrikkeladresse() {
		FimMatrikkeladresse fimMatrikkeladresse = new FimMatrikkeladresse();
		fimMatrikkeladresse.setEiendomsnavn(EIENDOMSNAVN);
		FimMatrikkelnummer fimMatrikkelnummer = new FimMatrikkelnummer();
		fimMatrikkelnummer.setBruksnummer(MATRIKKELNUMMER);
		fimMatrikkeladresse.setMatrikkelnummer(fimMatrikkelnummer);

		return fimMatrikkeladresse;
	}

	private static FimPostboksadresseNorsk createPostboksadresse() {
		FimPostboksadresseNorsk fimPostboksadresseNorsk = new FimPostboksadresseNorsk();
		fimPostboksadresseNorsk.setPostboksanlegg(POSTBOKSANLEGG);
		FimPostnummer fimPostnummer = new FimPostnummer();
		fimPostnummer.setValue(POSTSTED);
		fimPostboksadresseNorsk.setPoststed(fimPostnummer);

		return fimPostboksadresseNorsk;
	}

	private static FimUstrukturertAdresse createUstrukturertAdresse() {
		FimUstrukturertAdresse ustrukturertAdresse = new FimUstrukturertAdresse();
		ustrukturertAdresse.setAdresselinje1(GATENAVN);
		ustrukturertAdresse.setAdresselinje2(GATENUMMER);
		ustrukturertAdresse.setAdresselinje3(HUSBOKSTAV);
		return ustrukturertAdresse;
	}

	private static FimUstrukturertAdresse createUstrukturertAdresse2() {
		FimUstrukturertAdresse ustrukturertAdresse = new FimUstrukturertAdresse();
		ustrukturertAdresse.setAdresselinje1(ADRESSELINJE_1);
		return ustrukturertAdresse;
	}

	private static FimUstrukturertAdresse createUstrukturertAdresse3() {
		FimUstrukturertAdresse ustrukturertAdresse = new FimUstrukturertAdresse();
		ustrukturertAdresse.setAdresselinje1(ADRESSELINJE_2);
		ustrukturertAdresse.setAdresselinje1(ADRESSELINJE_3);
		ustrukturertAdresse.setAdresselinje1(ADRESSELINJE_4);
		return ustrukturertAdresse;
	}

	private static FimPersonstatus createPersonstatus() {
		FimPersonstatus personstatus = new FimPersonstatus();
		PERSONSTATUS_DOD.setValue(PERSONSTATUS_DOD_KODE);
		PERSONSTATUS_DOD.setKodeRef(PERSONSTATUS_DOD_KODEREF);
		personstatus.setPersonstatus(PERSONSTATUS_DOD);
		return personstatus;
	}

	private static FimPersonnavn createGjeldendePersonnavn() {
		FimPersonnavn personnavn = new FimPersonnavn();
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
