package no.nav.personsok.consumer.fim.personsok.mock;

import no.nav.personsok.domain.enums.PersonstatusType;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimLandkoder;

import java.util.Arrays;
import java.util.List;

public final class PersonMockFactory {

	private static final String FNR = "01234567890";
	private static final String SAMMENSATT_NAVN = "Sammensatt Navn";
	private static final String FORNAVN = "Fornavn";
	private static final String FORNAVN_D = "Donald";
	private static final String MELLOMNAVN = "Mellomnavn";
	private static final String ETTERNAVN = "Etternavn";
	private static final String GATENAVN = "Gatenavn";
	private static final Integer GATENUMMER = 1;
	private static final String HUSBOKSTAV = "Husbokstav";
	private static final Integer HUSNUMMER = 45;
	private static final String ENHET = "Enhet";
	private static final FimLandkoder FIM_LANDKODER = new FimLandkoder();
	private static final FimLandkoder FIM_LANDKODER_2 = new FimLandkoder();
	private static final String KJONN = "Mann";
	private static final String ADRESSELINJE1 = "Adresselinje 1";
	private static final String ADRESSELINJE2 = "Adresselinje 2";
	private static final String DISKRESJONSKODE7 = "7";
	private static final String DISKRESJONSKODE6 = "6";
	private static final String DISKRESJONSKODE5 = "5";
	private static final String DISKRESJONSKODE1 = "1";


	public static final String FIMPERSONIDENTERF = "F";
	public static final String FIMPERSONIDENTERD = "D";



	private PersonMockFactory() {
		//Do not allow instantiation of utility class.
	}

	public static PersonMock createPersonMock() {
		PersonMock personMock = new PersonMockBuilder().createPersonMock();
		personMock.setIdnummer(FNR);
		personMock.setIdenttype(FIMPERSONIDENTERF);
		personMock.setSammensattNavn(SAMMENSATT_NAVN);
		personMock.setFornavn(FORNAVN);
		personMock.setMellomnavn(MELLOMNAVN);
		personMock.setEtternavn(ETTERNAVN);
		personMock.setBostedsadresse(createBostedsadresse());
		personMock.setEnhet(ENHET);
		personMock.setKjonn(KJONN);
		personMock.setPostadresse(createPostadresse());

		return personMock;
	}

	public static PersonMock createBrukerMock() {

		PersonMock personMock = createPersonMock();

		personMock.setMidlertidigadresse(createUtenlandsadresseMock());
		personMock.setDiskresjonskode(DISKRESJONSKODE6);

		return personMock;
	}

	public static UstrukturertadresseMock createUtenlandsadresseMock() {
		UstrukturertadresseMock utenlandsadresseMock = new UstrukturertadresseMock();
		utenlandsadresseMock.setAdresseLinje1(ADRESSELINJE1);
		return utenlandsadresseMock;
	}

	public static UstrukturertadresseMock createPostadresse() {
		UstrukturertadresseMock utenlandsadresseMock = new UstrukturertadresseMock();
		utenlandsadresseMock.setAdresseLinje1(ADRESSELINJE2);
		return utenlandsadresseMock;
	}

	public static GateadresseMock createBostedsadresse() {
		GateadresseMock gateadresseMock = new GateadresseMock();
		gateadresseMock.setGatenavn(GATENAVN);
		gateadresseMock.setHusnummer(HUSNUMMER);
		gateadresseMock.setHusbokstav(HUSBOKSTAV);
		gateadresseMock.setGatenummer(GATENUMMER);
		return gateadresseMock;
	}

	public static GateadresseMock createBostedsadresse2(String navn, Integer nr, String bokstav, Integer gNr) {
		GateadresseMock gateadresseMock = new GateadresseMock();
		gateadresseMock.setGatenavn(navn);
		gateadresseMock.setHusnummer(nr);
		gateadresseMock.setHusbokstav(bokstav);
		gateadresseMock.setGatenummer(gNr);
		return gateadresseMock;
	}

	public static List<PersonMock> createPersonMockList() {

		return Arrays.asList(
				createDonaldFDuck(),
				createDollyDuck(),
				createOleDuck(),
				createDoleDuck(),
				createDoffenDuck(),
				createDonalPerPersen(),
				createDonaldOlaOlasen(),
				createDonaldNilsNilsen(),
				createDonalDoleDolesen(),
				createDonalDoffenDoffensen(),
				createDonaldDonaldDonaldsen(),
				createDonaldSuperAgent(),
				createDonaldPusleAgent(),
				createDonaldNAVansatt(),
				createDonaldNAVBrukerDuck()
		);
	}

	private static PersonMock createDonaldDonaldDonaldsen() {
		FIM_LANDKODER.setValue("NO");
		return new PersonMockBuilder()
				.setFodselsnummer("01077478000")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn(FORNAVN_D)
				.setEtternavn("Donaldsen")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 70, "D", 183))
				.setPostadresse(new UstrukturertadresseMock(FIM_LANDKODER, "Veienstedet", "1", "9999 Byen"))
				.setUtenlandsadresse(null)
				.setEnhet("0422")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	private static PersonMock createDonalDoffenDoffensen() {
		FIM_LANDKODER.setValue("NO");
		return new PersonMockBuilder()
				.setFodselsnummer("01067478000")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Doffen")
				.setEtternavn("Doffensen")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 60, "C", 193))
				.setPostadresse(new UstrukturertadresseMock(FIM_LANDKODER, "Veienstedet", "1", "9999 Byen"))
				.setUtenlandsadresse(new UstrukturertadresseMock(null, "Incognitogaten 1, 9999 Bortevekk , Langtvekkistan", null, null))
				.setEnhet("0622")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	private static PersonMock createDonalDoleDolesen() {
		FIM_LANDKODER.setValue("NO");
		return new PersonMockBuilder()
				.setFodselsnummer("01057478000")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Dole")
				.setEtternavn("Dolesen")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 50, "B", 0153))
				.setPostadresse(new UstrukturertadresseMock(FIM_LANDKODER, "Veienstedet", "1", "9999 Byen"))
				.setUtenlandsadresse(null)
				.setEnhet("0722")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}



	private static PersonMock createDonaldOlaOlasen() {
		FIM_LANDKODER.setValue("NO");
		return new PersonMockBuilder()
				.setFodselsnummer("01037478000")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Ola")
				.setEtternavn("Olasen")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 30, "F", 0223))
				.setPostadresse(new UstrukturertadresseMock(FIM_LANDKODER, "Veienstedet", "1", "9999 Byen"))
				.setUtenlandsadresse(null)
				.setEnhet("0222")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	private static PersonMock createDonalPerPersen() {
		FIM_LANDKODER.setValue("NO");
		return new PersonMockBuilder()
				.setFodselsnummer("01027478000")
				.setIdenttype(FIMPERSONIDENTERD)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Per")
				.setEtternavn("Persen")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 20, "G", 8123))
				.setPostadresse(new UstrukturertadresseMock(FIM_LANDKODER, "Veienstedet", "1", "9999 Byen"))
				.setUtenlandsadresse(null)
				.setEnhet("0562")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	private static PersonMock createDoffenDuck() {
		return new PersonMockBuilder()
				.setFodselsnummer("01017623456")
				.setIdenttype(FIMPERSONIDENTERD)
				.setFornavn("Doffen")
				.setMellomnavn(null)
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 4, "H", 1234))
				.setPostadresse(null)
				.setUtenlandsadresse(null)
				.setEnhet("7122")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	private static PersonMock createDoleDuck() {
		return new PersonMockBuilder()
				.setFodselsnummer("12017512345")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn("Dole")
				.setMellomnavn(null)
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 3, "D", 0123))
				.setPostadresse(null)
				.setUtenlandsadresse(null)
				.setEnhet("8172")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	private static PersonMock createOleDuck() {
		return new PersonMockBuilder()
				.setFodselsnummer("01017591234")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn("Ole")
				.setMellomnavn(null)
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 2, "A", 0123))
				.setPostadresse(null)
				.setUtenlandsadresse(null)
				.setEnhet("0142")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	public static PersonMock createDollyDuck() {
		FIM_LANDKODER_2.setValue("EN");
		return new PersonMockBuilder()
				.setFodselsnummer("12127489123")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn("Dolly")
				.setMellomnavn(null)
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2("Veienstedet", 1, "B", 9999))
				.setPostadresse(null)
				.setUtenlandsadresse(new UstrukturertadresseMock(FIM_LANDKODER_2, "Incognitogaten 1, 9999 Bortevekk , Langtvekkistan", null, null))
				.setEnhet("0189")
				.setKjonn(KJONN)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	private static PersonMock createDonaldFDuck() {
		FIM_LANDKODER.setValue("NO");
		return new PersonMockBuilder()
				.setFodselsnummer("01017478912")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Fauntleroy")
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2(null, null, null, 0123))
				.setPostadresse(new UstrukturertadresseMock(FIM_LANDKODER, "Veienstedet", "1", "9999 Byen"))
				.setUtenlandsadresse(null)
				.setEnhet("0182")
				.setKjonn(null)
				.setDiskresjonskode(DISKRESJONSKODE1)
				.setPersonstatus(PersonstatusType.DOED.name())
				.createPersonMock();
	}



	private static PersonMock createDonaldSuperAgent() {
		return new PersonMockBuilder()
				.setFodselsnummer("01020304007")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Superagent")
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2(null, null, null, null))
				.setUtenlandsadresse(null)
				.setEnhet("2103")
				.setKjonn(null)
				.setDiskresjonskode(DISKRESJONSKODE6)
				.setPersonstatus("Ukjent")
				.createPersonMock();
	}

	private static PersonMock createDonaldPusleAgent() {
		return new PersonMockBuilder()
				.setFodselsnummer("01020304000")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Pusleagent")
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2(null, null, null, null))
				.setUtenlandsadresse(null)
				.setEnhet("2103")
				.setKjonn(null)
				.setDiskresjonskode(DISKRESJONSKODE7)
				.setPersonstatus("Ukjent")
				.createPersonMock();
	}

	private static PersonMock createDonaldNAVansatt() {
		return new PersonMockBuilder()
				.setFodselsnummer("01077478000")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Egenansatt")
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2(null, null, null, null))
				.setUtenlandsadresse(null)
				.setEnhet("0122")
				.setKjonn(null)
				.setDiskresjonskode(DISKRESJONSKODE5)
				.setPersonstatus("Ukjent")
				.createPersonMock();
	}

	private static PersonMock createDonaldNAVBrukerDuck() {
		FIM_LANDKODER.setValue("NO");
		return new PersonMockBuilder()
				.setFodselsnummer("22222222222")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("NAV-bruker")
				.setEtternavn("Duck")
				.setBostedsadresse(createBostedsadresse2("Apalveien", 132, "A", 0001))
				.setPostadresse(new UstrukturertadresseMock(FIM_LANDKODER, "Apalveien", "123", "0001 Andeby"))
				.setUtenlandsadresse(null)
				.setEnhet("0182")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(null)
				.createPersonMock();
	}

	private static PersonMock createDonaldNilsNilsen() {
		FIM_LANDKODER.setValue("NO");
		return new PersonMockBuilder()
				.setFodselsnummer("01047478000")
				.setIdenttype(FIMPERSONIDENTERF)
				.setFornavn(FORNAVN_D)
				.setMellomnavn("Nils")
				.setEtternavn("Nilsen")
				.setBostedsadresse(createBostedsadresse2("Gatenveien", 40, "A", 0123))
				.setPostadresse(new UstrukturertadresseMock(FIM_LANDKODER, "Veienstedet", "1", "9999 Byen"))
				.setUtenlandsadresse(null)
				.setEnhet("0322")
				.setKjonn(null)
				.setDiskresjonskode(null)
				.setPersonstatus(PersonstatusType.DOED.name())
				.createPersonMock();
	}
}