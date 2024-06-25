package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.utils;

import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktMapper;
import no.nav.personoversikt.common.test.snapshot.SnapshotRule;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.mock.OppfolgingkontraktMockFactory;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktRequest;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktResponse;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.Bruker;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class OppfolgingskontraktMapperTest {
    @Rule
    public SnapshotRule snapshot = new SnapshotRule();
    private static final LocalDate FIXED_DATE = LocalDate.parse("2020-10-13");

    @Test
    public void testRequestMapping() throws DatatypeConfigurationException {
        OppfolgingskontraktMapper mapper = OppfolgingskontraktMapper.getInstance();

        OppfolgingskontraktRequest oppfolgingskontraktRequest = new OppfolgingskontraktRequest();
        oppfolgingskontraktRequest.setFodselsnummer("11223344455");
        oppfolgingskontraktRequest.setFrom(FIXED_DATE);
        oppfolgingskontraktRequest.setTo(FIXED_DATE);
        WSHentOppfoelgingskontraktListeRequest fimHentOppfolgingskontraktListeRequest = mapper.map(oppfolgingskontraktRequest);

        assertEquals(oppfolgingskontraktRequest.getFodselsnummer(), fimHentOppfolgingskontraktListeRequest.getPersonidentifikator());

        GregorianCalendar from = new GregorianCalendar();
        from.setTime(oppfolgingskontraktRequest.getFrom().toDate());
        XMLGregorianCalendar expectedFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(from);
        XMLGregorianCalendar actualFrom = fimHentOppfolgingskontraktListeRequest.getPeriode().getFom();
        assertEquals(expectedFrom.getDay(), actualFrom.getDay());
        assertEquals(expectedFrom.getMonth(), actualFrom.getMonth());
        assertEquals(expectedFrom.getYear(), actualFrom.getYear());

        GregorianCalendar to = new GregorianCalendar();
        to.setTime(oppfolgingskontraktRequest.getTo().toDate());
        XMLGregorianCalendar expectedTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(to);
        XMLGregorianCalendar actualTo = fimHentOppfolgingskontraktListeRequest.getPeriode().getTom();
        assertEquals(expectedTo.getDay(), actualTo.getDay());
        assertEquals(expectedTo.getMonth(), actualTo.getMonth());
        assertEquals(expectedTo.getYear(), actualTo.getYear());
        snapshot.assertMatches(fimHentOppfolgingskontraktListeRequest);
    }

    @Test
    public void testResponseMapping() {
        OppfolgingskontraktMapper mapper = OppfolgingskontraktMapper.getInstance();

        WSHentOppfoelgingskontraktListeResponse fimResponse = new WSHentOppfoelgingskontraktListeResponse();
        List<WSSYFOkontrakt> kontraktliste = new ArrayList<>();
        WSBruker bruker = createFimOppBruker();
        List<WSYtelseskontrakt> ytelser = createYtelse();
        List<WSSYFOPunkt> syfoPunkter = createSyfoPunkter();
        WSSYFOkontrakt kontrakt = new WSSYFOkontrakt();
        kontrakt.setIhtGjeldendeVedtak(createVedtak());
        kontrakt.getAvYtelse().addAll(ytelser);
        kontrakt.setGjelderBruker(bruker);
        kontrakt.setStatus("kontraktstatus");
        kontrakt.setSykmeldtFra(createDate(10, 10, 2012));
        kontrakt.getHarSYFOPunkt().addAll(syfoPunkter);
        kontraktliste.add(kontrakt);

        fimResponse.getOppfoelgingskontraktListe().addAll(kontraktliste);

        OppfolgingskontraktResponse oppfolgingskontraktResponse = mapper.map(fimResponse);

        for (WSSYFOkontrakt syfokontrakt : kontraktliste) {
            checkBruker(syfokontrakt, bruker, oppfolgingskontraktResponse);
        }
        checkSyfoPunkter(syfoPunkter, oppfolgingskontraktResponse);
        snapshot.assertMatches(oppfolgingskontraktResponse);
    }

    /**
     * Test for å sjekke at mocken ikke tryner
     */
    @Test
    public void testWithMock() {
        OppfolgingskontraktMapper mapper = OppfolgingskontraktMapper.getInstance();
        WSOppfoelgingskontrakt kontrakt = OppfolgingkontraktMockFactory.createOppfoelgingskontrakt();
        WSHentOppfoelgingskontraktListeResponse fimResponse = new WSHentOppfoelgingskontraktListeResponse();
        fimResponse.getOppfoelgingskontraktListe().add(kontrakt);
        OppfolgingskontraktResponse response = mapper.map(fimResponse);

        snapshot.assertMatches(response);
    }

    @Test
    public void modfeil1455() {
        OppfolgingskontraktMapper mapper = OppfolgingskontraktMapper.getInstance();
        final String formidlingsgruppe = "Arbeidsøker";
        final String servicegruppeVerdi = "Spesielt tilpasset innsats";
        final boolean meldepliktVerdi = true;
        WSBruker gjelderBruker = new WSBruker();

        gjelderBruker.setFormidlingsgruppe(formidlingsgruppe);
        WSMeldeplikt meldeplikt = new WSMeldeplikt();
        meldeplikt.setMeldeplikt(meldepliktVerdi);
        gjelderBruker.getMeldeplikt().add(meldeplikt);

        WSServiceGruppe serviceGruppe = new WSServiceGruppe();
        serviceGruppe.setServiceGruppe(servicegruppeVerdi);
        gjelderBruker.getServicegruppe().add(serviceGruppe);

        WSOppfoelgingskontrakt kontrakt1 = new WSOppfoelgingskontrakt();
        kontrakt1.setStatus("Aktiv");
        kontrakt1.setGjelderBruker(gjelderBruker);
        WSOppfoelgingskontrakt kontrakt2 = new WSOppfoelgingskontrakt();
        kontrakt2.setStatus("Inaktiv");
        kontrakt2.setGjelderBruker(gjelderBruker);

        List<WSOppfoelgingskontrakt> fimOppOppfoelgingskontraktList = new ArrayList<>();
        fimOppOppfoelgingskontraktList.add(kontrakt1);
        fimOppOppfoelgingskontraktList.add(kontrakt2);
        WSHentOppfoelgingskontraktListeResponse from = new WSHentOppfoelgingskontraktListeResponse();
        from.getOppfoelgingskontraktListe().addAll(fimOppOppfoelgingskontraktList);

        OppfolgingskontraktResponse to = mapper.map(from);

        Bruker bruker = to.getBruker();
        assertEquals(formidlingsgruppe, bruker.getFormidlingsgruppe());
        assertEquals(servicegruppeVerdi, bruker.getInnsatsgruppe());
        assertEquals(meldepliktVerdi, bruker.getMeldeplikt());
        snapshot.assertMatches(to);
    }

    private void checkBruker(WSSYFOkontrakt kontrakt, WSBruker bruker, OppfolgingskontraktResponse oppfolgingskontraktResponse) {
        assertEquals(bruker.getFormidlingsgruppe(), oppfolgingskontraktResponse.getBruker().getFormidlingsgruppe());
        assertEquals(bruker.getServicegruppe().get(0).getServiceGruppe(), oppfolgingskontraktResponse.getBruker().getInnsatsgruppe());
        assertEquals(bruker.getMeldeplikt().get(0).isMeldeplikt(), oppfolgingskontraktResponse.getBruker().getMeldeplikt());
        assertEquals(kontrakt.getSykmeldtFra(), toXMLGregorian(oppfolgingskontraktResponse.getBruker().getSykmeldtFrom()));
    }

    private void checkSyfoPunkter(List<WSSYFOPunkt> syfoPunkter, OppfolgingskontraktResponse oppfolgingskontraktResponse) {
        assertEquals(syfoPunkter.get(0).getStatus(), oppfolgingskontraktResponse.getSyfoPunkter().get(0).getStatus());
        assertEquals(syfoPunkter.get(0).getSYFOHendelse(), oppfolgingskontraktResponse.getSyfoPunkter().get(0).getSyfoHendelse());
        assertEquals(syfoPunkter.get(0).getDato(), toXMLGregorian(oppfolgingskontraktResponse.getSyfoPunkter().get(0).getDato()));
    }

    private List<WSSYFOPunkt> createSyfoPunkter() {
        List<WSSYFOPunkt> list = new ArrayList<>();
        WSSYFOPunkt syfoPunkt = new WSSYFOPunkt();
        list.add(syfoPunkt);
        syfoPunkt.setDato(createDate(5, 2, 2011));
        syfoPunkt.setFastOppfoelgingspunkt(true);
        syfoPunkt.setStatus("syfoPunktStatus");
        syfoPunkt.setSYFOHendelse("syfoHendelse");
        return list;
    }

    private WSBruker createFimOppBruker() {
        WSBruker fimBruker = new WSBruker();
        fimBruker.setFormidlingsgruppe("1222");
        WSMeldeplikt fimMeldeplikt = new WSMeldeplikt();
        fimMeldeplikt.setMeldeplikt(true);
        fimBruker.getMeldeplikt().add(fimMeldeplikt);
        WSServiceGruppe servicegruppe = new WSServiceGruppe();
        servicegruppe.setServiceGruppe("servicegruppe");
        fimBruker.getServicegruppe().add(servicegruppe);
        return fimBruker;
    }

    private List<WSYtelseskontrakt> createYtelse() {
        List<WSYtelseskontrakt> fimYtelser = new ArrayList<>();
        WSYtelseskontrakt ytelseskontrakt = new WSYtelseskontrakt();
        ytelseskontrakt.setYtelsestype("Ytelsestype");
        ytelseskontrakt.setStatus("Ytelsesstatus");
        ytelseskontrakt.setDatoKravMottatt(createDate(2, 2, 2002));

        fimYtelser.add(ytelseskontrakt);
        return fimYtelser;
    }

    private WSVedtak createVedtak() {
        WSVedtak vedtak = new WSVedtak();
        WSYtelseskontrakt kontrakt = new WSYtelseskontrakt();
        kontrakt.setDatoKravMottatt(createDate(10, 2, 2012));
        kontrakt.setStatus("vedtakskontraktstatus");
        kontrakt.setYtelsestype("ytelsestype fra vedtak");
        vedtak.setOmYtelse(kontrakt);
        WSPeriode periode = new WSPeriode();
        periode.setFom(createDate(1, 12, 2000));
        periode.setTom(createDate(1, 12, 2001));
        vedtak.setVedtaksperiode(periode);
        vedtak.setStatus("vedtaksstatus");
        vedtak.setVedtakstype("vedtakstype");
        return vedtak;
    }

    private XMLGregorianCalendar createDate(int day, int month, int year) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(year, month, day, 0);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Klarer ikke å lage dato", e);
        }
    }

    private XMLGregorianCalendar toXMLGregorian(LocalDate date) {

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 0);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Klarer ikke å lage dato", e);
        }
    }
}
