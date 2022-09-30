package no.nav.modiapersonoversikt.consumer.infotrygd.domain.pleiepenger;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PleiepengerrettighetTest {

    private static final String ARBEIDSKATEGORI = "Fisker";
    private static final LocalDate FRA_OG_MED_1 = LocalDate.now();
    private final LocalDate TIL_OG_MED_1 = FRA_OG_MED_1.plusWeeks(2);
    private final LocalDate FRA_OG_MED_2 = TIL_OG_MED_1.plusDays(1);
    private final LocalDate FRA_OG_MED_3 = FRA_OG_MED_1.plusMonths(1);
    private final LocalDate TIL_OG_MED_2 = FRA_OG_MED_3.minusDays(1);
    private final LocalDate TIL_OG_MED_3 = FRA_OG_MED_3.plusWeeks(2);
    private final LocalDate FRA_OG_MED_4 = TIL_OG_MED_3.plusDays(1);
    private final LocalDate TIL_OG_MED_4 = FRA_OG_MED_3.plusMonths(1).minusDays(1);
    private final Vedtak GJELDENDE_VEDTAK = new Vedtak().withPeriode(new Periode(FRA_OG_MED_1, TIL_OG_MED_1));
    private final Vedtak FREMTIDIG_VEDTAK_1 = new Vedtak().withPeriode(new Periode(FRA_OG_MED_2, TIL_OG_MED_2));
    private final Vedtak FREMTIDIG_VEDTAK_2 = new Vedtak().withPeriode(new Periode(FRA_OG_MED_3, TIL_OG_MED_3));
    private final Vedtak FREMTIDIG_VEDTAK_3 = new Vedtak().withPeriode(new Periode(FRA_OG_MED_4, TIL_OG_MED_4));
    private final Vedtak GAMMELT_VEDTAK_1 = new Vedtak()
            .withPeriode(new Periode(FRA_OG_MED_1.minusYears(1), TIL_OG_MED_1.minusYears(1)));
    private final Vedtak GAMMELT_VEDTAK_2 = new Vedtak()
            .withPeriode(new Periode(FRA_OG_MED_2.minusYears(1), TIL_OG_MED_2.minusYears(1)));
    private final Vedtak GAMMELT_VEDTAK_3 = new Vedtak()
            .withPeriode(new Periode(FRA_OG_MED_3.minusYears(1), TIL_OG_MED_3.minusYears(1)));
    private final Vedtak GAMMELT_VEDTAK_4 = new Vedtak()
            .withPeriode(new Periode(FRA_OG_MED_4.minusYears(1), TIL_OG_MED_4.minusYears(1)));
    private final Pleiepengeperiode GJELDENDE_PERIODE = lagPleiepengeperiode(GJELDENDE_VEDTAK);
    private final Pleiepengeperiode KOMMENDE_PERIODE_1 = lagPleiepengeperiode(FREMTIDIG_VEDTAK_1);
    private final Pleiepengeperiode KOMMENDE_PERIODE_2 = lagPleiepengeperiode(FREMTIDIG_VEDTAK_2);
    private final Pleiepengeperiode KOMMENDE_PERIODE_3 = lagPleiepengeperiode(FREMTIDIG_VEDTAK_3);

    private final Pleiepengeperiode GAMMEL_PERIODE_1 = lagPleiepengeperiode(GAMMELT_VEDTAK_1);
    private final Pleiepengeperiode GAMMEL_PERIODE_2 = lagPleiepengeperiode(GAMMELT_VEDTAK_2);
    private final Pleiepengeperiode GAMMEL_PERIODE_3 = lagPleiepengeperiode(GAMMELT_VEDTAK_3);
    private final Pleiepengeperiode GAMMEL_PERIODE_4 = lagPleiepengeperiode(GAMMELT_VEDTAK_4);


    private static final LocalDate FRA_OG_MED_2016 = LocalDate.parse("2016-08-16");
    private static final LocalDate FRA_OG_MED_2015 = LocalDate.parse("2015-08-16");
    private static final String ORGNUMMER_1 = "1234";
    private static final String ORGNUMMER_2 = "2345";
    private static final String ORGNUMMER_3 = "3456";

    private static Pleiepengeperiode lagPleiepengeperiode(Vedtak vedtak) {
        return new Pleiepengeperiode()
                .withArbeidskategori(ARBEIDSKATEGORI)
                .withFraOgMed(vedtak.getPeriode().fraOgMed())
                .withVedtakListe(singletonList(vedtak));
    }

    @Test
    public void pleiepengerRettighetSetterPerioderTilTomListe() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet();

        assertThat(pleiepengerrettighet.getPerioder().isEmpty(), is(true));
    }

    @Test
    public void getPerioderReturnererPerioder() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(singletonList(new Pleiepengeperiode()));

        List<Pleiepengeperiode> perioder = pleiepengerrettighet.getPerioder();

        assertThat(perioder.size(), is(1));
    }

    @Test
    public void withPerioderErstatterGammelListe() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(singletonList(new Pleiepengeperiode()));
        pleiepengerrettighet.withPerioder(asList(new Pleiepengeperiode(), new Pleiepengeperiode()));

        List<Pleiepengeperiode> perioder = pleiepengerrettighet.getPerioder();

        assertThat(perioder.size(), is(2));
    }

    @Test
    public void getAktuellPleiepengeperiodeReturnererGjeldendePeriodeNarDenErForstIListen() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(asList(GJELDENDE_PERIODE, KOMMENDE_PERIODE_1, GAMMEL_PERIODE_1));

        assertThat(pleiepengerrettighet.getAktuellPleiepengeperiode().get(), is(GJELDENDE_PERIODE));
    }

    @Test
    public void getAktuellPleiepengeperiodeReturnererGjeldendePeriodeNarDenErSistIListen() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(asList(GAMMEL_PERIODE_1, KOMMENDE_PERIODE_1, GJELDENDE_PERIODE));

        assertThat(pleiepengerrettighet.getAktuellPleiepengeperiode().get(), is(GJELDENDE_PERIODE));
    }

    @Test
    public void getAktuellPleiepengeperiodeReturnererNesteHvisAlleVedtakErIFremtiden() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(asList(KOMMENDE_PERIODE_1, KOMMENDE_PERIODE_2, KOMMENDE_PERIODE_3));

        Pleiepengeperiode pleiepengeperiode = pleiepengerrettighet.getAktuellPleiepengeperiode().get();

        assertThat(pleiepengeperiode.getFraOgMed(), is(KOMMENDE_PERIODE_1.getFraOgMed()));
    }

    @Test
    public void getAktuellPleiepengeperiodeReturnererForrigeHvisAlleVedtakErIFortiden() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(asList(GAMMEL_PERIODE_1, GAMMEL_PERIODE_2, GAMMEL_PERIODE_3));

        Pleiepengeperiode pleiepengeperiode = pleiepengerrettighet.getAktuellPleiepengeperiode().get();

        assertThat(pleiepengeperiode.getFraOgMed(), is(GAMMEL_PERIODE_3.getFraOgMed()));
    }

    @Test
    public void getAktuellPleiepengeperiodeReturnererGjeldendeHvisGjeldendeErSiste() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(asList(GAMMEL_PERIODE_1, GAMMEL_PERIODE_2, GJELDENDE_PERIODE));

        assertThat(pleiepengerrettighet.getAktuellPleiepengeperiode().get(), is(GJELDENDE_PERIODE));
    }

    @Test
    public void getAktuellPleiepengeperiodeReturnererGjeldendeHvisGjeldendeErMellomGamle() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(asList(GAMMEL_PERIODE_1, GJELDENDE_PERIODE, GAMMEL_PERIODE_2));

        assertThat(pleiepengerrettighet.getAktuellPleiepengeperiode().get(), is(GJELDENDE_PERIODE));
    }

    @Test
    public void getAktuellPleiepengeperiodeReturnererGjeldendeHvisGjeldendeErMellomKommende() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(asList(KOMMENDE_PERIODE_1, GJELDENDE_PERIODE, KOMMENDE_PERIODE_2));

        assertThat(pleiepengerrettighet.getAktuellPleiepengeperiode().get(), is(GJELDENDE_PERIODE));
    }

    @Test
    public void getAktuellPleiepengeperiodeReturnererEmptyHvisListeErTom() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet();

        assertThat(pleiepengerrettighet.getAktuellPleiepengeperiode().isPresent(), is(false));
    }

    @Test
    public void getForbrukteDagerEtterDennePeriodenReturnererRiktig() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withRestDagerAnvist(1105)
                .withPleiepengedager(1300);

        assertThat(pleiepengerrettighet.getTotaltDagerInnvilget(), is(195));
    }

    @Test
    public void getAlleArbeidsforholdSamlerInnArbeidsforholdFraFlerePerioder() {
        Pleiepengerrettighet pleiepengerrettighet = new Pleiepengerrettighet()
                .withPerioder(Arrays.asList(mockPeriodeMedToArbeidsforhold(), mockPeriodeMedEttArbeidsforhold()));

        List<Arbeidsforhold> arbeidsforhold = pleiepengerrettighet.getAlleArbeidsforhold();

        assertThat(arbeidsforhold.size(), is(3));
        assertThat(containsOrgnummer(arbeidsforhold, ORGNUMMER_1), is(true));
        assertThat(containsOrgnummer(arbeidsforhold, ORGNUMMER_2), is(true));
        assertThat(containsOrgnummer(arbeidsforhold, ORGNUMMER_3), is(true));
    }

    private Pleiepengeperiode mockPeriodeMedEttArbeidsforhold() {
        return new Pleiepengeperiode().withArbeidsforholdListe(Collections.singletonList(mockArbeidsforhold(ORGNUMMER_3)));
    }

    private Pleiepengeperiode mockPeriodeMedToArbeidsforhold() {
        return new Pleiepengeperiode().withArbeidsforholdListe(Arrays.asList(
                        mockArbeidsforhold(ORGNUMMER_1),
                        mockArbeidsforhold(ORGNUMMER_2)));
    }

    private Arbeidsforhold mockArbeidsforhold(String orgnummer) {
        return new Arbeidsforhold().withArbeidsgiverOrgnr(orgnummer);
    }

    private boolean containsOrgnummer(List<Arbeidsforhold> arbeidsforholdListe, String orgnummer) {
        return arbeidsforholdListe
                .stream()
                .anyMatch(arbeidsforhold -> arbeidsforhold.getArbeidsgiverOrgnr().equals(orgnummer));
    }

}
