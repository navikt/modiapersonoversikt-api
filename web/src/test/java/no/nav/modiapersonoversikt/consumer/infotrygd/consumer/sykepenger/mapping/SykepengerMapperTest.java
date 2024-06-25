package no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping;

import no.nav.modiapersonoversikt.commondomain.Periode;
import no.nav.modiapersonoversikt.utils.DateUtils;
import no.nav.personoversikt.common.test.snapshot.SnapshotRule;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.SykepengerMockFactory;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.*;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger.Forsikring;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger.Sykmelding;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger.Sykmeldingsperiode;
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.sykepenger.Yrkesskade;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeRequest;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SykepengerMapperTest {
    @Rule
    public SnapshotRule snapshot = new SnapshotRule();

    private SykepengerMapper mapper;

    @Before
    public void before() {
        mapper = SykepengerMapper.getInstance();
    }

    @Test
    public void testRequest() {
        LocalDate from = LocalDate.parse("2020-10-13").minusMonths(12);
        LocalDate to = LocalDate.parse("2020-10-13");
        String ident = "12345612345";

        SykepengerMapper mapper = SykepengerMapper.getInstance();
        SykepengerRequest request = new SykepengerRequest();
        request.setFrom(from);
        request.setTo(to);
        request.setIdent(ident);

        FimHentSykepengerListeRequest fimRequest = mapper.map(request);
        assertThat(fimRequest.getIdent(), equalTo(ident));

        compareDates(from, fimRequest.getSykmelding().getFom());
        compareDates(to, fimRequest.getSykmelding().getTom());
        snapshot.assertMatches(fimRequest);
    }

    @Test
    public void testResponse() {
        SykepengerMapper mapper = SykepengerMapper.getInstance();
        FimHentSykepengerListeResponse fimResponse = new FimHentSykepengerListeResponse();

        FimsykSykmeldingsperiode sykmeldingsperiode = new FimsykSykmeldingsperiode();
        long forbrukteDager = 100L;
        sykmeldingsperiode.setForbrukteDager(BigInteger.valueOf(forbrukteDager));
        Date iddato = new LocalDate(2012, 12, 20).toDate();
        sykmeldingsperiode.setSykmeldtFom(DateUtils.convertDateToXmlGregorianCalendar(iddato));

        FimsykSykmelding sykmelding = new FimsykSykmelding();
        sykmelding.setSykmelder("Dr. med. Nils Nilsen");
        sykmeldingsperiode.getSykmeldingListe().add(sykmelding);

        FimsykKommendeVedtak kommendeUtbetaling = new FimsykKommendeVedtak();
        FimsykOppgjoerstype oppgjorstype = new FimsykOppgjoerstype();
        oppgjorstype.setKode("oppgjorstype");
        String termKontant = "Kontant";
        oppgjorstype.setTermnavn(termKontant);
        kommendeUtbetaling.setOppgjoerstype(oppgjorstype);

        FimsykHistoriskVedtak historiskUtbetaling = new FimsykHistoriskVedtak();
        double nettoBelop = 10000.99;

        sykmeldingsperiode.getVedtakListe().add(kommendeUtbetaling);
        sykmeldingsperiode.getVedtakListe().add(historiskUtbetaling);

        fimResponse.getSykmeldingsperiodeListe().add(sykmeldingsperiode);

        SykepengerResponse response = mapper.map(fimResponse);

        Sykmeldingsperiode resSykmeldingsperiode = response.getSykmeldingsperioder().get(0);
        Sykmelding resSykmedling = resSykmeldingsperiode.getSykmeldinger().get(0);

        assertThat(resSykmedling.getSykmelder(), equalTo(sykmelding.getSykmelder()));
        assertThat(resSykmedling.getSykmelder(), equalTo(sykmelding.getSykmelder()));
        snapshot.assertMatches(response);

    }

    @Test
    public void testMockFactoryResponse() {
        FimHentSykepengerListeResponse fimResponse = SykepengerMockFactory.createFimHentSykepengerResponse();
        SykepengerMapper mapper = SykepengerMapper.getInstance();
        SykepengerResponse resResponse = mapper.map(fimResponse);

        Sykmeldingsperiode sykmeldingsperiode = resResponse.getSykmeldingsperioder().get(0);

        UtbetalingPaVent utbetalingPaaVent = sykmeldingsperiode.getUtbetalingerPaVent().get(0);
        assertThat(utbetalingPaaVent.getArbeidskategori().getKode(), equalTo(SykepengerMockFactory.ARBEIDSKATEGORI_KODE));
        assertThat(utbetalingPaaVent.getOppgjoerstype().getKode(), equalTo(SykepengerMockFactory.UTBETALING_PA_VENT_OPPGJORSTYPE_KODE));
        assertThat(utbetalingPaaVent.getOppgjoerstype().getTermnavn(), equalTo(SykepengerMockFactory.UTBETALING_PA_VENT_OPPGJORSTYPE_TERM));
        assertThat(utbetalingPaaVent.getUtbetalingsgrad(), equalTo(SykepengerMockFactory.UTBETALING_PA_VENT_UTBETALINGSGRAD));
        assertThat(utbetalingPaaVent.getVedtak().getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.UTBETALING_PA_VENT_PERIODE_FOM)));
        assertThat(utbetalingPaaVent.getVedtak().getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.UTBETALING_PA_VENT_PERIODE_TOM)));

        KommendeUtbetaling kommendeUtbetaling = sykmeldingsperiode.getKommendeUtbetalinger().get(0);
        assertThat(kommendeUtbetaling.getUtbetalingsgrad(), equalTo(SykepengerMockFactory.KOMMENDE_UTBETALING1_UTBETALINGSGRAD));
        assertThat(kommendeUtbetaling.getVedtak().getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.KOMMENDE_UTBETALING1_PERIODE_FOM)));
        assertThat(kommendeUtbetaling.getVedtak().getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.KOMMENDE_UTBETALING1_PERIODE_TOM)));
        assertThat(kommendeUtbetaling.getType().getTermnavn(), equalTo(SykepengerMockFactory.KOMMENDE_UTBETALING1_TYPE_TERMNAVN));

        Sykmelding sykmelding1 = sykmeldingsperiode.getSykmeldinger().get(0);
        assertThat(sykmelding1.getBehandlet(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING1_BEHANDLINGSDATO)));
        assertThat(sykmelding1.getSykmeldt().getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING1_PERIODE_FOM)));
        assertThat(sykmelding1.getSykmeldt().getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING1_PERIODE_TOM)));
        assertThat(sykmelding1.getSykmelder(), equalTo(SykepengerMockFactory.SYKMELDING1_SYKMELDER));
        assertThat(sykmelding1.getSykmeldingsgrad(), equalTo(SykepengerMockFactory.SYKMELDING1_GRADERING3_GRAD));
        assertThat(sykmelding1.getGradAvSykmeldingListe().get(0).getGradert().getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING1_GRADERING1_PERIODE_FOM)));
        assertThat(sykmelding1.getGradAvSykmeldingListe().get(0).getGradert().getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING1_GRADERING1_PERIODE_TOM)));
        assertThat(sykmelding1.getGradAvSykmeldingListe().get(0).getSykmeldingsgrad(), equalTo(SykepengerMockFactory.SYKMELDING1_GRADERING1_GRAD));

        Yrkesskade yrkesskade = sykmelding1.getGjelderYrkesskade();
        assertThat(yrkesskade.getSkadet(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING1_YRKESSKADE_SKADET_DATO)));
        assertThat(yrkesskade.getVedtatt(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMEDLING1_YRKESSKADE_VEDTAKS_DATO)));
        assertThat(yrkesskade.getYrkesskadeart().getKode(), equalTo(SykepengerMockFactory.SYKMEDLING1_YRKESSKADE_SKADEART_KODE));
        assertThat(yrkesskade.getYrkesskadeart().getTermnavn(), equalTo(SykepengerMockFactory.SYKMEDLING1_YRKESSKADE_SKADEART_TERM));

        Sykmelding sykmelding2 = sykmeldingsperiode.getSykmeldinger().get(1);
        assertThat(sykmelding2.getBehandlet(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING2_BEHANDLINGSDATO)));
        assertThat(sykmelding2.getSykmeldt().getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING2_PERIODE_FOM)));
        assertThat(sykmelding2.getSykmeldt().getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING2_PERIODE_TOM)));
        assertThat(sykmelding2.getSykmelder(), equalTo(SykepengerMockFactory.SYKMELDING2_SYKMELDER));
        assertThat(sykmelding2.getSykmeldingsgrad(), equalTo(SykepengerMockFactory.SYKMELDING2_GRADERING1_GRAD));
        assertThat(sykmelding2.getGradAvSykmeldingListe().get(0).getSykmeldingsgrad(), equalTo(SykepengerMockFactory.SYKMELDING2_GRADERING1_GRAD));
        assertThat(sykmelding2.getGradAvSykmeldingListe().get(0).getGradert().getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING2_GRADERING1_PERIODE_FOM)));
        assertThat(sykmelding2.getGradAvSykmeldingListe().get(0).getGradert().getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.SYKMELDING2_GRADERING1_PERIODE_TOM)));

        Arbeidsforhold arbeidsforhold = sykmeldingsperiode.getArbeidsforholdListe().get(0);
        assertThat(arbeidsforhold.getArbeidsgiverNavn(), equalTo(SykepengerMockFactory.ARBEIDSFORHOLD_NAVN));
        assertThat(arbeidsforhold.getArbeidsgiverKontonr(), equalTo(SykepengerMockFactory.ARBEIDSFORHOLD_KONTONUMMER));
        assertThat(arbeidsforhold.getRefusjonTom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.ARBEIDSFORFOLD_REFUSJON_TOM)));
        assertThat(arbeidsforhold.getRefusjonstype().getTermnavn(), equalTo(SykepengerMockFactory.ARBEIDSFORHOLD_REFUSJONSTYPE_TERM));
        assertThat(arbeidsforhold.getInntektForPerioden(), equalTo(SykepengerMockFactory.ARBEIDSFORHOLD_INNTEKT));
        assertThat(arbeidsforhold.getInntektsperiode().getTermnavn(), equalTo(SykepengerMockFactory.ARBEIDSFORHOLD_INNTEKTPERIODE_TERM));

        boolean arbeidsgiverperiode = sykmeldingsperiode.getErArbeidsgiverperiode();
        assertThat(arbeidsgiverperiode, equalTo(SykepengerMockFactory.ARBEIDSGIVER_PERIODE));

        Kodeverkstype arbeidskategori = sykmeldingsperiode.getArbeidskategori();
        assertThat(arbeidskategori.getKode(), equalTo(SykepengerMockFactory.ARBEIDSKATEGORI_KODE));
        assertThat(arbeidskategori.getTermnavn(), equalTo(SykepengerMockFactory.ARBEIDSKATEGORI_TERM_ARBEIDSLEDIG));

        Bruker bruker = sykmeldingsperiode.getBruker();
        assertThat(bruker.getIdent(), equalTo(SykepengerMockFactory.PERSON_ID));

        assertThat(sykmeldingsperiode.getMidlertidigStanset(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.MIDLERTIDIGSTANSDATO)));

        Periode ferieperiode = sykmeldingsperiode.getFerie1();
        assertThat(ferieperiode.getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.FERIEPERIODE_FOM)));
        assertThat(ferieperiode.getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.FERIEPERIODE_TOM)));

        Periode ferieperiode2 = sykmeldingsperiode.getFerie2();
        assertThat(ferieperiode2.getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.FERIEPERIODE2_FOM)));
        assertThat(ferieperiode2.getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.FERIEPERIODE2_TOM)));

        assertThat(sykmeldingsperiode.getForbrukteDager(), equalTo(SykepengerMockFactory.FORBRUKTE_DAGER));
        assertThat(sykmeldingsperiode.getSykmeldtFom().toDate(), equalTo(SykepengerMockFactory.IDDATO));

        Forsikring gjeldendeForsikring = sykmeldingsperiode.getGjeldendeForsikring();
        assertThat(gjeldendeForsikring.getErGyldig(), equalTo(SykepengerMockFactory.FORSIKRING_GYLDIG));
        assertThat(gjeldendeForsikring.getForsikringsordning(), equalTo(SykepengerMockFactory.FORSIKRINGSORDNING));
        assertThat(gjeldendeForsikring.getForsikret().getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.FORSIKRING_FOM)));
        assertThat(gjeldendeForsikring.getForsikret().getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.FORSIKRING_TOM)));
        assertThat(gjeldendeForsikring.getPremiegrunnlag(), equalTo(SykepengerMockFactory.FORSIKRING_PREMIEGRUNNLAG));

        Periode sanksjonsPeriode = sykmeldingsperiode.getSanksjon();
        compareDates(sanksjonsPeriode.getFrom(), DateUtils.convertDateToXmlGregorianCalendar(SykepengerMockFactory.SANKSJON_FOM));
        compareDates(sanksjonsPeriode.getTo(), DateUtils.convertDateToXmlGregorianCalendar(SykepengerMockFactory.SANKSJON_TOM));
        assertThat(sykmeldingsperiode.getSlutt(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.MAKSDATO)));
        assertThat(sykmeldingsperiode.getStansarsak().getKode(), equalTo(SykepengerMockFactory.STANSAARSAK_KODE));
        assertThat(sykmeldingsperiode.getStansarsak().getTermnavn(), equalTo(SykepengerMockFactory.STANSARRSAK_TERM));
        assertThat(sykmeldingsperiode.getUnntakAktivitet().getTermnavn(), equalTo(SykepengerMockFactory.UNNTAK_AKTIVITET_TERM));

        Sykmeldingsperiode sykmeldingsperiode1 = resResponse.getSykmeldingsperioder().get(1);
        Periode ferieperiode3 = sykmeldingsperiode1.getFerie1();
        assertThat(ferieperiode3.getFrom(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.FERIEPERIODE_FOM)));
        assertThat(ferieperiode3.getTo(), equalTo(LocalDate.fromDateFields(SykepengerMockFactory.FERIEPERIODE_TOM)));
        Periode ferieperiode4 = sykmeldingsperiode1.getFerie2();
        assertThat(ferieperiode4, equalTo(null));
        assertThat(sykmeldingsperiode1.getStansarsak().getKode(), equalTo(SykepengerMockFactory.STANSAARSAK_MANGLENDE_AKTIVITET_KODE));
        assertThat(sykmeldingsperiode1.getStansarsak().getTermnavn(), equalTo(SykepengerMockFactory.STANSARRSAK_MANGLENDE_AKTIVITET_TERM));

        Sykmeldingsperiode sykmeldingsperiode2 = resResponse.getSykmeldingsperioder().get(2);
        assertThat(sykmeldingsperiode2.getStansarsak().getKode(), equalTo(SykepengerMockFactory.STANSAARSAK_NOE_ANNET_KODE));
        assertThat(sykmeldingsperiode2.getStansarsak().getTermnavn(), equalTo(SykepengerMockFactory.STANSARRSAK_NOE_ANNET_TERM));
        snapshot.assertMatches(resResponse);
    }

    @Test
    public void dateMapping() {
        XMLGregorianCalendar xmlDate = DateUtils.convertDateToXmlGregorianCalendar(SykepengerMockFactory.SANKSJON_FOM);

        LocalDate to = mapper.map(xmlDate);

        compareDates(to, xmlDate);
        snapshot.assertMatches(to);
    }

    private void compareDates(LocalDate from, XMLGregorianCalendar sykmeldtFraFom) {
        assertThat(sykmeldtFraFom.getDay(), equalTo(from.getDayOfMonth()));
        assertThat(sykmeldtFraFom.getMonth(), equalTo(from.getMonthOfYear()));
        assertThat(sykmeldtFraFom.getYear(), equalTo(from.getYear()));
    }
}
