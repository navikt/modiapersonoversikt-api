package no.nav.sykmeldingsperioder.domain.pleiepenger;

import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PeriodeTest {

    @Test
    public void dagensDatoMidtIPeriodenGirGjeldende() {
        LocalDate today = LocalDate.now();
        Periode periode = new Periode(today.minusDays(10), today.plusDays(10));

        assertThat(periode.erGjeldende(), is(true));
    }

    @Test
    public void dagensDatoLikNedreSkrankeGirGjeldende() {
        LocalDate today = LocalDate.now();
        Periode periode = new Periode(today, today.plusDays(10));

        assertThat(periode.erGjeldende(), is(true));
    }

    @Test
    public void dagensDatoLikOevreSkrankeGirGjeldende() {
        LocalDate today = LocalDate.now();
        Periode periode = new Periode(today.minusDays(10), today);

        assertThat(periode.erGjeldende(), is(true));
    }

    @Test
    public void dagensDatoFoerNedreSkrankeGirIkkeGjeldende() {
        LocalDate today = LocalDate.now();
        Periode periode = new Periode(today.plusDays(1), today.plusDays(10));

        assertThat(periode.erGjeldende(), is(false));
    }

    @Test
    public void dagensDatoEtterOevreSkrankeGirIkkeGjeldende() {
        LocalDate today = LocalDate.now();
        Periode periode = new Periode(today.minusDays(10), today.minusDays(1));

        assertThat(periode.erGjeldende(), is(false));
    }

}
