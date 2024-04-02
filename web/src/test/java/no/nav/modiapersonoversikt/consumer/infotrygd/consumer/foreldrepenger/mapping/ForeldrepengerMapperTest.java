package no.nav.modiapersonoversikt.consumer.infotrygd.consumer.foreldrepenger.mapping;

import no.nav.modiapersonoversikt.commondomain.Periode;
import no.nav.modiapersonoversikt.utils.DateUtils;
import no.nav.personoversikt.common.test.snapshot.SnapshotRule;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.foreldrepenger.ForeldrepengerMockFactory;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.Arbeidsforhold;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.HistoriskUtbetaling;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.KommendeUtbetaling;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.foreldrepenger.Adopsjon;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.foreldrepenger.Foedsel;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.foreldrepenger.Foreldrepengeperiode;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.foreldrepenger.Foreldrepengerettighet;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetRequest;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class ForeldrepengerMapperTest {
    @Rule
    public SnapshotRule snapshot = new SnapshotRule();

    public static final LocalDate TERMINDATO = new LocalDate(2013, 2, 15);
    public static final LocalDate OMSORGSOVERTAKELSESDATO = new LocalDate(2013, 2, 14);
    private final LocalDate fomDate = new LocalDate(2013, 2, 13);
    private final XMLGregorianCalendar fomXMLDate = DateUtils.convertDateToXmlGregorianCalendar(fomDate.toDate());
    private final LocalDate tomDate = new LocalDate(2014, 2, 13);
    private final LocalDate IDDATE = new LocalDate(2015, 2, 13);
    private final XMLGregorianCalendar tomXMLDate = DateUtils.convertDateToXmlGregorianCalendar(tomDate.toDate());
    private final double nettoBelop = 10000.99;
    private final String refusjonstypeKode = "Etterbetalt";
    private final String termKontant = "Kontant";
    private final double dagSats = 250.33;
    private final String kreditorNavn = "Kreditor";

    private ForeldrepengerMapper mapper;

    @Before
    public void before() {
        mapper = ForeldrepengerMapper.getInstance();
    }

    @Test
    public void mappingRequest() {
        LocalDate from = new LocalDate(2013, 1, 1);
        LocalDate to = new LocalDate(2014, 1, 1);
        String brukerId = "12345612345";

        ForeldrepengerMapper mapper = ForeldrepengerMapper.getInstance();
        ForeldrepengerListeRequest request = new ForeldrepengerListeRequest();
        Periode periode = new Periode(from, to);
        request.setForeldrepengerettighetPeriode(periode);
        request.setIdent(brukerId);

        FimHentForeldrepengerettighetRequest fimRequest = mapper.map(request);
        assertThat(fimRequest.getIdent(), equalTo(brukerId));
        snapshot.assertMatches(fimRequest);
    }

    @Test
    public void mappingResponseAdopsjon() {
        ForeldrepengerMapper mapper = ForeldrepengerMapper.getInstance();
        FimHentForeldrepengerettighetResponse fimResponse = new FimHentForeldrepengerettighetResponse();

        FimAdopsjon foreldrepengerettighetA = new FimAdopsjon();
        createFimforeldrepengerettighet(foreldrepengerettighetA, true);

        FimForeldrepengeperiode periode = createFimforeldrepengeperiode();

        foreldrepengerettighetA.getForeldrepengeperiodeListe().add(periode);
        fimResponse.setForeldrepengerettighet(foreldrepengerettighetA);

        ForeldrepengerListeResponse response = mapper.map(fimResponse);

        Foreldrepengerettighet resforeldrepengerettighetA = response.getForeldrepengerettighet();
        Foreldrepengeperiode resperiode = resforeldrepengerettighetA.getPeriode().get(0);

        List<HistoriskUtbetaling> resHistoriskUtbetalinger = resperiode.getHistoriskeUtbetalinger();
        List<KommendeUtbetaling> resKommendeUtbetalinger = resperiode.getKommendeUtbetalinger();

        assertTrue(resforeldrepengerettighetA instanceof Adopsjon);
        Adopsjon adopsjon = (Adopsjon) resforeldrepengerettighetA;
        compareDates(adopsjon.getOmsorgsovertakelse(), foreldrepengerettighetA.getOmsorgsovertakelse());
        assertThat(adopsjon.getRettighetFom(), equalTo(adopsjon.getOmsorgsovertakelse()));
        assertThat(resforeldrepengerettighetA.getAndreForeldersFnr(), equalTo(foreldrepengerettighetA.getAndreForelder().getIdent()));
        compareDates(resforeldrepengerettighetA.getBarnetsFoedselsdato(), foreldrepengerettighetA.getBarnetFoedt());
        assertThat(resforeldrepengerettighetA.getArbeidsforholdListe().get(0).getArbeidsgiverNavn(), equalTo(foreldrepengerettighetA.getArbeidsforholdListe().get(0).getArbeidsgiverNavn()));
        assertThat(BigInteger.valueOf(resforeldrepengerettighetA.getRestDager()), equalTo(foreldrepengerettighetA.getRestDager()));
        assertThat(resforeldrepengerettighetA.getForeldrepengetype().getKode(), equalTo(foreldrepengerettighetA.getForeldrepengetype().getKode()));
        assertThat(resforeldrepengerettighetA.getForeldrepengetype().getTermnavn(), equalTo(foreldrepengerettighetA.getForeldrepengetype().getTermnavn()));

        assertThat(resforeldrepengerettighetA.getArbeidskategori().getKode(), equalTo(foreldrepengerettighetA.getArbeidskategori().getKode()));
        assertThat(resforeldrepengerettighetA.getArbeidskategori().getTermnavn(), equalTo(foreldrepengerettighetA.getArbeidskategori().getTermnavn()));

        compareDates(resperiode.getForeldrepengerFom(), periode.getForeldrepengerFom());
        assertThat(resperiode.isErFedrekvote(), equalTo(periode.isErFedrekvote()));
        snapshot.assertMatches(response);
    }

    @Test
    public void mappingResponseFoedsel() {

        ForeldrepengerMapper mapper = ForeldrepengerMapper.getInstance();
        FimHentForeldrepengerettighetResponse fimResponse = new FimHentForeldrepengerettighetResponse();

        FimFoedsel foreldrepengerettighetF = new FimFoedsel();
        createFimforeldrepengerettighet(foreldrepengerettighetF, false);

        FimForeldrepengeperiode periode = createFimforeldrepengeperiode();

        foreldrepengerettighetF.getForeldrepengeperiodeListe().add(periode);
        fimResponse.setForeldrepengerettighet(foreldrepengerettighetF);

        ForeldrepengerListeResponse response = mapper.map(fimResponse);

        Foreldrepengerettighet resforeldrepengerettighetF = response.getForeldrepengerettighet();
        Foreldrepengeperiode resperiode = resforeldrepengerettighetF.getPeriode().get(0);

        List<HistoriskUtbetaling> resHistoriskUtbetalinger = resperiode.getHistoriskeUtbetalinger();
        List<KommendeUtbetaling> resKommendeUtbetalinger = resperiode.getKommendeUtbetalinger();

        assertTrue(resforeldrepengerettighetF instanceof Foedsel);
        Foedsel foedsel = (Foedsel) resforeldrepengerettighetF;
        compareDates(foedsel.getTermin(), foreldrepengerettighetF.getTermin());
        assertThat(foedsel.getRettighetFom(), equalTo(foedsel.getTermin()));
        assertThat(resforeldrepengerettighetF.getAndreForeldersFnr(), equalTo(foreldrepengerettighetF.getAndreForelder().getIdent()));
        compareDates(resforeldrepengerettighetF.getBarnetsFoedselsdato(), foreldrepengerettighetF.getBarnetFoedt());
        assertThat(resforeldrepengerettighetF.getArbeidsforholdListe().get(0).getArbeidsgiverNavn(), equalTo(foreldrepengerettighetF.getArbeidsforholdListe().get(0).getArbeidsgiverNavn()));
        assertThat(BigInteger.valueOf(resforeldrepengerettighetF.getRestDager()), equalTo(foreldrepengerettighetF.getRestDager()));
        assertThat(resforeldrepengerettighetF.getForeldrepengetype().getKode(), equalTo(foreldrepengerettighetF.getForeldrepengetype().getKode()));
        assertThat(resforeldrepengerettighetF.getForeldrepengetype().getTermnavn(), equalTo(foreldrepengerettighetF.getForeldrepengetype().getTermnavn()));

        assertThat(resforeldrepengerettighetF.getArbeidskategori().getKode(), equalTo(foreldrepengerettighetF.getArbeidskategori().getKode()));
        assertThat(resforeldrepengerettighetF.getArbeidskategori().getTermnavn(), equalTo(foreldrepengerettighetF.getArbeidskategori().getTermnavn()));

        compareDates(resperiode.getForeldrepengerFom(), periode.getForeldrepengerFom());
        assertThat(resperiode.isErFedrekvote(), equalTo(periode.isErFedrekvote()));

        compareDates(resperiode.getKommendeUtbetalinger().get(0).getVedtak().getFrom(), periode.getVedtakListe().get(0).getVedtak().getFom());
        snapshot.assertMatches(response);
    }

    @Test
    public void mockFactoryResponse() {
        FimHentForeldrepengerettighetResponse fimResponse = ForeldrepengerMockFactory.createFimHentForeldrepengerListeResponse();
        ForeldrepengerMapper mapper = ForeldrepengerMapper.getInstance();
        ForeldrepengerListeResponse resResponse = mapper.map(fimResponse);

        Foreldrepengerettighet pengerettighet = resResponse.getForeldrepengerettighet();
        assertThat(pengerettighet.getAndreForeldersFnr(), equalTo(ForeldrepengerMockFactory.ANDREFORELDRESFNR));
        assertThat(pengerettighet.getBarnetsFoedselsdato(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.BARNETSFOEDSELSDATO_F)));
        assertThat(pengerettighet.getDekningsgrad(), equalTo(ForeldrepengerMockFactory.DEKNINSGRAD));
        assertThat(pengerettighet.getFedrekvoteTom(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.FEDREKVOTETOM)));
        assertThat(pengerettighet.getForeldrepengetype().getTermnavn(), equalTo(ForeldrepengerMockFactory.FORELDREPENGETYPE_F_TERM));
        assertThat(pengerettighet.getGraderingsdager(), equalTo(ForeldrepengerMockFactory.GRADERINGSDAGER2));
        assertThat(pengerettighet.getSlutt(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.MAKSDATO)));
        assertThat(pengerettighet.getRestDager(), equalTo(ForeldrepengerMockFactory.RESTDAGER));
        assertThat(pengerettighet.getForelder().getIdent(), equalTo(ForeldrepengerMockFactory.PERSON_ID));

        Arbeidsforhold arbeidsforhold = pengerettighet.getArbeidsforholdListe().get(0);
		assertThat(arbeidsforhold.getArbeidsgiverNavn(), equalTo(ForeldrepengerMockFactory.ARBEIDSFORHOLD_NAVN));
        assertThat(arbeidsforhold.getArbeidsgiverKontonr(), equalTo(ForeldrepengerMockFactory.ARBEIDSFORHOLD_KONTONUMMER));
        assertThat(arbeidsforhold.getRefusjonTom(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.PERIODETOM)));

        Foreldrepengeperiode periode = resResponse.getForeldrepengerettighet().getPeriode().get(0);
        assertThat(periode.isHarAleneomsorgFar(), equalTo(ForeldrepengerMockFactory.ALENEOMSORGFAR_FALSE));
        assertThat(periode.isHarAleneomsorgMor(), equalTo(ForeldrepengerMockFactory.ALENEOMSORGMOR_TRUE));
        assertThat(periode.getArbeidsprosentMor(), equalTo(ForeldrepengerMockFactory.ARBEIDSPROSENTMOR));
        assertThat(periode.getAvslaatt(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.AVSLAGSDATO)));
        assertThat(periode.getDisponibelGradering(), equalTo(ForeldrepengerMockFactory.DISPONIBELGRADERING));
        assertThat(periode.isErFedrekvote(), equalTo(ForeldrepengerMockFactory.FEDREKVOTE));

        assertThat(periode.getForskyvelsesaarsak1().getTermnavn(), equalTo(ForeldrepengerMockFactory.FORSKYVELSESAARSAK_TERM));
        assertThat(periode.getForskyvelsesperiode().getFrom(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.PERIODEFRA)));
        assertThat(periode.getForskyvelsesperiode().getTo(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.PERIODETOM)));

        assertThat(periode.getForskyvelsesaarsak2().getTermnavn(), equalTo(ForeldrepengerMockFactory.FORSKYVELSESAARSAK2_TERM));
        assertThat(periode.getForskyvelsesperiode2().getFrom(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.PERIODEFRA2)));
        assertThat(periode.getForskyvelsesperiode2().getTo(), equalTo(LocalDate.fromDateFields(ForeldrepengerMockFactory.PERIODETOM2)));

        assertThat(periode.getForeldrepengerFom(), equalTo(ForeldrepengerMockFactory.IDDATO));
        assertThat(periode.getMorSituasjon().getTermnavn(), equalTo(ForeldrepengerMockFactory.MORSSITUASJON_TERM));
        assertThat(periode.getRettTilFedrekvote().getTermnavn(), equalTo(ForeldrepengerMockFactory.RETTTILFEDREKVOTE_TERM));
        assertThat(periode.isRettTilModrekvote().getTermnavn(), equalTo(ForeldrepengerMockFactory.RETTTILMODREKVOTE_TERM));

        KommendeUtbetaling kommendeUtbetaling = periode.getKommendeUtbetalinger().get(0);
        assertThat(kommendeUtbetaling.getUtbetalingsgrad(), equalTo(ForeldrepengerMockFactory.KOMMENDE_UTBETALING1_UTBETALINGSGRAD));
        assertThat(kommendeUtbetaling.getSaksbehandler(), equalTo(ForeldrepengerMockFactory.SAKSBEHANDLER_IDENT));
        assertThat(kommendeUtbetaling.getArbeidsgiverOrgnr(), equalTo(ForeldrepengerMockFactory.ARBEIDSGIVER_ORGNR));
        snapshot.assertMatches(resResponse);
    }

    @Test
    public void dateMapping() {
        LocalDate to = mapper.map(fomXMLDate);

        compareDates(to, fomXMLDate);
        snapshot.assertMatches(to);
    }

    private FimForeldrepengeperiode createFimforeldrepengeperiode() {
        FimForeldrepengeperiode periode = new FimForeldrepengeperiode();

        periode.setHarAleneomsorgFar(true);
        periode.setHarAleneomsorgMor(false);
        periode.setArbeidsprosentMor(BigDecimal.valueOf(49, 59));
        periode.setDisponibelGradering(BigDecimal.valueOf(99, 999));
        periode.setErFedrekvote(true);
        FimForskyvelsesaarsak forskyvelsesaarsak = new FimForskyvelsesaarsak();
        forskyvelsesaarsak.setKode("Forskyvelsesaarsak");
        forskyvelsesaarsak.setTermnavn("Generell aarsak");
        periode.setForskyvelsesaarsak1(forskyvelsesaarsak);
        periode.setForeldrepengerFom(DateUtils.convertDateToXmlGregorianCalendar(IDDATE.toDate()));
        FimMorSituasjon morssituasjon = new FimMorSituasjon();
        morssituasjon.setKode("Mors situasjon");
        morssituasjon.setTermnavn("Ok");
        periode.setMorSituasjon(morssituasjon);
        FimRettTilFedrekvote rettFedrekvote = new FimRettTilFedrekvote();
        rettFedrekvote.setKode("fkvote");
        rettFedrekvote.setTermnavn("fedrekvote ja");
        periode.setRettTilFedrekvote(rettFedrekvote);
        FimRettTilMoedrekvote rettModrekvote = new FimRettTilMoedrekvote();
        rettModrekvote.setKode("mkvote");
        rettModrekvote.setTermnavn("modrekvote tja");
        periode.setRettTilMoedrekvote(rettModrekvote);
        FimStansaarsak stansaarsak = new FimStansaarsak();
        stansaarsak.setKode("Stansaarsak");
        stansaarsak.setTermnavn("Genrell aarsak");
        periode.setStansaarsak(stansaarsak);

        FimKommendeVedtak kommendeUtbetaling = new FimKommendeVedtak();
        FimOppgjoerstype oppgjorstype = new FimOppgjoerstype();
        oppgjorstype.setKode("oppgjorstype");
        oppgjorstype.setTermnavn("Kontant");
        kommendeUtbetaling.setOppgjoerstype(oppgjorstype);
        kommendeUtbetaling.setVedtak(createPeriode());
        periode.getVedtakListe().add(kommendeUtbetaling);

        FimHistoriskVedtak historiskUtbetaling = new FimHistoriskVedtak();
        FimRefusjonstype refusjonstype = new FimRefusjonstype();
        refusjonstype.setTermnavn("Refusjonstype");
        refusjonstype.setKode(refusjonstypeKode);
        historiskUtbetaling.setVedtak(createPeriode());
        historiskUtbetaling.setArbeidsgiverOrgnr(ForeldrepengerMockFactory.ARBEIDSGIVER_ORGNR);
        historiskUtbetaling.setSaksbehandler(ForeldrepengerMockFactory.SAKSBEHANDLER_IDENT);
        periode.getVedtakListe().add(historiskUtbetaling);

        LocalDate avslagsdatoDate = new LocalDate(2013, 2, 13);
        XMLGregorianCalendar avslagsdatoXMLdate = DateUtils.convertDateToXmlGregorianCalendar(avslagsdatoDate.toDate());
        periode.setAvslaatt(avslagsdatoXMLdate);

        FimPeriode fimperiodeForskyvelse = new FimPeriode();
        fimperiodeForskyvelse.setFom(fomXMLDate);
        fimperiodeForskyvelse.setTom(tomXMLDate);
        periode.setForskyvet1(fimperiodeForskyvelse);

        return periode;
    }

    private FimPeriode createPeriode() {
        LocalDate fradato = new LocalDate(2016, 2, 13);
        LocalDate tildato = new LocalDate(2016, 7, 13);
        return new FimPeriode()
                .withFom(DateUtils.convertDateToXmlGregorianCalendar(fradato.toDate()))
                .withTom(DateUtils.convertDateToXmlGregorianCalendar(tildato.toDate()));
    }

    private FimForeldrepengerettighet createFimforeldrepengerettighet(FimForeldrepengerettighet foreldrepengerettighet, boolean withNulls) {
        foreldrepengerettighet.setAndreForelder(new FimPerson().withIdent("10108000398"));
        FimArbeidskategori arbeidskategori = new FimArbeidskategori();
        arbeidskategori.setKode("Arbeidskategori");
        arbeidskategori.setTermnavn("Arbeidskategori");
        foreldrepengerettighet.setArbeidskategori(arbeidskategori);
        foreldrepengerettighet.setDekningsgrad(BigDecimal.valueOf(80, 50));
        foreldrepengerettighet.setGraderingsdager(BigInteger.valueOf(1254));
        foreldrepengerettighet.setRestDager(BigInteger.valueOf(54));

        FimPerson bruker = new FimPerson();
        bruker.setIdent("Bruker id");
        foreldrepengerettighet.setForelder(bruker);

        FimArbeidsforhold arbeidsforhold = new FimArbeidsforhold();
        if( withNulls ) {
			arbeidsforhold.setArbeidsgiverKontonr("25148755001");
			arbeidsforhold.setArbeidsgiverNavn("Arbeidsgiver");
			LocalDate refdate = new LocalDate(2013, 2, 13);
			XMLGregorianCalendar refXMLdate = DateUtils.convertDateToXmlGregorianCalendar(refdate.toDate());
			arbeidsforhold.setRefusjonTom(refXMLdate);
		}

        foreldrepengerettighet.getArbeidsforholdListe().add(arbeidsforhold);

        LocalDate barnetsFoedselsdatodate = new LocalDate(2013, 2, 13);
        XMLGregorianCalendar barnetsFoedselsdatoXMLdate = DateUtils.convertDateToXmlGregorianCalendar(barnetsFoedselsdatodate.toDate());
        foreldrepengerettighet.setBarnetFoedt(barnetsFoedselsdatoXMLdate);

        LocalDate fedrekvoteTomdate = new LocalDate(2013, 2, 13);
        XMLGregorianCalendar fedrekvoteTomXMLdate = DateUtils.convertDateToXmlGregorianCalendar(fedrekvoteTomdate.toDate());
        foreldrepengerettighet.setFedrekvoteTom(fedrekvoteTomXMLdate);

        if( withNulls ) {
			LocalDate maksdatodate = new LocalDate(2013, 2, 13);
			XMLGregorianCalendar maksdatoXMLdate = DateUtils.convertDateToXmlGregorianCalendar(maksdatodate.toDate());
			foreldrepengerettighet.setSlutt(maksdatoXMLdate);
		}

        if (foreldrepengerettighet instanceof FimAdopsjon adopsjon) {
            FimForeldrepengetype foreldrepengertype = new FimForeldrepengetype();
            foreldrepengertype.setKode("Foreldrepengetype");
            foreldrepengertype.setTermnavn("Adopsjon");
            foreldrepengerettighet.setForeldrepengetype(foreldrepengertype);

            XMLGregorianCalendar omsorgsovertakelseXMLDate = DateUtils.convertDateToXmlGregorianCalendar(OMSORGSOVERTAKELSESDATO.toDate());
            adopsjon.setOmsorgsovertakelse(omsorgsovertakelseXMLDate);
            return adopsjon;
        } else if (foreldrepengerettighet instanceof FimFoedsel foedsel) {
            FimForeldrepengetype foreldrepengertype = new FimForeldrepengetype();
            foreldrepengertype.setKode("Foreldrepengetype");
            foreldrepengertype.setTermnavn("Foedsel");
            foreldrepengerettighet.setForeldrepengetype(foreldrepengertype);

            XMLGregorianCalendar terminXMLDate = DateUtils.convertDateToXmlGregorianCalendar(TERMINDATO.toDate());
            foedsel.setTermin(terminXMLDate);
            return foedsel;
        }
        return null;
    }

    private void compareDates(LocalDate from, XMLGregorianCalendar sykmeldtFraFom) {
        assertThat(sykmeldtFraFom.getDay(), equalTo(from.getDayOfMonth()));
        assertThat(sykmeldtFraFom.getMonth(), equalTo(from.getMonthOfYear()));
        assertThat(sykmeldtFraFom.getYear(), equalTo(from.getYear()));
    }
}
