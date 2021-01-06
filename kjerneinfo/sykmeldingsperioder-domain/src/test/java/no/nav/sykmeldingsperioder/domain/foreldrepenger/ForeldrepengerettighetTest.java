package no.nav.sykmeldingsperioder.domain.foreldrepenger;

import no.nav.sykmeldingsperioder.domain.Arbeidsforhold;
import no.nav.sykmeldingsperioder.domain.Bruker;
import no.nav.sykmeldingsperioder.domain.Kodeverkstype;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class ForeldrepengerettighetTest {

    public static final String ARBEIDSFORHOLD_KONTONUMMER = "1234.12.7845";
    public static final String ARBEIDSFORHOLD_NAVN = "Statoil";
    public static final LocalDate ARBEIDSFORHOLD_REFFOM = new LocalDate(2013, 4, 1);
    public static final String ANDREFORELDRESFNR = "12345678954";
    public static final LocalDate BARNETSFOEDSELSDATO = new LocalDate(2013, 1, 13);
    public static final Double DEKNINSGRAD = (double) 60;
    public static final LocalDate FEDREKVOTETOM = new LocalDate(2013, 1, 1);
    public static final LocalDate MOEDREKVOTE = new LocalDate(2013, 1, 2);
    public static final Kodeverkstype FORELDREPENGETYPE = new Kodeverkstype("kode", "term");
    public static final Integer GRADERINGSDAGER = Integer.parseInt("60");
    public static final LocalDate MAKSDATO = new LocalDate(2013, 1, 1);
    public static final Integer RESTDAGER = Integer.parseInt("60");
    public static final LocalDate RETTIGHETFOM = new LocalDate(2013, 1, 1);
    public static final String BRUKERID = "ABC123";
    public static final Bruker bruker = new Bruker(BRUKERID);
    public static final int ANTALL_BARN = 8;

    @Test
    public void testBean() {
        Foreldrepengerettighet foreldrepengerettighet = new Foreldrepengerettighet();
        foreldrepengerettighet.setAndreForeldersFnr(ANDREFORELDRESFNR);
        List<Arbeidsforhold> arbeidsforholdListe = new ArrayList<>();
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        arbeidsforhold.setArbeidsgiverNavn(ARBEIDSFORHOLD_NAVN);
        arbeidsforhold.setArbeidsgiverKontonr(ARBEIDSFORHOLD_KONTONUMMER);
        arbeidsforhold.setRefusjonTom(ARBEIDSFORHOLD_REFFOM);
        arbeidsforholdListe.add(arbeidsforhold);
        foreldrepengerettighet.setArbeidsforholdListe(arbeidsforholdListe);
        foreldrepengerettighet.setBarnetsFoedselsdato(BARNETSFOEDSELSDATO);
        foreldrepengerettighet.setDekningsgrad(DEKNINSGRAD);
        foreldrepengerettighet.setFedrekvoteTom(FEDREKVOTETOM);
        foreldrepengerettighet.setMoedrekvoteTom(MOEDREKVOTE);
        foreldrepengerettighet.setForeldrepengetype(FORELDREPENGETYPE);
        foreldrepengerettighet.setGraderingsdager(GRADERINGSDAGER);
        foreldrepengerettighet.setSlutt(MAKSDATO);
        foreldrepengerettighet.setRestDager(RESTDAGER);
        foreldrepengerettighet.setRettighetFom(RETTIGHETFOM);
        foreldrepengerettighet.setForelder(bruker);
        foreldrepengerettighet.setAntallBarn(ANTALL_BARN);
        foreldrepengerettighet.setForeldreAvSammeKjoenn(new Kodeverkstype("test", "TEST"));
        List<Foreldrepengeperiode> foreldrepengeperiode = foreldrepengerettighet.getPeriode();
        foreldrepengerettighet.setPeriode(foreldrepengeperiode);

        assertEquals(ANDREFORELDRESFNR, foreldrepengerettighet.getAndreForeldersFnr());
        assertEquals(BARNETSFOEDSELSDATO, foreldrepengerettighet.getBarnetsFoedselsdato());
        assertEquals(DEKNINSGRAD, foreldrepengerettighet.getDekningsgrad());
        assertEquals(MOEDREKVOTE, foreldrepengerettighet.getMoedrekvoteTom());
        assertEquals(FEDREKVOTETOM, foreldrepengerettighet.getFedrekvoteTom());
        assertEquals(FORELDREPENGETYPE, foreldrepengerettighet.getForeldrepengetype());
        assertEquals(GRADERINGSDAGER, foreldrepengerettighet.getGraderingsdager());
        assertEquals(MAKSDATO, foreldrepengerettighet.getSlutt());
        assertEquals(RESTDAGER, foreldrepengerettighet.getRestDager());
        assertEquals(RETTIGHETFOM, foreldrepengerettighet.getRettighetFom());
        assertEquals(bruker, foreldrepengerettighet.getForelder());
        assertEquals(ANTALL_BARN, foreldrepengerettighet.getAntallBarn().intValue());
        assertEquals("TEST", foreldrepengerettighet.getForeldreAvSammeKjoenn().getTermnavn());
        assertThat(foreldrepengerettighet.getArbeidsforholdListe().get(0), equalTo(arbeidsforhold));
    }

    /**
     * Termindato brukes rettighet før fødselen. Etter fødsel blankes ut termindato og rettigheten må beregnes fra og med
     * fødselsdato.
     */
    @Test
    public void tomTermindato() {
        Foreldrepengerettighet foreldrepengerettighet = new Foreldrepengerettighet();
        foreldrepengerettighet.setRettighetFom(null);
        foreldrepengerettighet.setBarnetsFoedselsdato(BARNETSFOEDSELSDATO);

        assertThat(foreldrepengerettighet.getRettighetFom(), equalTo(BARNETSFOEDSELSDATO));
    }

    @Test
    public void ingenPerioder() {
        Foreldrepengerettighet foreldrepengerettighet = new Foreldrepengerettighet();
        foreldrepengerettighet.setPeriode(new ArrayList<Foreldrepengeperiode>());

        assertThat(foreldrepengerettighet.getPeriode().size(), equalTo(0));
    }
}
