package no.nav.personsok.consumer.fim.personsok;

import no.nav.personsok.consumer.fim.personsok.mock.SorterFelterMock;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SorterFelterMockTest {

	private static final String FODSELSNUMMER_DIRECTION = "A";
	private static final String NAVN_DIRECTION = "B";
	private static final String kommunenr_DIRECTION = "C";
	private static final String FORSTE = "D";
	private static final String FORRIGE = "E";
	private static final String NESTE = "F";
	private static final String SISTE = "G";
	private static final String DISKRESJONSKODE = "H";

	@Test
	public void objectTest() {

		SorterFelterMock sorterFelterMockActual = createSorterFelterMock();

		assertEquals(FODSELSNUMMER_DIRECTION, sorterFelterMockActual.getFodselsnummer());
		assertEquals(NAVN_DIRECTION, sorterFelterMockActual.getNavn());
		assertEquals(kommunenr_DIRECTION, sorterFelterMockActual.getKommunenr());
		assertEquals(FORSTE, sorterFelterMockActual.getForste());
		assertEquals(FORRIGE, sorterFelterMockActual.getForrige());
		assertEquals(NESTE, sorterFelterMockActual.getNeste());
		assertEquals(SISTE, sorterFelterMockActual.getSiste());
		assertEquals(DISKRESJONSKODE, sorterFelterMockActual.getDiskresjonskode());
	}

	private SorterFelterMock createSorterFelterMock() {

		SorterFelterMock sorterFelterMock = new SorterFelterMock();

		sorterFelterMock.setFodselsnummer(FODSELSNUMMER_DIRECTION);
		sorterFelterMock.setNavn(NAVN_DIRECTION);
		sorterFelterMock.setKommunenr(kommunenr_DIRECTION);
		sorterFelterMock.setForste(FORSTE);
		sorterFelterMock.setForrige(FORRIGE);
		sorterFelterMock.setNeste(NESTE);
		sorterFelterMock.setSiste(SISTE);
		sorterFelterMock.setDiskresjonskode(DISKRESJONSKODE);

		return sorterFelterMock;
	}
}
