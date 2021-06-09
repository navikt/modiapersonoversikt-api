package no.nav.sykmeldingsperioder.consumer.sykepenger;

import no.nav.kjerneinfo.common.utils.DateUtils;
import no.nav.sykmeldingsperioder.consumer.common.CommonMockFactory;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Denne klassen lager et responsobjekt som kan benyttes som svar fra den eksterne tjenesten for mockformål.
 * <p>
 * Siden hver sykemelingsperiode inneholder så mye data er hver periode ganske lik, konstantene som er listet opp under
 * gjelder alle for det første Sykemeldingsperiode-objektet i listen, for å se hvordan dataene er endret i de andre
 * objektene se metodene med navn createSykemeldingsperiodeN.
 */
public class SykepengerMockFactory {

    public static final String ARBEIDSFORHOLD_KONTONUMMER = "1234.12.7845";
    public static final String ARBEIDSFORHOLD_NAVN = "Google Inc.";
    public static final String ARBEIDSFORHOLD2_NAVN = "Microsoft";
    public static final Date ARBEIDSFORFOLD_REFUSJON_TOM = new LocalDate(2013, 8, 1).toDate();
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_KODE = "Refusjonstype";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_TERM = "Generell refusjon";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_KODE2 = "Refusjonstype2";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_TERM2 = "Generell refusjon2";
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_KODE = "inntektsperiode";
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_TERM = "inntektsperiode 1";
    public static final Date ARBEIDSFORFOLD_SYKEPENGER_FOM = new LocalDate(2013, 6, 4).toDate();
    public static final String ARBEIDSFORHOLD_KONTONUMMER2 = "9999.12.7845";
    public static final String ARBEIDSFORHOLD2_KONTONUMMER = "11111.11.99999";
    public static final String ARBEIDSFORHOLD_NAVN2 = "Hydro";
    public static final Date ARBEIDSFORFOLD_REFUSJON_TOM2 = new LocalDate(2013, 2, 1).toDate();
    public static final Date ARBEIDSFORFOLD_SYKEPENGER_FOM2 = new LocalDate(2012, 8, 1).toDate();
    public static final Double ARBEIDSFORHOLD_INNTEKT = 3652.20;
    public static final String ARBEIDSKATEGORI_TERM_ARBEIDSLEDIG = "Arbeidsledig";
    public static final String ARBEIDSKATEGORI_KODE = "99";
    public static final Date FERIEPERIODE_FOM = new LocalDate(2013, 12, 1).toDate();
    public static final Date FERIEPERIODE_TOM = new LocalDate(2013, 12, 21).toDate();
    public static final Date FERIEPERIODE2_FOM = new LocalDate(2016, 10, 1).toDate();
    public static final Date FERIEPERIODE2_TOM = new LocalDate(2016, 12, 16).toDate();
    public static final int FORBRUKTE_DAGER = 30;
    public static final boolean FORSIKRING_GYLDIG = true;
    public static final String FORSIKRINGSORDNING = "Helseforsikring";
    public static final Date FORSIKRING_FOM = new LocalDate(2010, 8, 1).toDate();
    public static final Date FORSIKRING_TOM = new LocalDate(2013, 8, 1).toDate();
    public static final double FORSIKRING_PREMIEGRUNNLAG = 100000.00;
    public static final Date IDDATO = new LocalDate(2013, 12, 12).toDate();
    public static final Date IDDATO2 = new LocalDate(2002, 1, 2).toDate();
    public static final Date IDDATO3 = new LocalDate(2010, 5, 25).toDate();
    public static final Date IDDATO4 = new LocalDate(2012, 5, 25).toDate();
    public static final Date IDDATO5 = new LocalDate(2014, 5, 25).toDate();
    public static final Date IDDATO6 = new LocalDate(2016, 5, 25).toDate();
    public static final Date MAKSDATO = new LocalDate(2014, 2, 1).toDate();
    public static final String STANSAARSAK_KODE = "Stansaarsak";
    public static final String STANSARRSAK_TERM = "Helse";
    public static final String STANSAARSAK_MANGLENDE_AKTIVITET_KODE = "MA";
    public static final String STANSARRSAK_MANGLENDE_AKTIVITET_TERM = "Manglende Aktivitet";
    public static final String STANSAARSAK_NOE_ANNET_KODE = "NA";
    public static final String STANSARRSAK_NOE_ANNET_TERM = "Noe Annet";
    public static final String PERSON_ID = "12345612345";
    public static final String UNNTAK_AKTIVITET_KODE = "UnntakAktivitet";
    public static final String UNNTAK_AKTIVITET_TERM = "Unntak";
    public static final Date MIDLERTIDIGSTANSDATO = new LocalDate(2013, 7, 29).toDate();
    public static final double HISTORTISK_UTBETALING_BRUTTOBELOP = 220.55;
    public static final double HISTORTISK_UTBETALING_DAGSATS = 120.89;
    public static final Date HISTORTISK_UTBETALING_DATO = new LocalDate(2013, 7, 22).toDate();
    public static final double HISTORTISK_UTBETALING2_BRUTTOBELOP = 22000.55;
    public static final double HISTORTISK_UTBETALING2_DAGSATS = 1000.00;
    public static final Date HISTORTISK_UTBETALING2_PERIODE_FOM = new LocalDate(2013, 4, 30).toDate();
    public static final Date HISTORTISK_UTBETALING2_PERIODE_TOM = new LocalDate(2013, 5, 20).toDate();
    public static final Date HISTORTISK_UTBETALING2_DATO = new LocalDate(2012, 12, 30).toDate();
    public static final double HISTORTISK_UTBETALING2_GRAD = 65.0;

    public static final String UTBETALING_PA_VENT_OPPGJORSTYPE_KODE = "41";
    public static final String UTBETALING_PA_VENT_OPPGJORSTYPE_TERM = "OPGJ999";
    public static final double UTBETALING_PA_VENT_UTBETALINGSGRAD = 22;
    public static final Date UTBETALING_PA_VENT_PERIODE_FOM = new LocalDate(2016, 5, 1).toDate();
    public static final Date UTBETALING_PA_VENT_PERIODE_TOM = new LocalDate(2016, 6, 1).toDate();
    public static final Date UTBETALING_PA_VENT_FERIE_FOM = new LocalDate(2016, 5, 2).toDate();
    public static final Date UTBETALING_PA_VENT_FERIE_TOM = new LocalDate(2016, 6, 2).toDate();

    public static final Date UTBETALING_PA_VENT_PERIODE_FOM2 = new LocalDate(2013, 5, 1).toDate();
    public static final Date UTBETALING_PA_VENT_PERIODE_TOM2 = new LocalDate(2013, 6, 1).toDate();
    public static final Date UTBETALING_PA_VENT_FERIE_FOM2 = new LocalDate(2013, 5, 20).toDate();
    public static final Date UTBETALING_PA_VENT_FERIE_TOM2 = new LocalDate(2013, 6, 2).toDate();

    public static final double KOMMENDE_UTBETALING1_UTBETALINGSGRAD = 50.0;
    public static final Date KOMMENDE_UTBETALING1_PERIODE_FOM = new LocalDate(2013, 2, 6).toDate();
    public static final Date KOMMENDE_UTBETALING1_PERIODE_TOM = new LocalDate(2013, 5, 6).toDate();
    public static final String KOMMENDE_UTBETALING1_TYPE_KODE = "UT";
    public static final String KOMMENDE_UTBETALING1_TYPE_TERMNAVN = "Utbetaling";

    public static final String KOMMENDE_UTBETALING2_OPPGJORSTYPE_KODE = "61";
    public static final String KOMMENDE_UTBETALING2_OPPGJORSTYPE_TERM = "OPGJ61";
    public static final double KOMMENDE_UTBETALING2_UTBETALINGSGRAD = 70.0;
    public static final Date KOMMENDE_UTBETALING2_PERIODE_FOM = new LocalDate(2013, 3, 6).toDate();
    public static final Date KOMMENDE_UTBETALING2_PERIODE_TOM = new LocalDate(2013, 7, 6).toDate();
    public static final double KOMMENDE_UTBETALING3_UTBETALINGSGRAD = 70.0;
    public static final Date KOMMENDE_UTBETALING3_PERIODE_FOM = new LocalDate(2013, 4, 6).toDate();
    public static final Date KOMMENDE_UTBETALING3_PERIODE_TOM = new LocalDate(2013, 5, 6).toDate();
    public static final String SYKMELDING1_SYKMELDER = "Dr. Bjarne Hansensen";
    public static final Date SYKMELDING1_BEHANDLINGSDATO = new LocalDate(2013, 2, 8).toDate();
    public static final Date SYKMELDING1_PERIODE_FOM = new LocalDate(2013, 4, 8).toDate();
    public static final Date SYKMELDING1_PERIODE_TOM = new LocalDate(2013, 11, 8).toDate();
    public static final Date SYKMELDING1_YRKESSKADE_SKADET_DATO = new LocalDate(2013, 8, 8).toDate();
    public static final Date SYKMEDLING1_YRKESSKADE_VEDTAKS_DATO = new LocalDate(2013, 6, 8).toDate();
    public static final String SYKMEDLING1_YRKESSKADE_SKADEART_KODE = "Skadeart";
    public static final String SYKMEDLING1_YRKESSKADE_SKADEART_TERM = "Hodeskade";
    public static final FimsykYrkesskade SYKMELDING1_YRKESSKADE = createYrkesskade();
    public static final double SYKMELDING1_GRADERING1_GRAD = 70.0;
    public static final Date SYKMELDING1_GRADERING1_PERIODE_FOM = new LocalDate(2013, 3, 8).toDate();
    public static final Date SYKMELDING1_GRADERING1_PERIODE_TOM = new LocalDate(2013, 4, 8).toDate();
    public static final double SYKMELDING1_GRADERING2_GRAD = 80.0;
    public static final Date SYKMELDING1_GRADERING2_PERIODE_FOM = new LocalDate(2013, 7, 20).toDate();
    public static final Date SYKMELDING1_GRADERING2_PERIODE_TOM = new LocalDate(2013, 8, 8).toDate();
    public static final double SYKMELDING1_GRADERING3_GRAD = 90.0;
    public static final Date SYKMELDING1_GRADERING3_PERIODE_FOM = new LocalDate(2013, 8, 9).toDate();
    public static final Date SYKMELDING1_GRADERING3_PERIODE_TOM = new LocalDate(2013, 11, 15).toDate();
    public static final String SYKMELDING2_SYKMELDER = "Dr. Herman Hermansen";
    public static final Date SYKMELDING2_BEHANDLINGSDATO = new LocalDate(2012, 4, 8).toDate();
    public static final Date SYKMELDING2_PERIODE_FOM = new LocalDate(2012, 5, 8).toDate();
    public static final Date SYKMELDING2_PERIODE_TOM = new LocalDate(2013, 5, 18).toDate();
    public static final FimsykYrkesskade SYKMELDING2_YRKESSKADE = null;
    public static final double SYKMELDING2_GRADERING1_GRAD = 50.0;
    public static final Date SYKMELDING2_GRADERING1_PERIODE_FOM = new LocalDate(2012, 6, 8).toDate();
    public static final Date SYKMELDING2_GRADERING1_PERIODE_TOM = new LocalDate(2012, 8, 14).toDate();
    public static final String SYKMELDING3_SYKMELDER = "Dr. Kalle Hermansen";
    public static final Date SYKMELDING3_BEHANDLINGSDATO = new LocalDate(2011, 6, 9).toDate();
    public static final Date SYKMELDING3_PERIODE_FOM = new LocalDate(2011, 6, 11).toDate();
    public static final Date SYKMELDING3_PERIODE_TOM = new LocalDate(2012, 6, 18).toDate();
    public static final FimsykYrkesskade SYKMELDING3_YRKESSKADE = null;
    public static final boolean ARBEIDSGIVER_PERIODE = true;
    public static final String ARBEIDSFORHOLD_ORGNR = "1234512345";
    public static final String KOMMENDE_UTBETALING_SAKSBEHANLDER = "SAKB001";
    public static final String ARBEIDSFORHOLD2_ORGNR = "9912135555";
    public static final String KOMMENDE_UTBETALING2_SAKSBEHANLDER = "SAKB002";
    public static final Date SANKSJON_FOM = new LocalDate(2002, 6, 11).toDate();
    public static final Date SANKSJON_TOM = new LocalDate(2002, 9, 11).toDate();

    public static final Date SANKSJON2_FOM = new LocalDate(2016, 5, 11).toDate();
    public static final Date SANKSJON2_TOM = null;

    public static final Date SANKSJON3_FOM = new LocalDate(2016, 7, 11).toDate();
    public static final Date SANKSJON3_TOM = new LocalDate(2016, 9, 11).toDate();

    public static FimHentSykepengerListeResponse createFimHentSykepengerResponse() {
        FimHentSykepengerListeResponse response = new FimHentSykepengerListeResponse();
        List<FimsykSykmeldingsperiode> sykmeldingsperiodeListe = response.getSykmeldingsperiodeListe();
        sykmeldingsperiodeListe.add(createSykemeldingsperiode());
        sykmeldingsperiodeListe.add(createSykemeldingsperiode2());
        sykmeldingsperiodeListe.add(createSykemeldingsperiode3());

        return response;
    }

    public static FimHentSykepengerListeResponse createFimHentSykepengerResponseManglerSykmeldingForPerioden() {
        FimHentSykepengerListeResponse response = new FimHentSykepengerListeResponse();
        List<FimsykSykmeldingsperiode> sykmeldingsperdiodeListe = response.getSykmeldingsperiodeListe();
        sykmeldingsperdiodeListe.add(createSykmeldingsperiodeMedManglendeSykmelding());
        return response;
    }

    public static FimHentSykepengerListeResponse createFimHentSykepengerResponseMedFerie() {
        FimHentSykepengerListeResponse response = new FimHentSykepengerListeResponse();
        List<FimsykSykmeldingsperiode> sykmeldingsperdiodeListe = response.getSykmeldingsperiodeListe();
        sykmeldingsperdiodeListe.add(createSykemeldingsperiodeMedFerie());
        return response;
    }
    public static FimHentSykepengerListeResponse createFimHentSykepengerResponseMedFerieOgSykmeldingMangler() {
        FimHentSykepengerListeResponse response = new FimHentSykepengerListeResponse();
        List<FimsykSykmeldingsperiode> sykmeldingsperdiodeListe = response.getSykmeldingsperiodeListe();
        sykmeldingsperdiodeListe.add(createSykemeldingsperiodeMedFerieOgSykmeldingMangler());
        return response;
    }

    public static FimHentSykepengerListeResponse createFimHentSykepengerResponseMedSanksjon() {
        FimHentSykepengerListeResponse response = new FimHentSykepengerListeResponse();
        List<FimsykSykmeldingsperiode> sykmeldingsperdiodeListe = response.getSykmeldingsperiodeListe();
        sykmeldingsperdiodeListe.add(createSykemeldingsperiodeMedSanksjon());
        return response;
    }

    public static FimsykSykmeldingsperiode createSykemeldingsperiode() {
        FimsykSykmeldingsperiode sykmeldingsperiode = new FimsykSykmeldingsperiode();
        FimsykRefusjonstype refusjonstype = new FimsykRefusjonstype();
        refusjonstype.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE);
        refusjonstype.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM);
        FimsykInntektsperiode inntektperiode = new FimsykInntektsperiode();
        inntektperiode.setKode(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE);
        inntektperiode.setTermnavn(ARBEIDSFORHOLD_INNTEKTPERIODE_TERM);
        sykmeldingsperiode
                .withArbeidsforholdListe(CommonMockFactory.createArbeidsforholdSykepenger(ARBEIDSFORHOLD_KONTONUMMER, ARBEIDSFORHOLD_NAVN,
                        LocalDate.fromDateFields(ARBEIDSFORFOLD_REFUSJON_TOM), refusjonstype,
                        LocalDate.fromDateFields(ARBEIDSFORFOLD_SYKEPENGER_FOM), ARBEIDSFORHOLD_INNTEKT, inntektperiode))
                .withErArbeidsgiverperiode(ARBEIDSGIVER_PERIODE)
                .withArbeidskategori(createArbeidskategori())
                .withFerie1(createFimPeriode(FERIEPERIODE_FOM, FERIEPERIODE_TOM))
                .withFerie2(createFimPeriode(FERIEPERIODE2_FOM, FERIEPERIODE2_TOM))
                .withForbrukteDager(BigInteger.valueOf(FORBRUKTE_DAGER))
                .withGjeldendeForsikring(createForsikring())
                .withSykmeldtFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO))
                .withSlutt(DateUtils.convertDateToXmlGregorianCalendar(MAKSDATO))
                .withMidlertidigStanset(DateUtils.convertDateToXmlGregorianCalendar(MIDLERTIDIGSTANSDATO))
                .withSanksjon(createFimPeriode(SANKSJON_FOM, SANKSJON_TOM))
                .withStansaarsak(new FimsykStansaarsak().withKode(STANSAARSAK_KODE).withTermnavn(STANSARRSAK_TERM))
                .withSykmeldingListe(createSykmeldinger())
                .withSykmeldt(createBruker())
                .withUnntakAktivitet(new FimsykUnntakAktivitet().withKode(UNNTAK_AKTIVITET_KODE).withTermnavn(UNNTAK_AKTIVITET_TERM))
                .withVedtakListe(createUtbetalinger());
        return sykmeldingsperiode;
    }

    /**
     * Dette har ingen forsikring (for å teste at disse feltene skjules), men flere arbeidsgivere
     */
    public static FimsykSykmeldingsperiode createSykemeldingsperiode2() {
        FimsykSykmeldingsperiode sykmeldingsperiode = new FimsykSykmeldingsperiode();
        FimsykRefusjonstype refusjonstype = new FimsykRefusjonstype();
        refusjonstype.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE);
        refusjonstype.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM);
        FimsykRefusjonstype refusjonstype2 = new FimsykRefusjonstype();
        refusjonstype.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE2);
        refusjonstype.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM2);
        FimsykInntektsperiode inntektperiode = new FimsykInntektsperiode();
        inntektperiode.setKode(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE);
        inntektperiode.setTermnavn(ARBEIDSFORHOLD_INNTEKTPERIODE_TERM);
        sykmeldingsperiode
                .withArbeidsforholdListe(
                        CommonMockFactory.createArbeidsforholdSykepenger(ARBEIDSFORHOLD_KONTONUMMER2, ARBEIDSFORHOLD_NAVN2, LocalDate.fromDateFields(ARBEIDSFORFOLD_REFUSJON_TOM2), refusjonstype,
                                LocalDate.fromDateFields(ARBEIDSFORFOLD_SYKEPENGER_FOM2), ARBEIDSFORHOLD_INNTEKT, inntektperiode),
                        CommonMockFactory.createArbeidsforholdSykepenger(ARBEIDSFORHOLD_KONTONUMMER, ARBEIDSFORHOLD_NAVN, LocalDate.fromDateFields(ARBEIDSFORFOLD_REFUSJON_TOM), refusjonstype2,
                                LocalDate.fromDateFields(ARBEIDSFORFOLD_SYKEPENGER_FOM), ARBEIDSFORHOLD_INNTEKT, inntektperiode))
                .withErArbeidsgiverperiode(ARBEIDSGIVER_PERIODE)
                .withFerie1(createFimPeriode(FERIEPERIODE_FOM, FERIEPERIODE_TOM))
                .withForbrukteDager(BigInteger.valueOf(20))
                .withSykmeldtFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO2))
                .withSlutt(DateUtils.convertDateToXmlGregorianCalendar(MAKSDATO))
                .withStansaarsak(new FimsykStansaarsak().withKode(STANSAARSAK_MANGLENDE_AKTIVITET_KODE).withTermnavn(STANSARRSAK_MANGLENDE_AKTIVITET_TERM))
                .withSykmeldingListe(createSykmeldinger())
                .withSykmeldt(createBruker())
                .withUnntakAktivitet(new FimsykUnntakAktivitet().withKode(UNNTAK_AKTIVITET_KODE).withTermnavn(UNNTAK_AKTIVITET_TERM))
                .withVedtakListe(createUtbetalinger());
        return sykmeldingsperiode;
    }

    public static FimsykSykmeldingsperiode createSykemeldingsperiode3() {
        FimsykSykmeldingsperiode sykmeldingsperiode = new FimsykSykmeldingsperiode();
        sykmeldingsperiode.withSykmeldtFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO3))
                .withStansaarsak(new FimsykStansaarsak().withKode(STANSAARSAK_NOE_ANNET_KODE).withTermnavn(STANSARRSAK_NOE_ANNET_TERM))
                .withSykmeldingListe(createSykmeldinger())
                .withSykmeldt(createBruker())
                .withVedtakListe(createUtbetalinger());
        return sykmeldingsperiode;
    }

    private static FimsykSykmeldingsperiode createSykemeldingsperiodeMedFerieOgSykmeldingMangler() {
        FimsykSykmeldingsperiode sykmeldingsperiode = new FimsykSykmeldingsperiode();
        sykmeldingsperiode.withSykmeldtFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO4))
                .withSykmeldingListe(createSykmeldinger())
                .withSykmeldt(createBruker())
                .withVedtakListe(createUtbetalinger())
                .withFerie1(createFimPeriode(UTBETALING_PA_VENT_FERIE_FOM, UTBETALING_PA_VENT_FERIE_TOM));
        return sykmeldingsperiode;
    }

    private static FimsykSykmeldingsperiode createSykemeldingsperiodeMedFerie() {
        FimsykSykmeldingsperiode sykmeldingsperiode = new FimsykSykmeldingsperiode();
        sykmeldingsperiode.withSykmeldtFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO4))
                .withSykmeldingListe(createSykmeldinger())
                .withSykmeldt(createBruker())
                .withVedtakListe(createUtbetalinger(UTBETALING_PA_VENT_PERIODE_FOM2, UTBETALING_PA_VENT_PERIODE_TOM2))
                .withFerie1(createFimPeriode(UTBETALING_PA_VENT_FERIE_FOM2, UTBETALING_PA_VENT_FERIE_TOM2));
        return sykmeldingsperiode;
    }

    private static FimsykSykmeldingsperiode createSykemeldingsperiodeMedSanksjon() {
        FimsykSykmeldingsperiode sykmeldingsperiode = new FimsykSykmeldingsperiode();
        sykmeldingsperiode.withSykmeldtFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO5))
                .withSykmeldingListe(createSykmeldinger())
                .withSykmeldt(createBruker())
                .withVedtakListe(createUtbetalinger());

        FimsykPeriode fimPeriode = new FimsykPeriode();
        fimPeriode.setFom(DateUtils.convertDateToXmlGregorianCalendar(SANKSJON2_FOM));
        fimPeriode.setTom(null);

        sykmeldingsperiode.withSanksjon(fimPeriode);
        return sykmeldingsperiode;
    }

    private static FimsykSykmeldingsperiode createSykmeldingsperiodeMedManglendeSykmelding() {
        FimsykSykmeldingsperiode sykmeldingsperiode = new FimsykSykmeldingsperiode();
        sykmeldingsperiode.withSykmeldtFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATO4))
                .withSykmeldingListe(createSykmeldinger())
                .withSykmeldt(createBruker())
                .withVedtakListe(createUtbetalinger());
        return sykmeldingsperiode;
    }

    private static List<FimsykVedtak> createUtbetalinger() {
        return createUtbetalinger(UTBETALING_PA_VENT_PERIODE_FOM, UTBETALING_PA_VENT_PERIODE_TOM);
    }

    private static List<FimsykVedtak> createUtbetalinger(Date utbetalingPaVentPeriodeFOM, Date utbetalingPaVentPeriodeTOM) {
        List<FimsykVedtak> utbetalinger = new ArrayList<>();
        utbetalinger.add(createKommendeUtbetaling());
        utbetalinger.add(createAnnenKommendeUtbetaling());
        utbetalinger.add(createAnnenKommendeUtbetaling());

        utbetalinger.add(createUtbetalingPaaVent(utbetalingPaVentPeriodeFOM, utbetalingPaVentPeriodeTOM));
        return utbetalinger;
    }


    private static FimsykKommendeVedtak createUtbetalingPaaVent(Date utbetalingPaVentPeriodeFOM, Date utbetalingPaVentPeriodeTOM) {
        FimsykKommendeVedtak utbetalingPaaVent = new FimsykKommendeVedtak();

        utbetalingPaaVent.setOppgjoerstype(new FimsykOppgjoerstype().withKode(UTBETALING_PA_VENT_OPPGJORSTYPE_KODE).withTermnavn(UTBETALING_PA_VENT_OPPGJORSTYPE_TERM));
        utbetalingPaaVent.setUtbetalingsgrad(BigDecimal.valueOf(UTBETALING_PA_VENT_UTBETALINGSGRAD));
        utbetalingPaaVent.setVedtak(createFimPeriode(utbetalingPaVentPeriodeFOM,
                utbetalingPaVentPeriodeTOM));
        return utbetalingPaaVent;

    }

    private static FimsykHistoriskVedtak createKommendeUtbetaling() {
        FimsykHistoriskVedtak kommendeUtbetaling = new FimsykHistoriskVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(HISTORTISK_UTBETALING_DAGSATS));
        kommendeUtbetaling.setArbeidsgiverNavn(ARBEIDSFORHOLD_NAVN);
        kommendeUtbetaling.setArbeidsgiverKontonr(ARBEIDSFORHOLD_KONTONUMMER);
        kommendeUtbetaling.setVedtak(createFimPeriode(KOMMENDE_UTBETALING1_PERIODE_FOM,
                KOMMENDE_UTBETALING1_PERIODE_TOM));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(HISTORTISK_UTBETALING_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalt(DateUtils.convertDateToXmlGregorianCalendar(HISTORTISK_UTBETALING_DATO));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING1_UTBETALINGSGRAD));

        kommendeUtbetaling.setPeriodetype(new FimsykPeriodetype().withKode(KOMMENDE_UTBETALING1_TYPE_KODE).withTermnavn(KOMMENDE_UTBETALING1_TYPE_TERMNAVN));

        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSFORHOLD_ORGNR);
        kommendeUtbetaling.setSaksbehandler(KOMMENDE_UTBETALING_SAKSBEHANLDER);

        return kommendeUtbetaling;
    }

    private static FimsykHistoriskVedtak createAnnenKommendeUtbetaling() {
        FimsykHistoriskVedtak kommendeUtbetaling = new FimsykHistoriskVedtak();

        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(HISTORTISK_UTBETALING2_DAGSATS));
        kommendeUtbetaling.setArbeidsgiverNavn(ARBEIDSFORHOLD2_NAVN);
        kommendeUtbetaling.setArbeidsgiverKontonr(ARBEIDSFORHOLD2_KONTONUMMER);

        kommendeUtbetaling.setVedtak(createFimPeriode(HISTORTISK_UTBETALING2_PERIODE_FOM,
                HISTORTISK_UTBETALING2_PERIODE_TOM));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(HISTORTISK_UTBETALING2_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalt(DateUtils.convertDateToXmlGregorianCalendar(HISTORTISK_UTBETALING2_DATO));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(HISTORTISK_UTBETALING2_GRAD));

        kommendeUtbetaling.setPeriodetype(new FimsykPeriodetype().withKode("RE").withTermnavn("Reduksjon"));

        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSFORHOLD2_ORGNR);
        kommendeUtbetaling.setSaksbehandler(KOMMENDE_UTBETALING2_SAKSBEHANLDER);

        return kommendeUtbetaling;
    }

    private static FimsykBruker createBruker() {
        FimsykBruker fimBruker = new FimsykBruker();
        fimBruker.setIdent(PERSON_ID);
        return fimBruker;
    }

    private static List<FimsykSykmelding> createSykmeldinger() {
        List<FimsykSykmelding> sykmeldinger = new ArrayList<>();

        FimsykSykmelding sykmelding = new FimsykSykmelding();
        sykmelding.setSykmelder(SYKMELDING1_SYKMELDER);
        sykmelding.setBehandlet(DateUtils.convertDateToXmlGregorianCalendar(SYKMELDING1_BEHANDLINGSDATO));
        sykmelding.setGjelderYrkesskade(SYKMELDING1_YRKESSKADE);
        sykmelding.setSykmeldt(createFimPeriode(SYKMELDING1_PERIODE_FOM, SYKMELDING1_PERIODE_TOM));

        FimsykGradering gradering = new FimsykGradering();
        gradering.setSykmeldingsgrad(BigDecimal.valueOf(SYKMELDING1_GRADERING1_GRAD));
        gradering.setGradert(createFimPeriode(SYKMELDING1_GRADERING1_PERIODE_FOM, SYKMELDING1_GRADERING1_PERIODE_TOM));
        sykmelding.getGradAvSykmeldingListe().add(gradering);

        FimsykGradering gradering3 = new FimsykGradering();
        gradering3.setSykmeldingsgrad(BigDecimal.valueOf(SYKMELDING1_GRADERING3_GRAD));
        gradering3.setGradert(createFimPeriode(SYKMELDING1_GRADERING3_PERIODE_FOM, SYKMELDING1_GRADERING3_PERIODE_TOM));
        sykmelding.getGradAvSykmeldingListe().add(gradering3);

        FimsykGradering gradering2 = new FimsykGradering();
        gradering2.setSykmeldingsgrad(BigDecimal.valueOf(SYKMELDING1_GRADERING2_GRAD));
        gradering2.setGradert(createFimPeriode(SYKMELDING1_GRADERING2_PERIODE_FOM, SYKMELDING1_GRADERING2_PERIODE_TOM));
        sykmelding.getGradAvSykmeldingListe().add(gradering2);

        sykmeldinger.add(sykmelding);

        FimsykSykmelding sykmelding2 = new FimsykSykmelding();
        sykmelding2.setSykmelder(SYKMELDING2_SYKMELDER);
        sykmelding2.setBehandlet(DateUtils.convertDateToXmlGregorianCalendar(SYKMELDING2_BEHANDLINGSDATO));
        sykmelding2.setGjelderYrkesskade(SYKMELDING2_YRKESSKADE);
        sykmelding2.setSykmeldt(createFimPeriode(SYKMELDING2_PERIODE_FOM, SYKMELDING2_PERIODE_TOM));

        FimsykGradering gradering21 = new FimsykGradering();
        gradering21.setSykmeldingsgrad(BigDecimal.valueOf(SYKMELDING2_GRADERING1_GRAD));
        gradering21.setGradert(createFimPeriode(SYKMELDING2_GRADERING1_PERIODE_FOM, SYKMELDING2_GRADERING1_PERIODE_TOM));
        sykmelding2.getGradAvSykmeldingListe().add(gradering21);
        sykmeldinger.add(sykmelding2);

        FimsykSykmelding sykmelding3 = new FimsykSykmelding();
        sykmelding3.setSykmelder(SYKMELDING3_SYKMELDER);
        sykmelding3.setBehandlet(DateUtils.convertDateToXmlGregorianCalendar(SYKMELDING3_BEHANDLINGSDATO));
        sykmelding3.setGjelderYrkesskade(SYKMELDING3_YRKESSKADE);
        sykmelding3.setSykmeldt(createFimPeriode(SYKMELDING3_PERIODE_FOM, SYKMELDING3_PERIODE_TOM));
        sykmeldinger.add(sykmelding3);

        return sykmeldinger;
    }

    private static FimsykYrkesskade createYrkesskade() {
        FimsykYrkesskade yrkesskade = new FimsykYrkesskade();
        yrkesskade.setSkadet(DateUtils.convertDateToXmlGregorianCalendar(SYKMELDING1_YRKESSKADE_SKADET_DATO));
        yrkesskade.setVedtatt(DateUtils.convertDateToXmlGregorianCalendar(SYKMEDLING1_YRKESSKADE_VEDTAKS_DATO));
        yrkesskade.setYrkesskadeart(new FimsykYrkesskadeart().withKode(SYKMEDLING1_YRKESSKADE_SKADEART_KODE).withTermnavn(SYKMEDLING1_YRKESSKADE_SKADEART_TERM));
        return yrkesskade;
    }

    private static FimsykForsikring createForsikring() {
        FimsykForsikring fimForsikring = new FimsykForsikring();
        fimForsikring.setErGyldig(FORSIKRING_GYLDIG);
        fimForsikring.setForsikringsordning(FORSIKRINGSORDNING);
        fimForsikring.setForsikret(createFimPeriode(FORSIKRING_FOM, FORSIKRING_TOM));
        fimForsikring.setPremiegrunnlag(BigDecimal.valueOf(FORSIKRING_PREMIEGRUNNLAG));
        return fimForsikring;
    }

    private static FimsykArbeidskategori createArbeidskategori() {
        return new FimsykArbeidskategori().withKode(ARBEIDSKATEGORI_KODE).withTermnavn(ARBEIDSKATEGORI_TERM_ARBEIDSLEDIG);
    }

    private static FimsykPeriode createFimPeriode(Date fom, Date tom) {
        FimsykPeriode fimPeriode = new FimsykPeriode();
        fimPeriode.setFom(DateUtils.convertDateToXmlGregorianCalendar(fom));
        fimPeriode.setTom(DateUtils.convertDateToXmlGregorianCalendar(tom));
        return fimPeriode;
    }
}
