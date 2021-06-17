package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.pleiepenger;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PleiepengeperiodeTest {

    private final LocalDate TODAY = LocalDate.now();

    @Test
    public void pleiepengerperiodeHarLagerTomVedtakslisteSomDefault() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode();
        assertThat(pleiepengeperiode.getVedtakListe(), is(notNullValue()));
    }

    @Test
    public void pleiepengerperiodeHarLagerTomArbeidsforholdListeSomDefault() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode();
        assertThat(pleiepengeperiode.getArbeidsforholdListe(), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void arbeidsforholdListeKanIkkeSettesTilNull() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode();
        pleiepengeperiode.withArbeidsforholdListe(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void vedtaksListeKanIkkeSettesTilNull() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode();
        pleiepengeperiode.withVedtakListe(null);
    }

    @Test
    public void getAktueltVedtakReturnererEmptyHvisVedtakslisteErTom() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode();

        assertThat(pleiepengeperiode.getAktueltVedtak(), is(empty()));
    }

    @Test
    public void getAktueltVedtakReturnererGjeldendeVedtakHvisVedtakErForstIListen() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode()
                .withVedtakListe(Arrays.asList(
                        new Vedtak().withPeriode(new Periode(TODAY, TODAY.plusDays(14))),
                        new Vedtak().withPeriode(new Periode(TODAY.plusDays(15), TODAY.plusDays(31)))));

        Vedtak vedtak = pleiepengeperiode.getAktueltVedtak().get();

        assertThat(vedtak.getPeriode().fraOgMed, is(TODAY));
    }

    @Test
    public void getAktueltVedtakReturnererGjeldendeVedtakHvisVedtakErSistIListen() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode()
                .withVedtakListe(Arrays.asList(
                        new Vedtak().withPeriode(new Periode(TODAY.minusDays(31), TODAY.minusDays(15))),
                        new Vedtak().withPeriode(new Periode(TODAY.minusDays(14), TODAY))));

        Vedtak vedtak = pleiepengeperiode.getAktueltVedtak().get();

        assertThat(vedtak.getPeriode().tilOgMed, is(TODAY));
    }

    @Test
    public void getGjeldendeVedtakReturnererGjeldende() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode()
                .withVedtakListe(Arrays.asList(
                        new Vedtak().withPeriode(new Periode(TODAY.minusDays(31), TODAY.minusDays(15))),
                        new Vedtak().withPeriode(new Periode(TODAY.plusDays(31), TODAY.plusDays(55))),
                        new Vedtak().withPeriode(new Periode(TODAY.minusDays(14), TODAY))
                ));

        Vedtak vedtak = pleiepengeperiode.getAktueltVedtak().get();

        assertThat(vedtak.getPeriode().tilOgMed, is(TODAY));
    }

    @Test
    public void getAktueltVedtakMedIngenGjeldendePeriodeOgPeriodeIFremtidenReturnererNeste() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode()
                .withVedtakListe(Arrays.asList(
                        new Vedtak().withPeriode(new Periode(TODAY.minusDays(31), TODAY.minusDays(15))),
                        new Vedtak().withPeriode(new Periode(TODAY.plusDays(31), TODAY.plusDays(55))),
                        new Vedtak().withPeriode(new Periode(TODAY.minusDays(14), TODAY.minusDays(3)))
                ));

        Vedtak vedtak = pleiepengeperiode.getAktueltVedtak().get();

        assertThat(vedtak.getPeriode().fraOgMed, is(TODAY.plusDays(31)));
    }

    @Test
    public void getAktueltVedtakMedIngenGjeldendePeriodeOgPeriodeIFortidenReturnererForrige() {
        Pleiepengeperiode pleiepengeperiode = new Pleiepengeperiode()
                .withVedtakListe(Arrays.asList(
                        new Vedtak().withPeriode(new Periode(TODAY.minusDays(31), TODAY.minusDays(15))),
                        new Vedtak().withPeriode(new Periode(TODAY.minusMonths(6), TODAY.minusMonths(5))),
                        new Vedtak().withPeriode(new Periode(TODAY.minusDays(14), TODAY.minusDays(3)))
                ));

        Vedtak vedtak = pleiepengeperiode.getAktueltVedtak().get();

        assertThat(vedtak.getPeriode().tilOgMed, is(TODAY.minusDays(3)));
    }

}
