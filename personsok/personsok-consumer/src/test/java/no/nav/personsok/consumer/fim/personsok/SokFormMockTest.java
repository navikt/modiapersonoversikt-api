package no.nav.personsok.consumer.fim.personsok;

import no.nav.personsok.consumer.fim.personsok.mock.SokFormMock;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SokFormMockTest {

	private static final String IDNUMMER = "A";
	private static final String GATENAVN = "C";
	private static final String HUSNUMMER = "D";
	private static final String HUSBOKSTAV = "E";
	private static final String POSTNUMMER = "F";
	private static final String KONTONUMMER = "G";
	private static final String FORNAVN = "H";
	private static final String ETTERNAVN = "I";
	private static final String ENHET = "J";
	private static final String FOEDSELSDATO_FRA = "K";
	private static final String FOEDSELSDATO_TIL = "L";
	private static final String ALDER_FRA = "M";
	private static final String ALDER_TIL = "N";
	private static final String KJOENN = "O";


	@Test
	public void objectTest() {
		SokFormMock sokFormMockActual = createSokFormMock();

		assertEquals(ALDER_FRA, sokFormMockActual.getAlderFra());
		assertEquals(ALDER_TIL, sokFormMockActual.getAlderTil());
		assertEquals(ENHET, sokFormMockActual.getEnhet());
		assertEquals(ETTERNAVN, sokFormMockActual.getEtternavn());
		assertEquals(FOEDSELSDATO_FRA, sokFormMockActual.getFoedselsdatoFra());
		assertEquals(FOEDSELSDATO_TIL, sokFormMockActual.getFoedselsdatoTil());
		assertEquals(FORNAVN, sokFormMockActual.getFornavn());
		assertEquals(GATENAVN, sokFormMockActual.getGatenavn());
		assertEquals(HUSBOKSTAV, sokFormMockActual.getHusbokstav());
		assertEquals(HUSNUMMER, sokFormMockActual.getHusnummer());
		assertEquals(IDNUMMER, sokFormMockActual.getIdnummer());
		assertEquals(KJOENN, sokFormMockActual.getKjoenn());
		assertEquals(KONTONUMMER, sokFormMockActual.getKontonummer());
		assertEquals(POSTNUMMER, sokFormMockActual.getPostnummer());
	}

	private SokFormMock createSokFormMock() {
		SokFormMock sokFormMock = new SokFormMock();

		sokFormMock.setAlderFra(ALDER_FRA);
		sokFormMock.setAlderTil(ALDER_TIL);
		sokFormMock.setEnhet(ENHET);
		sokFormMock.setEtternavn(ETTERNAVN);
		sokFormMock.setFoedselsdatoFra(FOEDSELSDATO_FRA);
		sokFormMock.setFoedselsdatoTil(FOEDSELSDATO_TIL);
		sokFormMock.setFornavn(FORNAVN);
		sokFormMock.setGatenavn(GATENAVN);
		sokFormMock.setHusbokstav(HUSBOKSTAV);
		sokFormMock.setHusnummer(HUSNUMMER);
		sokFormMock.setIdnummer(IDNUMMER);
		sokFormMock.setKjoenn(KJOENN);
		sokFormMock.setKontonummer(KONTONUMMER);
		sokFormMock.setPostnummer(POSTNUMMER);

		return sokFormMock;
	}

}
