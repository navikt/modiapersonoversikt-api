package no.nav.kjerneinfo.domene.person;

import no.nav.kjerneinfo.domain.person.Fodselsnummer;
import no.nav.kjerneinfo.domene.factory.PersonDoFactory;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FodselsnummerTest {

	private String fnrStringMann = "01019000164";
	private String fnrStringKvinne = "01019000083";
	private String dnrStringKvinne = "44118900224";

    //Fiktive nr som ikke sjekker kontrollsiffre
    //Men som følger regler for individnr og år
    private String ugyldigFnr = "01055055011";

	private Fodselsnummer fnr;

	@Before
	public void setUp() {
		fnr = new Fodselsnummer();
	}

	@Test
	public void sjekkCreateFodselsnummer() {
		String toStringText = "Fodselsnummer [fodselsnummer=" + fnrStringMann + "]";
		assertEquals(toStringText,
				PersonDoFactory.createFodselsnummer(fnrStringMann).toString());

	}

	@Test
	public void sjekkIsDnr() {
		fnr.setNummer(dnrStringKvinne);
		assert (fnr.isDnummer());
		fnr.setNummer(fnrStringKvinne);
		assertFalse(fnr.isDnummer());
	}

	@Test
	public void sjekkFodselsDato() {

		fnr.setNummer(fnrStringKvinne);
		assertEquals(18, fnr.getFodselsdato().getDayOfMonth());
		assertEquals(7, fnr.getFodselsdato().getMonthOfYear());
		assertEquals(1975, fnr.getFodselsdato().getYear());

		fnr.setNummer(dnrStringKvinne);
		assertEquals(4, fnr.getFodselsdato().getDayOfMonth());
		assertEquals(11, fnr.getFodselsdato().getMonthOfYear());
		assertEquals(1989, fnr.getFodselsdato().getYear());

        sjekkget4DigitYearOfBirthWithAdjustedFnr();
	}

    @Test
    public void parsingAvFodselsnummerForDodfodt() throws Exception {
        fnr.setNummer("29020000001");
        assertEquals(29, fnr.getFodselsdato().getDayOfMonth());
        assertEquals(2, fnr.getFodselsdato().getMonthOfYear());
        assertEquals(0, fnr.getFodselsdato().getYear());
    }

    private void sjekkget4DigitYearOfBirthWithAdjustedFnr() {
        fnr.setNummer(ugyldigFnr);
        try {
            fnr.getFodselsdato();
            fail();
        } catch (IllegalArgumentException e) {
        }

        fnr.setNummer("01016050012");
        assertEquals(1860, fnr.getFodselsdato().getYear());

        fnr.setNummer("01018000055");
        assertEquals(1980, fnr.getFodselsdato().getYear());

        fnr.setNummer("01019000083");
        assertEquals(1990, fnr.getFodselsdato().getYear());

        fnr.setNummer("01014500037");
        assertEquals(1945, fnr.getFodselsdato().getYear());

        fnr.setNummer("01013050038");
        assertEquals(2030, fnr.getFodselsdato().getYear());

        fnr.setNummer("01011050081");
        assertEquals(2010, fnr.getFodselsdato().getYear());
    }

	@Test
	public void sjekkIsFemale() {
		fnr.setNummer(fnrStringKvinne);
		assert (fnr.isFemale());
		fnr.setNummer(dnrStringKvinne);
		assert (fnr.isFemale());
		fnr.setNummer(fnrStringMann);
		assertFalse(fnr.isFemale());
	}

    @Test
    public void sjekkUnderOneYear() {
        fnr.setNummer(fnrStringMann);
        assertFalse(fnr.isUnder1Year());

        String fnrUnderEtAar = createFnrUnderAar();
        fnr.setNummer(fnrUnderEtAar);
        assert (fnr.isUnder1Year());

    }

    private String createFnrUnderAar() {
        int year = (new LocalDate().getYear())-2001;
        return "3112" + year + "76012";
    }
}