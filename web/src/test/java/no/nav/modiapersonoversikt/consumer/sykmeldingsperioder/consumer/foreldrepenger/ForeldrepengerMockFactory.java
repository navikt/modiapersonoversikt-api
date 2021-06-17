package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.utils.DateUtils;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.*;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger.Foedsel;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengeperiode;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Denne klassen lager et responsobjekt som kan benyttes som svar fra den eksterne tjenesten for mockformål.
 */
public class ForeldrepengerMockFactory {

    public static final String PERSON_ID = "12345612345";
    public static final String ANDRE_FORELDER2 = "10128948527";
    public static final Date PERIODEFRA = new LocalDate(2013, 8, 6).toDate();
    public static final Date PERIODETOM = new LocalDate(2014, 2, 11).toDate();
    public static final Date PERIODEFRA2 = new LocalDate(2015, 8, 6).toDate();
    public static final Date PERIODETOM2 = new LocalDate(2016, 2, 11).toDate();
    public static final Date PERIODEFRA3 = new LocalDate(2012, 10, 10).toDate();
    public static final Date PERIODETOM3 = new LocalDate(2014, 10, 10).toDate();
    public static final Date ARBEIDSFORFOLD_SYKEPENGER_FOM2 = new LocalDate(2012, 10, 6).toDate();
    public static final Date ARBEIDSFORFOLD_REFUSJON_TOM2 = new LocalDate(2012, 12, 6).toDate();
    public static final String ARBEIDSFORHOLD_KONTONUMMER2 = "9999.12.7845";
    public static final String ARBEIDSFORHOLD_KONTONUMMER = "1234.12.7845";
    public static final String ARBEIDSFORHOLD_NAVN = "Statoil";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_KODE = "Reftype";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_TERM = "Generell refusjon";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_KODE2 = "Reftype2";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_TERM2 = "Generell refusjon2";
    public static final String ARBEIDSFORHOLD_NAVN2 = "Hydro";
    public static final String ARBEIDSKATEGORI_TERM = "Arbeidsledig";
    public static final Date ARBEIDSFORFOLD_SYKEPENGER_FOM = new LocalDate(2013, 7, 6).toDate();
    public static final Date ARBEIDSFORFOLD_REFUSJON_TOM = new LocalDate(2013, 10, 6).toDate();
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_KODE = "inntektsp1";
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_TERM = "Inntektsperiode 1";
    public static final Double ARBEIDSFORHOLD_INNTEKT = 625.20;
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_KODE2 = "inntektsp2";
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_TERM2 = "Inntektsperiode 2";
    public static final Double ARBEIDSFORHOLD_INNTEKT2 = 45.50;
    public static final String ARBEIDSGIVER_ORGNR = "123456789";
    public static final String ARBEIDSGIVER_ORGNR2 = "22222222222";
    public static final String SAKSBEHANDLER_IDENT = "SAKB001";
    public static final LocalDate IDDATO = new LocalDate(2012, 10, 20);
    public static final LocalDate IDDATO2 = new LocalDate(2011, 11, 25);
    public static final String ARBEIDSKATEGORI_KODE = "Arbeidskategori";
    public static final double KOMMENDE_UTBETALING_BRUTTOBELOP = 220.55;
    public static final double KOMMENDE_UTBETALING_DAGSATS1 = 120.89;
    public static final double KOMMENDE_UTBETALING2_BRUTTOBELOP = 22000.55;
    public static final double KOMMENDE_UTBETALING2_DAGSATS = 1000.00;
    public static final double KOMMENDE_UTBETALING2_GRAD = 65.0;
    public static final String KOMMENDE_UTBETALING1_OPPGJORSTYPE_KODE = "31";
    public static final String KOMMENDE_UTBETALING1_OPPGJORSTYPE_TERM = "OPGJ31";
    public static final double KOMMENDE_UTBETALING1_UTBETALINGSGRAD = 50.0;
    public static final String KOMMENDE_UTBETALING2_OPPGJORSTYPE_KODE = "61";
    public static final String KOMMENDE_UTBETALING2_OPPGJORSTYPE_TERM = "OPGJ61";
    public static final double KOMMENDE_UTBETALING2_UTBETALINGSGRAD = 70.0;
    public static final boolean ALENEOMSORGFAR_FALSE = false;
    public static final boolean ALENEOMSORGFAR_TRUE = true;
    public static final boolean ALENEOMSORGMOR_TRUE = true;
    public static final boolean ALENEOMSORGMOR_FALSE = false;
    public static final Double ARBEIDSPROSENTMOR = 50.0;
    public static final Double ARBEIDSPROSENTMOR2 = 80.0;
    public static final Double ARBEIDSPROSENTMOR3 = 30.0;
    public static final Date AVSLAGSDATO = new LocalDate(2013, 2, 1).toDate();
    public static final Double DISPONIBELGRADERING = 60.0;
    public static final Double DISPONIBELGRADERING2 = 40.0;
    public static final boolean FEDREKVOTE = false;
    public static final String FORSKYVELSESAARSAK_KODE = "Forskyvelsesaarsak";
    public static final String FORSKYVELSESAARSAK_TERM = "Generell forskyvelse";
    public static final String FORSKYVELSESAARSAK2_KODE = "Forskyvelsesaarsak";
    public static final String FORSKYVELSESAARSAK2_TERM = "Annen type forskyvelse";
    public static final FimPeriode FORSKYVELSESPERIODE = createFimPeriode(PERIODEFRA, PERIODETOM);
    public static final FimPeriode FORSKYVELSESPERIODE2 = createFimPeriode(PERIODEFRA2, PERIODETOM2);
    public static final String MORSSITUASJON_KODE = "Situasjonmor";
    public static final String MORSSITUASJON_TERM = "Ok";
    public static final String STANSAARSAK_KODE_P = "Stansaarsak";
    public static final String STANSAARSAK_TERM_P = "Midlertidig stanset";
    public static final Integer ANTALLBARN2 = 0;
    public static final String ANDREFORELDRESFNR = "01016515487";
    public static final String ANDREFORELDRESFNR2 = "02044575845";
    public static final Date BARNETSFOEDSELSDATO_A = new LocalDate(2013, 8, 2).toDate();
    public static final Date BARNETSFOEDSELSDATO_F = new LocalDate(2011, 5, 9).toDate();
    public static final Double DEKNINSGRAD = 60.0;
    public static final Date FEDREKVOTETOM = new LocalDate(2013, 8, 2).toDate();
    public static final Date FEDREKVOTETOM2 = new LocalDate(2012, 4, 1).toDate();
    public static final Date MODREKVOTETOM = new LocalDate(2013, 12, 2).toDate();
    public static final String FORELDREPENGETYPE_F_KODE = "01";
    public static final String FORELDREPENGETYPE_F_TERM = "Fødsel";
    public static final String FORELDREPENGETYPE_A_KODE = "02";
    public static final String FORELDREPENGETYPE_A_TERM = "Adopsjon";
    public static final String FORELDREAVSAMMEKJOENN_A_KODE = "A";
    public static final String FORELDREAVSAMMEKJOENN_A_TERM = "Mor";
    public static final Integer GRADERINGSDAGER = 120;
    public static final Integer GRADERINGSDAGER2 = 15;
    public static final Date MAKSDATO = new LocalDate(2013, 11, 11).toDate();
    public static final Integer RESTDAGER = 30;
    public static final String RETTTILFEDREKVOTE_KODE = "fkode";
    public static final String RETTTILFEDREKVOTE_TERM = "fedrekvote ja";
    public static final String RETTTILFEDREKVOTE_KODE2 = "fkode2";
    public static final String RETTTILFEDREKVOTE_TERM2 = "fedrekvote tjaa";
    public static final String RETTTILFEDREKVOTE_KODE3 = "fkode3";
    public static final String RETTTILFEDREKVOTE_TERM3 = "fedrekvote nei";
    public static final String RETTTILMODREKVOTE_KODE = "mkode";
    public static final String RETTTILMODREKVOTE_TERM = "modrekvote tja";
    public static final Date TERMINDATO = new LocalDate(2015, 8, 1).toDate();
    public static final Date OMSORGSOVERTAKELSE = new LocalDate(2013, 2, 15).toDate();
    public static final String AVSLAGSAARSAK_KODE = "avk";
    public static final String AVSLAGSAARSAK_TERM = "ugyldig dokument";
    public static final Date MIDLERTIDIGSTANS_DATO = new LocalDate(2014, 4, 4).toDate();
    public static final String SAKSBEHANDLER_IDENT2 = "SAKB002";
    public static final String SAKSBEHANDLER_IDENT3 = "SAKB003";
    public static final String SAKSBEHANDLER_IDENT4 = "SAKB004";
    public static final String SAKSBEHANDLER_IDENT5 = "SAKB005";
    public static final String SAKSBEHANDLER_IDENT6 = "SAKB006";
    public static final String ARBEIDSGIVER_ORGNR3 = "333333333";
    public static final String ARBEIDSGIVER_ORGNR4 = "444444444";
    public static final String ARBEIDSGIVER_ORGNR5 = "555555555";
    public static final String ARBEIDSGIVER_ORGNR6 = "666666666";
    public static final double KOMMENDE_UTBETALING3_DAGSATS = 333;
    public static final double KOMMENDE_UTBETALING4_DAGSATS = 444;
    public static final double KOMMENDE_UTBETALING5_DAGSATS = 555;
    public static final double KOMMENDE_UTBETALING6_DAGSATS = 666;
    public static final double KOMMENDE_UTBETALING3_BRUTTOBELOP = 3000;
    public static final double KOMMENDE_UTBETALING4_BRUTTOBELOP = 4000;
    public static final double KOMMENDE_UTBETALING5_BRUTTOBELOP = 5000;
    public static final double KOMMENDE_UTBETALING6_BRUTTOBELOP = 6000;
    public static final double KOMMENDE_UTBETALING3_GRAD = 33;
    public static final double KOMMENDE_UTBETALING4_GRAD = 44;
    public static final double KOMMENDE_UTBETALING5_GRAD = 55;
    public static final double KOMMENDE_UTBETALING6_GRAD = 66;
    public static final Date PERIODEFRA4 = new LocalDate(2014, 1, 7).toDate();
    public static final Date PERIODETOM4 = new LocalDate(2014, 2, 8).toDate();
    public static final Date PERIODEFRA5 = new LocalDate(2015, 3, 9).toDate();
    public static final Date PERIODETOM5 = new LocalDate(2015, 4, 10).toDate();
    public static final Date PERIODEFRA6 = new LocalDate(2016, 5, 11).toDate();
    public static final Date PERIODETOM6 = new LocalDate(2016, 6, 12).toDate();

    public static FimHentForeldrepengerettighetResponse createFimHentForeldrepengerListeResponse() {
        FimHentForeldrepengerettighetResponse response = new FimHentForeldrepengerettighetResponse();

        response.setForeldrepengerettighet(createforeldrepengerettighetF());
        return response;
    }

    public static FimHentForeldrepengerettighetResponse createFimHentForeldrepengerListeResponseAdopsjon() {
        FimHentForeldrepengerettighetResponse response = new FimHentForeldrepengerettighetResponse();
        response.setForeldrepengerettighet(createforeldrepengerettighetA());
        return response;
    }

    public ForeldrepengerListeResponse createForeldrepengerListeResponse() {
        ForeldrepengerListeResponse response = new ForeldrepengerListeResponse();
        response.setForeldrepengerettighet(createForeldrepengerettighetFoedsel(PERSON_ID));
        return response;
    }

    private Foedsel createForeldrepengerettighetFoedsel(String personID) {
        Foedsel foreldrepengerettighet = new Foedsel();

        foreldrepengerettighet.setForelder(createBruker(personID));
        foreldrepengerettighet.setArbeidsforholdListe(createArbeidsforholdList());
        foreldrepengerettighet.setPeriode(createForeldrepengeperiodeList());
        createForeldrepengerettighetFaktaFoedsel(foreldrepengerettighet);
        foreldrepengerettighet.setRettighetFom(foreldrepengerettighet.getTermin());
        return foreldrepengerettighet;
    }

    private void createForeldrepengerettighetFaktaFoedsel(Foedsel foreldrepengerettighet) {
        foreldrepengerettighet.setAndreForeldersFnr(ANDRE_FORELDER2);
        foreldrepengerettighet.setBarnetsFoedselsdato(LocalDate.fromDateFields(DateUtils.getDate(2013, 4, 5)));
        foreldrepengerettighet.setAntallBarn(Integer.valueOf("1"));
        foreldrepengerettighet.setDekningsgrad(80.0);
        foreldrepengerettighet.setFedrekvoteTom(null);
        foreldrepengerettighet.setForeldrepengetype(new Kodeverkstype("01", "Fødsel"));
        foreldrepengerettighet.setGraderingsdager(12);
        foreldrepengerettighet.setSlutt(LocalDate.fromDateFields(DateUtils.getDate(2013, 11, 30)));
        foreldrepengerettighet.setRestDager(163);
        foreldrepengerettighet.setTermin(LocalDate.fromDateFields(DateUtils.getDate(2013, 4, 1)));
    }

    public static Bruker createBruker(String personID) {
        Bruker bruker = new Bruker();
        bruker.setIdent(personID);
        return bruker;
    }

    private List<Arbeidsforhold> createArbeidsforholdList() {
        List<Arbeidsforhold> arbeidsforholdList = new ArrayList<>();
        Kodeverkstype refusjonstype1 = new Kodeverkstype();
        refusjonstype1.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE);
        refusjonstype1.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM);
        Kodeverkstype refusjonstype2 = new Kodeverkstype();
        refusjonstype2.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE2);
        refusjonstype2.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM2);
        Kodeverkstype inntektperiode = new Kodeverkstype();
        inntektperiode.setKode(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE);
        inntektperiode.setTermnavn(ARBEIDSFORHOLD_INNTEKTPERIODE_TERM);
        Kodeverkstype inntektperiode2 = new Kodeverkstype();
        inntektperiode2.setKode(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE2);
        inntektperiode2.setTermnavn(ARBEIDSFORHOLD_INNTEKTPERIODE_TERM2);
        arbeidsforholdList
                .add(createArbeidsforhold(ARBEIDSFORHOLD_KONTONUMMER, ARBEIDSFORHOLD_NAVN, ARBEIDSFORFOLD_REFUSJON_TOM, ARBEIDSFORFOLD_SYKEPENGER_FOM, refusjonstype1, inntektperiode, ARBEIDSFORHOLD_INNTEKT));
        arbeidsforholdList.add(createArbeidsforhold(ARBEIDSFORHOLD_KONTONUMMER2, ARBEIDSFORHOLD_NAVN2, ARBEIDSFORFOLD_REFUSJON_TOM2, ARBEIDSFORFOLD_SYKEPENGER_FOM2, refusjonstype2, inntektperiode2,
                ARBEIDSFORHOLD_INNTEKT2));

        return arbeidsforholdList;
    }

    private Arbeidsforhold createArbeidsforhold(String kontonummer, String navn, Date refusjonFom, Date sykepengerFom, Kodeverkstype refusjonstype, Kodeverkstype inntektsperiode, Double inntekt) {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        arbeidsforhold.setArbeidsgiverKontonr(kontonummer);
        arbeidsforhold.setArbeidsgiverNavn(navn);
        arbeidsforhold.setRefusjonTom(LocalDate.fromDateFields(refusjonFom));
        arbeidsforhold.setSykepengerFom(LocalDate.fromDateFields(sykepengerFom));
        arbeidsforhold.setInntektsperiode(inntektsperiode);
        arbeidsforhold.setInntektForPerioden(inntekt);
        arbeidsforhold.setRefusjonstype(refusjonstype);


        return arbeidsforhold;
    }

    private List<Foreldrepengeperiode> createForeldrepengeperiodeList() {
        List<Foreldrepengeperiode> foreldrepengeperiodeList = new ArrayList<>();
        foreldrepengeperiodeList.add(createForeldrepengeperiode(IDDATO));
        foreldrepengeperiodeList.add(createForeldrepengeperiode(IDDATO2));

        return foreldrepengeperiodeList;
    }

    private Foreldrepengeperiode createForeldrepengeperiode(LocalDate iddato) {
        Foreldrepengeperiode foreldrepengeperiode = new Foreldrepengeperiode();
        foreldrepengeperiode.setForeldrepengerFom(iddato);

        foreldrepengeperiode.setKommendeUtbetalinger(createKommendeUtbetalingList());
        return foreldrepengeperiode;
    }

    private List<KommendeUtbetaling> createKommendeUtbetalingList() {
        List<KommendeUtbetaling> kommendeUtbetalinger = new ArrayList<>();
        kommendeUtbetalinger.add(createKommenedeUtbetaling(LocalDate.fromDateFields(DateTime.now().minusMonths(4).toDate()), LocalDate.fromDateFields(DateTime.now().plusMonths(3).toDate())));
        kommendeUtbetalinger.add(createKommenedeUtbetaling(LocalDate.fromDateFields(DateTime.now().minusMonths(3).toDate()), LocalDate.fromDateFields(DateTime.now().plusMonths(2).toDate())));
        return kommendeUtbetalinger;
    }

    private KommendeUtbetaling createKommenedeUtbetaling(LocalDate fom, LocalDate tom) {
        KommendeUtbetaling kommendeUtbetaling = new KommendeUtbetaling();
        createUtbetaling(kommendeUtbetaling, fom, tom);
        return kommendeUtbetaling;
    }

    private KommendeUtbetaling createHistoriskUtbetaling(LocalDate fom, LocalDate tom) {
        KommendeUtbetaling kommendeUtbetaling = new KommendeUtbetaling();
        createUtbetaling(kommendeUtbetaling, fom, tom);
        return kommendeUtbetaling;
    }

    private void createUtbetaling(Utbetaling utbetaling, LocalDate fom, LocalDate tom) {
        utbetaling.setVedtak(new Periode(fom, tom));
    }

    public static FimForeldrepengerettighet createforeldrepengerettighetF() {
        FimFoedsel foreldrepengerettighet = new FimFoedsel();
        foreldrepengerettighet.withAndreForelder(new FimPerson().withIdent(ANDREFORELDRESFNR))
                .withArbeidskategori(new FimArbeidskategori().withKode(ARBEIDSKATEGORI_KODE).withTermnavn(ARBEIDSKATEGORI_TERM))
                .withBarnetFoedt(DateUtils.convertDateToXmlGregorianCalendar(BARNETSFOEDSELSDATO_F))
                .withAntallBarn(BigInteger.valueOf(1))
                .withForelder(createFimBruker())
                .withDekningsgrad(BigDecimal.valueOf(DEKNINSGRAD))
                .withFedrekvoteTom(DateUtils.convertDateToXmlGregorianCalendar(FEDREKVOTETOM))
                .withMoedrekvoteTom(DateUtils.convertDateToXmlGregorianCalendar(MODREKVOTETOM))
                .withForeldrepengetype(new FimForeldrepengetype().withKode(FORELDREPENGETYPE_F_KODE).withTermnavn(FORELDREPENGETYPE_F_TERM))
                .withForeldreAvSammeKjoenn(new FimForeldreAvSammeKjoenn().withKode(FORELDREAVSAMMEKJOENN_A_KODE).withTermnavn(FORELDREAVSAMMEKJOENN_A_TERM))
                .withGraderingsdager(BigInteger.valueOf(GRADERINGSDAGER2))
                .withArbeidsforholdListe(createArbeidsforhold())
                .withForeldrepengeperiodeListe(createForeldrepengeperioder())
                .withSlutt(DateUtils.convertDateToXmlGregorianCalendar(MAKSDATO))
                .withRestDager(BigInteger.valueOf(RESTDAGER))
                .withTermin(DateUtils.convertDateToXmlGregorianCalendar(TERMINDATO))
                .withBarnetFoedt(DateUtils.convertDateToXmlGregorianCalendar(BARNETSFOEDSELSDATO_F));

        return foreldrepengerettighet;
    }

    public static FimForeldrepengerettighet createforeldrepengerettighetA() {
        FimAdopsjon foreldrepengerettighet = new FimAdopsjon();
        foreldrepengerettighet.withAndreForelder(new FimPerson().withIdent(ANDREFORELDRESFNR2))
                .withArbeidskategori(new FimArbeidskategori().withKode(ARBEIDSKATEGORI_KODE).withTermnavn(ARBEIDSKATEGORI_TERM))
                .withAntallBarn(BigInteger.valueOf(ANTALLBARN2))
                .withBarnetFoedt(DateUtils.convertDateToXmlGregorianCalendar(BARNETSFOEDSELSDATO_A))
                .withForelder(createFimBruker())
                .withDekningsgrad(BigDecimal.valueOf(DEKNINSGRAD))
                .withFedrekvoteTom(DateUtils.convertDateToXmlGregorianCalendar(FEDREKVOTETOM2))
                .withForeldrepengetype(new FimForeldrepengetype().withKode(FORELDREPENGETYPE_A_KODE).withTermnavn(FORELDREPENGETYPE_A_TERM))
                .withGraderingsdager(BigInteger.valueOf(GRADERINGSDAGER))
                .withArbeidsforholdListe(createArbeidsforholdEmpty())
                .withForeldrepengeperiodeListe(createForeldrepengeperioder2())
                .withSlutt(DateUtils.convertDateToXmlGregorianCalendar(MAKSDATO))
                .withRestDager(BigInteger.valueOf(RESTDAGER))
                .withOmsorgsovertakelse(DateUtils.convertDateToXmlGregorianCalendar(OMSORGSOVERTAKELSE));

        return foreldrepengerettighet;
    }

    public static List<FimForeldrepengeperiode> createForeldrepengeperioder() {
        List<FimForeldrepengeperiode> foreldrepengeperioder = new ArrayList<>();
        foreldrepengeperioder.add(createForeldrepengeperiode());
        foreldrepengeperioder.add(createForeldrepengeperiode2());
        foreldrepengeperioder.add(createForeldrepengeperiode3());

        return foreldrepengeperioder;
    }

    public static List<FimForeldrepengeperiode> createForeldrepengeperioder2() {
        List<FimForeldrepengeperiode> foreldrepengeperioder = new ArrayList<>();
        foreldrepengeperioder.add(new FimForeldrepengeperiode());
        foreldrepengeperioder.add(createForeldrepengeperiode());

        return foreldrepengeperioder;
    }

    public static FimForeldrepengeperiode createForeldrepengeperiode() {
        FimForeldrepengeperiode foreldrepengerperiode = new FimForeldrepengeperiode();
        foreldrepengerperiode.withHarAleneomsorgFar(ALENEOMSORGFAR_FALSE)
                .withHarAleneomsorgMor(ALENEOMSORGMOR_TRUE)
                .withArbeidsprosentMor(BigDecimal.valueOf(ARBEIDSPROSENTMOR))
                .withAvslaatt(DateUtils.convertDateToXmlGregorianCalendar(AVSLAGSDATO))
                .withAvslagsaarsak(new FimAvslagsaarsak().withKode(AVSLAGSAARSAK_KODE).withTermnavn(AVSLAGSAARSAK_TERM))
                .withDisponibelGradering(BigDecimal.valueOf(DISPONIBELGRADERING))
                .withErFedrekvote(FEDREKVOTE)
                .withForskyvelsesaarsak1(new FimForskyvelsesaarsak().withKode(FORSKYVELSESAARSAK_KODE).withTermnavn(FORSKYVELSESAARSAK_TERM))
                .withForskyvet1(FORSKYVELSESPERIODE)
                .withForskyvelsesaarsak2(new FimForskyvelsesaarsak().withKode(FORSKYVELSESAARSAK2_KODE).withTermnavn(FORSKYVELSESAARSAK2_TERM))
                .withForskyvet2(FORSKYVELSESPERIODE2)
                .withForeldrepengerFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO.toDate()))
                .withMidlertidigStanset(DateUtils.convertDateToXmlGregorianCalendar(MIDLERTIDIGSTANS_DATO))
                .withMorSituasjon(new FimMorSituasjon().withKode(MORSSITUASJON_KODE).withTermnavn(MORSSITUASJON_TERM))
                .withRettTilFedrekvote(new FimRettTilFedrekvote().withKode(RETTTILFEDREKVOTE_KODE).withTermnavn(RETTTILFEDREKVOTE_TERM))
                .withRettTilMoedrekvote(new FimRettTilMoedrekvote().withKode(RETTTILMODREKVOTE_KODE).withTermnavn(RETTTILMODREKVOTE_TERM))
                .withStansaarsak(new FimStansaarsak().withKode(STANSAARSAK_KODE_P).withTermnavn(STANSAARSAK_TERM_P))
                .withVedtakListe(createUtbetalinger());

        return foreldrepengerperiode;
    }

    public static FimForeldrepengeperiode createForeldrepengeperiode2() {
        FimForeldrepengeperiode foreldrepengerperiode2 = new FimForeldrepengeperiode();
        foreldrepengerperiode2.withHarAleneomsorgFar(ALENEOMSORGFAR_FALSE)
                .withHarAleneomsorgMor(ALENEOMSORGMOR_TRUE)
                .withArbeidsprosentMor(BigDecimal.valueOf(ARBEIDSPROSENTMOR2))
                .withAvslaatt(DateUtils.convertDateToXmlGregorianCalendar(AVSLAGSDATO))
                .withDisponibelGradering(BigDecimal.valueOf(DISPONIBELGRADERING2))
                .withErFedrekvote(FEDREKVOTE)
                .withForskyvelsesaarsak1(new FimForskyvelsesaarsak().withKode(FORSKYVELSESAARSAK_KODE).withTermnavn(FORSKYVELSESAARSAK_TERM))
                .withForskyvet1(FORSKYVELSESPERIODE)
                .withForeldrepengerFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO2.toDate()))
                .withMorSituasjon(new FimMorSituasjon().withKode(MORSSITUASJON_KODE).withTermnavn(MORSSITUASJON_TERM))
                .withRettTilFedrekvote(new FimRettTilFedrekvote().withKode(RETTTILFEDREKVOTE_KODE2).withTermnavn(RETTTILFEDREKVOTE_TERM2))
                .withRettTilMoedrekvote(new FimRettTilMoedrekvote().withKode(RETTTILMODREKVOTE_KODE).withTermnavn(RETTTILMODREKVOTE_TERM))
                .withStansaarsak(new FimStansaarsak().withKode(STANSAARSAK_KODE_P).withTermnavn(STANSAARSAK_TERM_P))
                .withVedtakListe(createUtbetalinger());

        return foreldrepengerperiode2;
    }

    public static FimForeldrepengeperiode createForeldrepengeperiode3() {
        FimForeldrepengeperiode foreldrepengerperiode3 = new FimForeldrepengeperiode();
        List<FimVedtak> utbetalinger = new ArrayList<>();
        utbetalinger.add(createKommendeUtbetaling2());
        foreldrepengerperiode3.withHarAleneomsorgFar(ALENEOMSORGFAR_TRUE)
                .withHarAleneomsorgMor(ALENEOMSORGMOR_FALSE)
                .withArbeidsprosentMor(BigDecimal.valueOf(ARBEIDSPROSENTMOR3))
                .withAvslaatt(DateUtils.convertDateToXmlGregorianCalendar(AVSLAGSDATO))
                .withErFedrekvote(FEDREKVOTE)
                .withForskyvelsesaarsak1(new FimForskyvelsesaarsak().withKode(FORSKYVELSESAARSAK_KODE).withTermnavn(FORSKYVELSESAARSAK_TERM))
                .withForskyvet1(FORSKYVELSESPERIODE)
                .withForeldrepengerFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO.toDate()))
                .withMorSituasjon(new FimMorSituasjon().withKode(MORSSITUASJON_KODE).withTermnavn(MORSSITUASJON_TERM))
                .withRettTilFedrekvote(new FimRettTilFedrekvote().withKode(RETTTILFEDREKVOTE_KODE3).withTermnavn(RETTTILFEDREKVOTE_TERM3))
                .withStansaarsak(new FimStansaarsak().withKode(STANSAARSAK_KODE_P).withTermnavn(STANSAARSAK_TERM_P))
                .withVedtakListe(utbetalinger);

        return foreldrepengerperiode3;
    }

    private static List<FimVedtak> createUtbetalinger() {
        List<FimVedtak> utbetalinger = new ArrayList<>();
        utbetalinger.add(createKommenedeUtbetaling());
        utbetalinger.add(createKommendeUtbetaling2());
        utbetalinger.add(createKommendeUtbetaling3());
        utbetalinger.add(createKommendeUtbetaling4());
        utbetalinger.add(createKommendeUtbetaling5());
        utbetalinger.add(createKommendeUtbetaling6());
        return utbetalinger;
    }

    private static FimKommendeVedtak createHistoriskUtbetaling() {
        FimKommendeVedtak kommendeUtbetaling = new FimKommendeVedtak();
        kommendeUtbetaling.setOppgjoerstype(new FimOppgjoerstype().withKode(KOMMENDE_UTBETALING1_OPPGJORSTYPE_KODE).withTermnavn(KOMMENDE_UTBETALING1_OPPGJORSTYPE_TERM));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING1_UTBETALINGSGRAD));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA, PERIODETOM));
        return kommendeUtbetaling;
    }

    private static FimKommendeVedtak createHistoriskUtbetaling2() {
        FimKommendeVedtak kommendeUtbetaling = new FimKommendeVedtak();
        kommendeUtbetaling.setOppgjoerstype(new FimOppgjoerstype().withKode(KOMMENDE_UTBETALING2_OPPGJORSTYPE_KODE).withTermnavn(KOMMENDE_UTBETALING2_OPPGJORSTYPE_TERM));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING2_UTBETALINGSGRAD));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA, PERIODETOM));
        return kommendeUtbetaling;
    }

    private static FimHistoriskVedtak createKommenedeUtbetaling() {
        FimHistoriskVedtak kommendeUtbetaling = new FimHistoriskVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING_DAGSATS1));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA, PERIODETOM));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalt(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING1_UTBETALINGSGRAD));
        kommendeUtbetaling.setArbeidsgiverNavn(ARBEIDSFORHOLD_NAVN);
        kommendeUtbetaling.setArbeidsgiverKontonr(ARBEIDSFORHOLD_KONTONUMMER);
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT);

        return kommendeUtbetaling;
    }

    private static FimHistoriskVedtak createKommendeUtbetaling2() {
        FimHistoriskVedtak kommendeUtbetaling = new FimHistoriskVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING2_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA2, PERIODETOM2));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING2_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalt(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA2));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING2_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR2);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT2);

        return kommendeUtbetaling;
    }


    private static FimVedtak createKommendeUtbetaling3() {
        FimHistoriskVedtak kommendeUtbetaling = new FimHistoriskVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING3_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA3, PERIODETOM3));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING3_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalt(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA3));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING3_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR3);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT3);

        return kommendeUtbetaling;
    }
    private static FimVedtak createKommendeUtbetaling4() {
        FimHistoriskVedtak kommendeUtbetaling = new FimHistoriskVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING4_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA4, PERIODETOM4));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING4_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalt(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA4));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING4_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR4);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT4);

        return kommendeUtbetaling;
    }
    private static FimVedtak createKommendeUtbetaling5() {
        FimHistoriskVedtak kommendeUtbetaling = new FimHistoriskVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING5_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA5, PERIODETOM5));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING5_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalt(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA5));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING5_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR5);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT5);

        return kommendeUtbetaling;
    }
    private static FimVedtak createKommendeUtbetaling6() {
        FimHistoriskVedtak kommendeUtbetaling = new FimHistoriskVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING6_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA6, PERIODETOM6));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING6_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalt(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA6));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING6_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR6);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT6);

        return kommendeUtbetaling;
    }

    private static FimPerson createFimBruker() {
        FimPerson fimBruker = new FimPerson();
        fimBruker.setIdent(PERSON_ID);
        return fimBruker;
    }

    private static FimPeriode createFimPeriode(Date fom, Date tom) {
        FimPeriode fimPeriode = new FimPeriode();
        fimPeriode.setFom(DateUtils.convertDateToXmlGregorianCalendar(fom));
        fimPeriode.setTom(DateUtils.convertDateToXmlGregorianCalendar(tom));
        return fimPeriode;
    }

    private static List<FimArbeidsforhold> createArbeidsforholdEmpty() {
        return new ArrayList<>();
    }

    private static List<FimArbeidsforhold> createArbeidsforhold() {
        List<FimArbeidsforhold> arbeidsforhold = new ArrayList<>();
        FimArbeidsforhold fimArbeidsforhold1 = new FimArbeidsforhold();
        fimArbeidsforhold1.setArbeidsgiverKontonr(ARBEIDSFORHOLD_KONTONUMMER);
        fimArbeidsforhold1.setArbeidsgiverNavn(ARBEIDSFORHOLD_NAVN);
        FimRefusjonstype refusjonstype1 = new FimRefusjonstype();
        refusjonstype1.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE);
        refusjonstype1.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM);
        fimArbeidsforhold1.setRefusjonstype(refusjonstype1);
        fimArbeidsforhold1.setRefusjonTom(DateUtils.convertDateToXmlGregorianCalendar(PERIODETOM));
        FimInntektsperiode inntektperiode = new FimInntektsperiode();
        inntektperiode.setKode(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE);
        inntektperiode.setTermnavn(ARBEIDSFORHOLD_INNTEKTPERIODE_TERM);
        fimArbeidsforhold1.setInntektsperiode(inntektperiode);
        fimArbeidsforhold1.setInntektForPerioden(BigDecimal.valueOf(ARBEIDSFORHOLD_INNTEKT));

        FimArbeidsforhold fimArbeidsforhold2 = new FimArbeidsforhold();
        fimArbeidsforhold2.setArbeidsgiverKontonr(ARBEIDSFORHOLD_KONTONUMMER2);
        fimArbeidsforhold2.setArbeidsgiverNavn(ARBEIDSFORHOLD_NAVN2);
        FimRefusjonstype refusjonstype2 = new FimRefusjonstype();
        refusjonstype2.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE2);
        refusjonstype2.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM2);
        fimArbeidsforhold2.setRefusjonstype(refusjonstype2);
        fimArbeidsforhold2.setRefusjonTom(DateUtils.convertDateToXmlGregorianCalendar(PERIODETOM));
        FimInntektsperiode inntektperiode2 = new FimInntektsperiode();
        inntektperiode2.setKode(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE2);
        inntektperiode2.setTermnavn(ARBEIDSFORHOLD_INNTEKTPERIODE_TERM2);
        fimArbeidsforhold2.setInntektsperiode(inntektperiode2);
        fimArbeidsforhold2.setInntektForPerioden(BigDecimal.valueOf(ARBEIDSFORHOLD_INNTEKT2));

        arbeidsforhold.add(fimArbeidsforhold1);
        arbeidsforhold.add(fimArbeidsforhold2);
        return arbeidsforhold;
    }
}
