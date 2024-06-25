package no.nav.modiapersonoversiktproxy.consumer.infortrygd.pleiepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.PleiepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.to.PleiepengerListeResponse;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Arbeidsforhold;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Pleiepengeperiode;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Pleiepengerrettighet;
import no.nav.modiapersonoversiktproxy.utils.DateUtils;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetRequest;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class PleiepengerMapperTest {

    private static final String FNR_BARNET = "10108000398";
    private static final String FNR_OMSORGSPERSON = "12345612345";
    private static final String FNR_ANDRE_OMSORGSPERSON = "65432154321";
    private static final XMLGregorianCalendar FOM_DATO =
            DateUtils.convertDateTimeToXmlGregorianCalendar(LocalDateTime.parse("2014-05-14"));
    private static final XMLGregorianCalendar TOM_DATO =
            DateUtils.convertDateTimeToXmlGregorianCalendar(LocalDateTime.parse("2015-04-14"));
    private static final int DAGER = 123;
    private static final int PROSENT = 66;
    private static final String ORGNR = "123456789";
    private static final String KONTONUMMER = "12345678901";
    private static final BigDecimal INNTEKT = BigDecimal.valueOf(37123.45);
    private static final String INNTEKTSPERIODE = "Månedlig";
    private static final String REFUSJONSTYPE_TERMNAVN = "Refusjon";
    private static final String REFUSJONSTYPE_KODE = "J";
    private static final String ARBEIDSKATEGORI = "Fisker";

    private PleiepengerMapper mapper;

    @Before
    public void before() {
        mapper = new PleiepengerMapper();
    }

    private List<WSPleiepengerettighet> mockPleiepengerettighetListe() {
        return singletonList(
                new WSPleiepengerettighet()
                        .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                        .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON))
        );
    }

    @Test
    public void requestMappesTilWSRequest() {
        PleiepengerListeRequest request = new PleiepengerListeRequest(FNR_BARNET);

        WSHentPleiepengerettighetRequest wsRequest = mapper.map(request);

        assertThat(wsRequest.getOmsorgsperson().getIdent(), is(FNR_BARNET));
    }

    @Test
    public void wsResponseMappesMedPleiepengerettighetListe() {
        WSHentPleiepengerettighetResponse response = new WSHentPleiepengerettighetResponse()
                .withPleiepengerettighetListe(mockPleiepengerettighetListe());

        PleiepengerListeResponse domeneResponse = mapper.map(response);

        assertThat(domeneResponse.getPleieepengerettighetListe().isEmpty(), is(false));
    }

    @Test
    public void wsResponseMappesUtenPleiepengerettighetListe() {
        WSHentPleiepengerettighetResponse response = new WSHentPleiepengerettighetResponse();

        PleiepengerListeResponse domeneResponse = mapper.map(response);

        assertThat(domeneResponse.getPleieepengerettighetListe().isEmpty(), is(true));
    }

    @Test
    public void pleiepengerettighetMappesMedPerioder() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON))
                .withPleiepengeperiodeListe(singletonList(new WSPleiepengeperiode()
                        .withArbeidskategori(new WSArbeidskategori().withTermnavn(ARBEIDSKATEGORI))
                        .withPleiepengerFom(FOM_DATO)));

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getPerioder().isEmpty(), is(false));
    }

    @Test
    public void pleiepengeperiodeMappesMedRiktigDato() {
        WSPleiepengeperiode wsPeriode = new WSPleiepengeperiode()
                .withArbeidskategori(new WSArbeidskategori().withTermnavn(ARBEIDSKATEGORI))
                .withPleiepengerFom(FOM_DATO);

        Pleiepengeperiode periode = mapper.map(wsPeriode);

        assertThat(periode.getFraOgMed().getDayOfMonth(), is(FOM_DATO.getDay()));
        assertThat(periode.getFraOgMed().getMonthValue(), is(FOM_DATO.getMonth()));
        assertThat(periode.getFraOgMed().getYear(), is(FOM_DATO.getYear()));
    }

    @Test
    public void wsRettighetMappesMedOmsorgsperson() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON));

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getOmsorgsperson(), is(FNR_OMSORGSPERSON));
    }

    @Test
    public void wsRettighetMappesMedAndreOmsorgsperson() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON))
                .withAndreOmsorgsperson(new WSPerson().withIdent(FNR_ANDRE_OMSORGSPERSON));

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getAndreOmsorgsperson(), is(FNR_ANDRE_OMSORGSPERSON));
    }

    @Test
    public void wsRettighetMappesRiktigUtenAndreOmsorgsperson() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON));

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getAndreOmsorgsperson(), is(nullValue()));
    }

    @Test
    public void wsRettighetMappesMedPleiepengedager() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON))
                .withPleiepengedager(DAGER);

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getPleiepengedager(), is(DAGER));
    }

    @Test
    public void wsRettighetMappesMedForbrukteDagerTOMIDag() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON))
                .withForbrukteDagerTOMIDag(DAGER);

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getForbrukteDagerTOMIDag(), is(DAGER));
    }

    @Test
    public void wsRettighetMappesMedRestDagerFOMIMorgen() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON))
                .withRestDagerFOMIMorgen(DAGER);

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getRestDagerFOMIMorgen(), is(DAGER));
    }

    @Test
    public void wsRettighetMappesMedRestDagerAnvist() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withBarnet(new WSPerson().withIdent(FNR_BARNET))
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON))
                .withRestDagerAnvist(DAGER);

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getRestDagerAnvist(), is(DAGER));
    }

    @Test
    public void wsRettighetMappesMedBarnet() {
        WSPleiepengerettighet wsRettighet = new WSPleiepengerettighet()
                .withOmsorgsperson(new WSPerson().withIdent(FNR_OMSORGSPERSON))
                .withBarnet(new WSPerson().withIdent(FNR_BARNET));

        Pleiepengerrettighet rettighet = mapper.map(wsRettighet);

        assertThat(rettighet.getBarnet(), is(FNR_BARNET));
    }

    @Test
    public void pleiepengeperiodeMappesMedVedtaksliste() {
        WSPleiepengeperiode wsPeriode = new WSPleiepengeperiode()
                .withArbeidskategori(new WSArbeidskategori().withTermnavn(ARBEIDSKATEGORI))
                .withPleiepengerFom(FOM_DATO)
                .withVedtakListe(VedtaksMapperTest.mockVedtakMedKunPakrevdeFelt());

        Pleiepengeperiode periode = mapper.map(wsPeriode);

        assertThat(periode.getVedtakListe().isEmpty(), is(false));
    }

    @Test
    public void pleiepengeperiodeMappesMedArbeidsforholdListe() {
        WSPleiepengeperiode wsPeriode = new WSPleiepengeperiode()
                .withArbeidskategori(new WSArbeidskategori().withTermnavn(ARBEIDSKATEGORI))
                .withPleiepengerFom(FOM_DATO)
                .withArbeidsforholdListe(new WSArbeidsforhold().withArbeidsgiverOrgnr(ORGNR));

        Pleiepengeperiode periode = mapper.map(wsPeriode);

        assertThat(periode.getArbeidsforholdListe(), is(notNullValue()));
    }

    @Test
    public void pleiepengeperiodeMappesMedArbeidskategoriPaaArbeidsforhold() {
        WSPleiepengeperiode wsPeriode = new WSPleiepengeperiode()
                .withPleiepengerFom(FOM_DATO)
                .withArbeidskategori(new WSArbeidskategori().withTermnavn(ARBEIDSKATEGORI))
                .withArbeidsforholdListe(new WSArbeidsforhold().withArbeidsgiverOrgnr(ORGNR));

        Pleiepengeperiode periode = mapper.map(wsPeriode);

        assertThat(periode.getArbeidsforholdListe().get(0).getArbeidskategori(), is(ARBEIDSKATEGORI));
    }

    @Test
    public void pleiepengeperiodeMappesMedAntallPleiepengedager() {
        WSPleiepengeperiode wsPeriode = new WSPleiepengeperiode()
                .withPleiepengerFom(FOM_DATO)
                .withAntallPleiepengedager(DAGER);

        Pleiepengeperiode periode = mapper.map(wsPeriode);

        assertThat(periode.getAntallPleiepengedager(), is(DAGER));
    }

    @Test
    public void arbeidsforholdMappesMedOrgnr() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold().withArbeidsgiverOrgnr(ORGNR);

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getArbeidsgiverOrgnr(), is(ORGNR));
    }

    @Test
    public void arbeidsforholdMappesMedKontonr() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR)
                .withArbeidsgiverKontonr(KONTONUMMER);

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getArbeidsgiverKontonr(), is(KONTONUMMER));
    }

    @Test
    public void arbeidsforholdMappesUtenKontonr() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR);

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getArbeidsgiverKontonr(), is(nullValue()));
    }

    @Test
    public void arbeidsforholdMappesMedInntektForPerioden() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR)
                .withInntektForPerioden(INNTEKT);

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getInntektForPerioden(), is(INNTEKT));
    }

    @Test
    public void arbeidsforholdMappesUtenInntektForPerioden() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR);

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getInntektForPerioden(), is(nullValue()));
    }

    @Test
    public void arbeidsforholdMappesMedInntektsperiode() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR)
                .withInntektsperiode(new WSInntektsperiode()
                        .withTermnavn(INNTEKTSPERIODE));

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getInntektsperiode(), is(INNTEKTSPERIODE));
    }

    @Test
    public void arbeidsforholdMappesUtenInntektsperiode() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR);

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getInntektsperiode(), is(nullValue()));
    }

    @Test
    public void arbeidsforholdMappesMedRefusjonstype() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR)
                .withRefusjonstype(new WSRefusjonstype()
                        .withKode(REFUSJONSTYPE_KODE)
                        .withTermnavn(REFUSJONSTYPE_TERMNAVN));

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getRefusjonstype(), is(REFUSJONSTYPE_TERMNAVN));
    }

    @Test
    public void arbeidsforholdMappesUtenRefusjonstype() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR);

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getRefusjonstype(), is("Ikke refusjon"));
    }

    @Test
    public void arbeidsforholdMappesMedRefusjonTOM() {
        WSArbeidsforhold wsArbeidsforhold = new WSArbeidsforhold()
                .withArbeidsgiverOrgnr(ORGNR)
                .withRefusjonTom(TOM_DATO);

        Arbeidsforhold arbeidsforhold = mapper.map(wsArbeidsforhold);

        assertThat(arbeidsforhold.getRefusjonTom(), is(notNullValue()));
        assertThat(arbeidsforhold.getRefusjonTom().getDayOfMonth(), is(TOM_DATO.getDay()));
        assertThat(arbeidsforhold.getRefusjonTom().getMonthValue(), is(TOM_DATO.getMonth()));
        assertThat(arbeidsforhold.getRefusjonTom().getYear(), is(TOM_DATO.getYear()));
    }

}
