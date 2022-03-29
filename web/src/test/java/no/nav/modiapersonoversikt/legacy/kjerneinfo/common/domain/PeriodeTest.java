package no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain;

import no.nav.modiapersonoversikt.commondomain.Periode;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PeriodeTest {

    @Test
    public void testIsNotGyldigNullFrom() {
        assertFalse(new Periode(null, new LocalDate()).erGyldig());
    }

    @Test
    public void testGyldigBasedOnFom() {
        assertTrue(new Periode(new LocalDate(), null).erGyldig());
    }

    @Test
    public void testIsNotGyldigRange() {
        assertFalse(new Periode(new LocalDate(2000, 1, 1), new LocalDate(2010, 1, 1)).erGyldig());
    }

    @Test
    public void testIsGyldig() {
        assertTrue(new Periode(new LocalDate(2000, 1, 1), LocalDate.now()).erGyldig());
    }
}