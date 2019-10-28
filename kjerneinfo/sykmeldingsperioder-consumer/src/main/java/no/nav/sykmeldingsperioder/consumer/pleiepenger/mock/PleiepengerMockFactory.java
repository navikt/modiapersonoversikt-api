package no.nav.sykmeldingsperioder.consumer.pleiepenger.mock;

import no.nav.kjerneinfo.common.mockutils.DateUtils;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse;
import org.joda.time.LocalDate;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PleiepengerMockFactory {

    public static final String PERSON_ID = "12345612345";
    public static final int PERIODE_GRADERINGSGRAD = 66;
    public static final Date PERIODEFRA = new LocalDate(2017, 1, 6).toDate();
    public static final Date PERIODETOM = new LocalDate(2017, 2, 11).toDate();
    public static final Date PERIODEFRA2 = new LocalDate().toDate();
    public static final Date PERIODETOM2 = new LocalDate().plusDays(14).toDate();
    public static final Date PERIODEFRA3 = new LocalDate(2012, 10, 10).toDate();
    public static final Date PERIODETOM3 = new LocalDate(2014, 10, 10).toDate();

    public static final Date ARBEIDSFORFOLD_SYKEPENGER_FOM2 = new LocalDate(2012, 10, 6).toDate();
    public static final XMLGregorianCalendar ARBEIDSFORFOLD_REFUSJON_TOM2 =
            DateUtils.convertDateToXmlGregorianCalendar(new LocalDate(2012, 12, 6).toDate());
    public static final String ARBEIDSFORHOLD_KONTONUMMER2 = "9999.12.7845";
    public static final String ARBEIDSFORHOLD_KONTONUMMER = "1234.12.7845";
    public static final String ARBEIDSFORHOLD_ORGNUMMER = "977119987";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_KODE = "Reftype";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_TERM = "Generell refusjon";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_KODE2 = "Reftype2";
    public static final String ARBEIDSFORHOLD_REFUSJONSTYPE_TERM2 = "Generell refusjon2";
    public static final String ARBEIDSFORHOLD_ORGNUMMER2 = "222222222";

    public static final Date ARBEIDSFORFOLD_SYKEPENGER_FOM = new LocalDate(2013, 7, 6).toDate();
    public static final XMLGregorianCalendar ARBEIDSFORFOLD_REFUSJON_TOM =
            DateUtils.convertDateToXmlGregorianCalendar(new LocalDate(2013, 10, 6).toDate());
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_KODE = "inntektsp1";
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_TERM = "Inntektsperiode 1";
    public static final Double ARBEIDSFORHOLD_INNTEKT = 625.20;
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_KODE2 = "inntektsp2";
    public static final String ARBEIDSFORHOLD_INNTEKTPERIODE_TERM2 = "Inntektsperiode 2";
    public static final Double ARBEIDSFORHOLD_INNTEKT2 = 45.50;
    public static final String ARBEIDSGIVER_ORGNR = "123456789";
    public static final String ARBEIDSGIVER_ORGNR2 = "22222222222";
    public static final String SAKSBEHANDLER_IDENT = "SAKB001";

    public static final double KOMMENDE_UTBETALING_BRUTTOBELOP = 220.55;
    public static final double KOMMENDE_UTBETALING_DAGSATS1 = 120.89;
    public static final double KOMMENDE_UTBETALING2_BRUTTOBELOP = 22000.55;
    public static final double KOMMENDE_UTBETALING2_DAGSATS = 1000.00;
    public static final double KOMMENDE_UTBETALING2_GRAD = 65.0;

    public static final double KOMMENDE_UTBETALING1_UTBETALINGSGRAD = 50.0;
    public static final int KOMMENDE_UTBETALING1_KOMPENSASJONSGRAD = 100;

    public static final String ANDREFORELDRESFNR = "01016515487";
    public static final String BARN_FNR = "10108000398";
    public static final String BARN_2_FNR = "05039912245";
    public static final String BARN_3_FNR = "040702";

    public static final Integer RESTADAGER = 515;
    public static final Integer FORBRUKTE_DAGER_TOM_IDAG = 475;
    public static final Integer PLEIEPENGERDAGER = 1300;
    public static final Integer RESTDAGER_FOM_IMORGEN = 333;

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
    private static final Integer PLEIEPENGEGRAD_1 = 66;
    private static final Integer PLEIEPENGEGRAD_2 = 100;
    private static final int ANTALL_PLEIEPENGERDAGER = 15;
    private static final String ARBEIDSKATEGORI = "Arbeidstaker";

    public static WSHentPleiepengerettighetResponse createWsHentPleiepengerListeResponse() {
        return new WSHentPleiepengerettighetResponse()
                .withPleiepengerettighetListe(
                        createPleiepengerettighet1(),
                        createPleiepengerettighet2(),
                        createPleiepengerettighet3()
                );
    }

    private static WSPleiepengerettighet createPleiepengerettighet1() {
        return new WSPleiepengerettighet()
                .withOmsorgsperson(new WSPerson().withIdent(PERSON_ID))
                .withAndreOmsorgsperson(new WSPerson().withIdent(ANDREFORELDRESFNR))
                .withBarnet(new WSPerson().withIdent(BARN_FNR))
                .withRestDagerAnvist(RESTADAGER)
                .withForbrukteDagerTOMIDag(FORBRUKTE_DAGER_TOM_IDAG)
                .withPleiepengedager(PLEIEPENGERDAGER)
                .withRestDagerFOMIMorgen(RESTDAGER_FOM_IMORGEN)
                .withPleiepengeperiodeListe(Arrays.asList(createPleiepengeperiode(PERIODEFRA),
                        createPleiepengeperiode(PERIODEFRA2)));
    }

    private static WSPleiepengerettighet createPleiepengerettighet2() {
        return new WSPleiepengerettighet()
                .withOmsorgsperson(new WSPerson().withIdent(PERSON_ID))
                .withAndreOmsorgsperson(new WSPerson().withIdent(ANDREFORELDRESFNR))
                .withBarnet(new WSPerson().withIdent(BARN_2_FNR))
                .withRestDagerAnvist(RESTADAGER)
                .withForbrukteDagerTOMIDag(FORBRUKTE_DAGER_TOM_IDAG)
                .withPleiepengedager(PLEIEPENGERDAGER)
                .withRestDagerFOMIMorgen(RESTDAGER_FOM_IMORGEN)
                .withPleiepengeperiodeListe(Arrays.asList(createPleiepengeperiode(PERIODEFRA3),
                        createPleiepengeperiode(PERIODEFRA4)));
    }

    private static WSPleiepengerettighet createPleiepengerettighet3() {
        return new WSPleiepengerettighet()
                .withOmsorgsperson(new WSPerson().withIdent(PERSON_ID))
                .withAndreOmsorgsperson(new WSPerson().withIdent(ANDREFORELDRESFNR))
                .withBarnet(new WSPerson().withIdent(BARN_3_FNR))
                .withRestDagerAnvist(RESTADAGER)
                .withForbrukteDagerTOMIDag(FORBRUKTE_DAGER_TOM_IDAG)
                .withPleiepengedager(PLEIEPENGERDAGER)
                .withRestDagerFOMIMorgen(RESTDAGER_FOM_IMORGEN)
                .withPleiepengeperiodeListe(Arrays.asList(createPleiepengeperiode(PERIODEFRA3),
                        createPleiepengeperiode(PERIODEFRA4)));
    }

    private static List<WSArbeidsforhold> createArbeidsforholdList() {
        List<WSArbeidsforhold> arbeidsforholdList = new ArrayList<>();
        WSRefusjonstype refusjonstype1 = new WSRefusjonstype();
        refusjonstype1.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE);
        refusjonstype1.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM);
        WSRefusjonstype refusjonstype2 = new WSRefusjonstype();
        refusjonstype2.setKode(ARBEIDSFORHOLD_REFUSJONSTYPE_KODE2);
        refusjonstype2.setTermnavn(ARBEIDSFORHOLD_REFUSJONSTYPE_TERM2);
        WSInntektsperiode inntektperiode = new WSInntektsperiode();
        inntektperiode.setKode(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE);
        inntektperiode.setTermnavn(ARBEIDSFORHOLD_INNTEKTPERIODE_TERM);
        WSInntektsperiode inntektperiode2 = new WSInntektsperiode();
        inntektperiode2.setKode(ARBEIDSFORHOLD_INNTEKTPERIODE_KODE2);
        inntektperiode2.setTermnavn(ARBEIDSFORHOLD_INNTEKTPERIODE_TERM2);
        arbeidsforholdList
                .add(createArbeidsforhold(ARBEIDSFORHOLD_KONTONUMMER, ARBEIDSFORHOLD_ORGNUMMER, ARBEIDSFORFOLD_REFUSJON_TOM, ARBEIDSFORFOLD_SYKEPENGER_FOM, refusjonstype1, inntektperiode, ARBEIDSFORHOLD_INNTEKT));
        arbeidsforholdList.add(createArbeidsforhold(ARBEIDSFORHOLD_KONTONUMMER2, ARBEIDSFORHOLD_ORGNUMMER2, ARBEIDSFORFOLD_REFUSJON_TOM2, ARBEIDSFORFOLD_SYKEPENGER_FOM2, refusjonstype2, inntektperiode2,
                ARBEIDSFORHOLD_INNTEKT2));

        return arbeidsforholdList;
    }

    private static WSArbeidsforhold createArbeidsforhold(String kontonummer, String orgnummer, XMLGregorianCalendar refusjonTom, Date sykepengerFom, WSRefusjonstype refusjonstype, WSInntektsperiode inntektsperiode, Double inntekt) {
        WSArbeidsforhold arbeidsforhold = new WSArbeidsforhold();
        arbeidsforhold.setArbeidsgiverKontonr(kontonummer);
        arbeidsforhold.setInntektsperiode(inntektsperiode);
        arbeidsforhold.setRefusjonstype(refusjonstype);
        arbeidsforhold.setInntektForPerioden(BigDecimal.valueOf(inntekt));
        arbeidsforhold.setRefusjonTom(refusjonTom);
        arbeidsforhold.setArbeidsgiverOrgnr(orgnummer);

        return arbeidsforhold;
    }

    private static WSPleiepengeperiode createPleiepengeperiode(Date fraOgMed) {
        return new WSPleiepengeperiode()
                .withAntallPleiepengedager(ANTALL_PLEIEPENGERDAGER)
                .withArbeidskategori(new WSArbeidskategori().withTermnavn(ARBEIDSKATEGORI))
                .withPleiepengerFom(DateUtils.convertDateToXmlGregorianCalendar(fraOgMed))
                .withArbeidsforholdListe(createArbeidsforholdList())
                .withVedtakListe(createUtbetalinger());
    }

    private static List<WSVedtak> createUtbetalinger() {
        List<WSVedtak> utbetalinger = new ArrayList<>();
        utbetalinger.add(createKommenedeUtbetaling());
        utbetalinger.add(createKommendeUtbetaling2());
        utbetalinger.add(createKommendeUtbetaling3());
        utbetalinger.add(createKommendeUtbetaling4());
        utbetalinger.add(createKommendeUtbetaling5());
        utbetalinger.add(createKommendeUtbetaling6());
        return utbetalinger;
    }

    private static WSVedtak createKommenedeUtbetaling() {
        WSVedtak kommendeUtbetaling = new WSVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING_DAGSATS1));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA, PERIODETOM));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING1_UTBETALINGSGRAD));
        kommendeUtbetaling.setArbeidsgiverKontonr(ARBEIDSFORHOLD_KONTONUMMER);
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT);
        kommendeUtbetaling.setAnvistUtbetaling(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA));
        kommendeUtbetaling.setKompensasjonsgrad(KOMMENDE_UTBETALING1_KOMPENSASJONSGRAD);
        kommendeUtbetaling.setPleiepengegrad(PLEIEPENGEGRAD_1);

        return kommendeUtbetaling;
    }

    private static WSVedtak createKommendeUtbetaling2() {
        WSVedtak kommendeUtbetaling = new WSVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING2_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA2, PERIODETOM2));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING2_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING2_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR2);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT2);
        kommendeUtbetaling.setAnvistUtbetaling(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA));
        kommendeUtbetaling.setPleiepengegrad(PLEIEPENGEGRAD_2);

        return kommendeUtbetaling;
    }


    private static WSVedtak createKommendeUtbetaling3() {
        WSVedtak kommendeUtbetaling = new WSVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING3_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA3, PERIODETOM3));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING3_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING3_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR3);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT3);
        kommendeUtbetaling.setAnvistUtbetaling(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA));
        kommendeUtbetaling.setPleiepengegrad(PLEIEPENGEGRAD_2);

        return kommendeUtbetaling;
    }
    private static WSVedtak createKommendeUtbetaling4() {
        WSVedtak kommendeUtbetaling = new WSVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING4_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA4, PERIODETOM4));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING4_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING4_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR4);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT4);
        kommendeUtbetaling.setAnvistUtbetaling(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA));
        kommendeUtbetaling.setPleiepengegrad(PLEIEPENGEGRAD_2);

        return kommendeUtbetaling;
    }
    private static WSVedtak createKommendeUtbetaling5() {
        WSVedtak kommendeUtbetaling = new WSVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING5_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA5, PERIODETOM5));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING5_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING5_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR5);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT5);
        kommendeUtbetaling.setAnvistUtbetaling(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA));
        kommendeUtbetaling.setPleiepengegrad(PLEIEPENGEGRAD_1);

        return kommendeUtbetaling;
    }
    private static WSVedtak createKommendeUtbetaling6() {
        WSVedtak kommendeUtbetaling = new WSVedtak();
        kommendeUtbetaling.setDagsats(BigDecimal.valueOf(KOMMENDE_UTBETALING6_DAGSATS));
        kommendeUtbetaling.setVedtak(createFimPeriode(PERIODEFRA6, PERIODETOM6));
        kommendeUtbetaling.setBruttobeloep(BigDecimal.valueOf(KOMMENDE_UTBETALING6_BRUTTOBELOP));
        kommendeUtbetaling.setUtbetalingsgrad(BigDecimal.valueOf(KOMMENDE_UTBETALING6_GRAD));
        kommendeUtbetaling.setArbeidsgiverOrgnr(ARBEIDSGIVER_ORGNR6);
        kommendeUtbetaling.setSaksbehandler(SAKSBEHANDLER_IDENT6);
        kommendeUtbetaling.setAnvistUtbetaling(DateUtils.convertDateToXmlGregorianCalendar(PERIODEFRA));
        kommendeUtbetaling.setPleiepengegrad(PLEIEPENGEGRAD_2);

        return kommendeUtbetaling;
    }

    private static WSPeriode createFimPeriode(Date fom, Date tom) {
        WSPeriode fimPeriode = new WSPeriode();
        fimPeriode.setFom(DateUtils.convertDateToXmlGregorianCalendar(fom));
        fimPeriode.setTom(DateUtils.convertDateToXmlGregorianCalendar(tom));
        return fimPeriode;
    }
}
