import no.nav.brukerprofil.domain.*;
import no.nav.brukerprofil.domain.adresser.Gateadresse;
import no.nav.brukerprofil.domain.adresser.StrukturertAdresse;
import no.nav.brukerprofil.domain.adresser.UstrukturertAdresse;
import no.nav.kjerneinfo.common.domain.Kodeverdi;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class BrukerTest {

	private String FNR = "12345678910";
	private String BOLIGNUMMER = "bnr1";
	private String GATENAVN = "gnavn1";
	private String GATENUMMER = "gnr1";
	private String POSTNUMMER = "prn1";
	private String HUSBOKSTAV = "h";
	private String ADRLINJE1 = "adrlinje1";
	private String ADRLINJE2 = "adrlinje2";
	private String ADRLINJE3 = "adrlinje3";
	private String BANKNAVN = "banknavn1";
	private String KONTONUMMER = "kontnr1";
	private String MOBILNR = "55441122";
	private String MOBILRETNINGSNUMMER = "+47";
	private static String ENDRETAV = "Ola N";
	private static LocalDateTime TIME = new LocalDateTime(2013, 12, 2, 13, 30);
	private String HJEMNR = "55662233";
	private String JOBBNR = "88774455";
	private String BANKKODE = "bankkode";
	private Kodeverdi LANDKODE = new Kodeverdi("landkode", "landkode");
	private String SWIFT = "swift";
	private Kodeverdi VALUTA = new Kodeverdi("aud", "aud");
	private String DKIFMOBILTELEFON = "12345678";
	private String DKIFEPOST = "test@minid.no";
	private String DKIFRESERVERT = "true";

	@Test
	public void sjekkOpprettBruker() {
		Bruker Bruker = createBruker(FNR, BOLIGNUMMER, GATENAVN, GATENUMMER, POSTNUMMER, HUSBOKSTAV,
				BANKNAVN, KONTONUMMER, MOBILNR, HJEMNR, JOBBNR, DKIFMOBILTELEFON, DKIFEPOST, DKIFRESERVERT);

		sjekkBruker(Bruker);
	}

	@Test
	public void sjekkOpprettBruker2() {
		Bruker Bruker = createBruker2(FNR, ADRLINJE1, ADRLINJE2, ADRLINJE3, BANKNAVN, KONTONUMMER, MOBILNR, HJEMNR, JOBBNR,
				BANKKODE, LANDKODE, SWIFT, VALUTA, DKIFMOBILTELEFON, DKIFEPOST, DKIFRESERVERT);

		sjekkBruker(Bruker);
	}

	@Test
	public void alleNavnBlirUpperCaset() {
		Bruker bruker = new Bruker();
		String fornavn = "Fornavn";
		String mellomnavn = "Mellomnavn";
		String etternavn = "Etternavn";


		bruker.setFornavn(new Navn(fornavn));
		bruker.setMellomnavn(new Navn(mellomnavn));
		bruker.setEtternavn(new Navn(etternavn));

		assertThat(bruker.getFornavn().getNavn(), is(fornavn.toUpperCase()));
		assertThat(bruker.getMellomnavn().getNavn(), is(mellomnavn.toUpperCase()));
		assertThat(bruker.getEtternavn().getNavn(), is(etternavn.toUpperCase()));
	}

	private void sjekkBruker(Bruker bruker) {
		assertEquals(bruker.getIdent(), FNR);
		assertEquals(bruker.getMobil().getIdentifikator(), MOBILNR);
		assertEquals(bruker.getMobil().getRetningsnummer().getKodeRef(), MOBILRETNINGSNUMMER);
		assertEquals(bruker.getMobil().getEndretAv(), ENDRETAV);
		assertEquals(bruker.getMobil().getEndringstidspunkt(), TIME);
		assertEquals(bruker.getHjemTlf().getIdentifikator(), HJEMNR);
		assertEquals(bruker.getJobbTlf().getIdentifikator(), JOBBNR);
		assertEquals(bruker.getBankkonto().getKontonummer(), KONTONUMMER);
		assertEquals(bruker.getDkifMobiltelefon().getIdentifikator(), DKIFMOBILTELEFON);
		assertEquals(bruker.getDkifEpost().getIdentifikator(), DKIFEPOST);
		assertEquals(bruker.getDkifReservasjon(), DKIFRESERVERT);
		sjekkBankkonto(bruker.getBankkonto());
		if (bruker.getBostedsadresse() != null) {
			sjekkGateadresse(bruker.getBostedsadresse());
		}
	}

	private void sjekkBankkontoUtland(BankkontoUtland bankkontoUtland) {
		assertEquals(KONTONUMMER, bankkontoUtland.getKontonummer());
		assertEquals(BANKNAVN, bankkontoUtland.getBanknavn());
		sjekkUstrukturertadresse(bankkontoUtland.getBankadresse());
		assertEquals(BANKKODE, bankkontoUtland.getBankkode());
		assertEquals(LANDKODE, bankkontoUtland.getLandkode());
		assertEquals(VALUTA, bankkontoUtland.getValuta());
		assertEquals(SWIFT, bankkontoUtland.getSwift());
	}

	private void sjekkBankkonto(Bankkonto bankkonto) {
		assertEquals(BANKNAVN, bankkonto.getBanknavn());
		assertEquals(KONTONUMMER, bankkonto.getKontonummer());
		assertEquals(ENDRETAV, bankkonto.getEndretAv());
		assertEquals(TIME, bankkonto.getEndringstidspunkt());
		if (bankkonto instanceof BankkontoUtland) {
			sjekkBankkontoUtland((BankkontoUtland) bankkonto);
		}
	}

	private void sjekkUstrukturertadresse(UstrukturertAdresse adresse) {
		assert (adresse instanceof UstrukturertAdresse);
		assertEquals(ADRLINJE1, adresse.getAdresselinje1());
		assertEquals(ADRLINJE2, adresse.getAdresselinje2());
		assertEquals(ADRLINJE3, adresse.getAdresselinje3());
	}

	private void sjekkGateadresse(StrukturertAdresse adresse) {
		assert (adresse instanceof Gateadresse);
		assertEquals(BOLIGNUMMER, ((Gateadresse)adresse).getBolignummer());
		assertEquals(GATENAVN, ((Gateadresse)adresse).getGatenavn());
		assertEquals(GATENUMMER, ((Gateadresse)adresse).getHusnummer());
		assertEquals(POSTNUMMER , ((Gateadresse)adresse).getPoststed());
		assertEquals(HUSBOKSTAV, ((Gateadresse)adresse).getHusbokstav());
	}


	private static Bruker createBruker(String fnr, String bolignummer, String gatenavn, String gatenummer, String postnummer,
									   String husbokstav, String banknavn, String kontonummer, String mobilnr,
									   String hjemnr, String jobbnr, String dkifMobiltelefon, String dkifEpost, String dkifReservasjon) {

		Bruker bruker = new Bruker();

		bruker.setIdent(fnr);
		bruker.setBankkonto(createBankkonto(banknavn, kontonummer));
		setTelefoner(mobilnr, hjemnr, jobbnr, bruker);
		bruker.setBostedsadresse(createGateadresse(bolignummer, gatenavn, gatenummer, husbokstav, postnummer));
		bruker.setDkifMobiltelefon(createTelefon(dkifMobiltelefon, new Kodeverdi("DKIF", "dkifMobiltelefon")));
		bruker.setDkifEpost(createEpost(dkifEpost));
		bruker.setDkifReservasjon(dkifReservasjon);
		return bruker;
	}

	private static Bruker createBruker2(String fnr, String adrlinje1, String adrlinje2, String adrlinje3,
									   String banknavn, String kontonummer, String mobilnr,
									   String hjemnr, String jobbnr, String bankkode, Kodeverdi landkode, String swift, Kodeverdi valuta,
										String dkifMobiltelefon, String dkifEpost, String dkifReservasjon) {
		Bruker bruker = new Bruker();

		bruker.setIdent(fnr);
		bruker.setBankkonto(createBankkontoUtland(banknavn, kontonummer, bankkode, landkode, swift, valuta, adrlinje1, adrlinje2, adrlinje3));
		setTelefoner(mobilnr, hjemnr, jobbnr, bruker);
		bruker.setDkifMobiltelefon(createTelefon(dkifMobiltelefon, new Kodeverdi("DKIF", "dkifMobiltelefon")));
		bruker.setDkifEpost(createEpost(dkifEpost));
		bruker.setDkifReservasjon(dkifReservasjon);

		return bruker;
	}

	private static Bankkonto createBankkonto(String banknavn, String kontonummer) {
		Bankkonto bankkonto = new Bankkonto();
		bankkonto.setKontonummer(kontonummer);
		bankkonto.setBanknavn(banknavn);
		bankkonto.setEndretAv(ENDRETAV);
		bankkonto.setEndringstidspunkt(TIME);
		return bankkonto;
	}

	private static Bankkonto createBankkontoUtland(String banknavn, String kontonummer, String bankkode, Kodeverdi landkode, String swift, Kodeverdi valuta, String adrlinje1, String adrlinje2, String adrlinje3) {
		BankkontoUtland bankkonto = new BankkontoUtland();
		bankkonto.setKontonummer(kontonummer);
		bankkonto.setBanknavn(banknavn);
		bankkonto.setBankkode(bankkode);
		bankkonto.setLandkode(landkode);
		bankkonto.setSwift(swift);
		bankkonto.setValuta(valuta);
		bankkonto.setBankadresse(createUstrukturertadresse(adrlinje1, adrlinje2, adrlinje3));
		bankkonto.setEndretAv(ENDRETAV);
		bankkonto.setEndringstidspunkt(TIME);
		return bankkonto;
	}

	private static Epost createEpost(String identifikator) {
		Epost epost = new Epost();
		epost.setIdentifikator(identifikator);
		return epost;
	}

	private static void setTelefoner(String mobilnr, String hjemnr, String jobbnr, Bruker bruker) {
		Kodeverdi mobilKode = new Kodeverdi("MOBI", "mobil");
		Telefon mobil = createTelefon(mobilnr, mobilKode);
		mobil.setEndretAv(ENDRETAV);
		mobil.setEndringstidspunkt(TIME);

		Kodeverdi hjemKode = new Kodeverdi("HJET", "hjem");
		Telefon hjemTelefon = createTelefon(hjemnr, hjemKode);

		Kodeverdi jobbKode = new Kodeverdi("ARBT", "jobb");
		Telefon jobbtelefon = createTelefon(jobbnr, jobbKode);

		bruker.setMobil(mobil);
		bruker.setHjemTlf(hjemTelefon);
		bruker.setJobbTlf(jobbtelefon);
	}


	private static Telefon createTelefon(String identifikator, Kodeverdi kodeverdi) {
		Telefon telefon = new Telefon();
		telefon.setIdentifikator(identifikator);
		telefon.setType(kodeverdi);
		telefon.setRetningsnummer(new Kodeverdi("+47", "NORGE"));
		return telefon;
	}


	private static UstrukturertAdresse createUstrukturertadresse(String adrlinje1, String adrlinje2, String adrlinje3) {
		UstrukturertAdresse adresse = new UstrukturertAdresse();
		adresse.setAdresselinje1(adrlinje1);
		adresse.setAdresselinje2(adrlinje2);
		adresse.setAdresselinje3(adrlinje3);
		return adresse;
	}

	private static Gateadresse createGateadresse(String bolignummer, String gatenavn, String gatenummer, String husbokstav, String postnummer) {
		Gateadresse gateadresse = new Gateadresse();
		gateadresse.setGatenavn(gatenavn);
		gateadresse.setHusnummer(gatenummer);
		gateadresse.setBolignummer(bolignummer);
		gateadresse.setHusbokstav(husbokstav);
		gateadresse.setPoststed(postnummer);
		return gateadresse;
	}
}
