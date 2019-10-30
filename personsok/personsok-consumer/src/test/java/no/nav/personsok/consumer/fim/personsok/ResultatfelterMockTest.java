package no.nav.personsok.consumer.fim.personsok;

import no.nav.personsok.consumer.fim.personsok.mock.ResultatfelterMock;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResultatfelterMockTest {

	private static final String ROW = "A";
	private static final String FODSELSNUMMER = "B";
	private static final String NAVN = "C";
	private static final String kommunenr = "D";
	private static final String DISKRESJONSKODE = "E";


	@Test
	public void objectTest() {

		ResultatfelterMock resultatfelterMockActual = createResultatfelterMock();

		assertEquals(ROW, resultatfelterMockActual.getRow());
		assertEquals(FODSELSNUMMER, resultatfelterMockActual.getFodselsnummer());
		assertEquals(NAVN, resultatfelterMockActual.getNavn());
		assertEquals(kommunenr, resultatfelterMockActual.getKommunenr());
		assertEquals(DISKRESJONSKODE, resultatfelterMockActual.getDiskresjonskode());
	}

	private ResultatfelterMock createResultatfelterMock() {
		ResultatfelterMock resultatfelterMock = new ResultatfelterMock();

		resultatfelterMock.setRow(ROW);
		resultatfelterMock.setFodselsnummer(FODSELSNUMMER);
		resultatfelterMock.setNavn(NAVN);
		resultatfelterMock.setKommunenr(kommunenr);
		resultatfelterMock.setDiskresjonskode(DISKRESJONSKODE);

		return resultatfelterMock;
	}
}
