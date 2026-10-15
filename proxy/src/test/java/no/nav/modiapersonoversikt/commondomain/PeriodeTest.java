package no.nav.modiapersonoversikt.commondomain;

import java.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PeriodeTest {

    @Test
    public void testIsNotGyldigNullFrom() {
        assertFalse(new Periode(null, LocalDate.now()).erGyldig());
    }

    @Test
    public void testGyldigBasedOnFom() {
        assertTrue(new Periode(LocalDate.now(), null).erGyldig());
    }

    @Test
    public void testIsNotGyldigRange() {
        assertFalse(new Periode(LocalDate.of(2000, 1, 1), LocalDate.of(2010, 1, 1)).erGyldig());
    }

    @Test
    public void testIsGyldig() {
        assertTrue(new Periode(LocalDate.of(2000, 1, 1), LocalDate.now()).erGyldig());
    }
}