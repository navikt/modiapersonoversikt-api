package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.utils;

import no.nav.modiapersonoversikt.utils.DateUtils;
import no.nav.personoversikt.test.snapshot.SnapshotRule;
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.YtelseskontraktMapper;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt.mock.YtelseskontraktMockFactory;
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.YtelseskontraktRequest;
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.YtelseskontraktResponse;
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.Dagpengeytelse;
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.Ytelse;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.*;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeRequest;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeResponse;
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


public class YtelseskontraktMapperTest {
    @Rule
    public SnapshotRule snapshot = new SnapshotRule();

    public static final String STATUS = "nr 2";
    public static final String AKTIVITET_FASE = "Aktivitetsfase";
    private static final LocalDate FIXED_DATE = LocalDate.parse("2020-10-13");

    @Test
    public void testMapper() throws DatatypeConfigurationException {
        YtelseskontraktMapper mapper = YtelseskontraktMapper.getInstance();

        YtelseskontraktRequest ytelseskontraktRequest = new YtelseskontraktRequest();
        ytelseskontraktRequest.setFodselsnummer("123451234");
        ytelseskontraktRequest.setFrom(FIXED_DATE);
        ytelseskontraktRequest.setTo(FIXED_DATE);

        FimHentYtelseskontraktListeRequest fimHentYtelseskontraktListeRequest = mapper.map(ytelseskontraktRequest);

        assertEquals(ytelseskontraktRequest.getFodselsnummer(), fimHentYtelseskontraktListeRequest.getPersonidentifikator());

        GregorianCalendar from = new GregorianCalendar();
        from.setTime(ytelseskontraktRequest.getFrom().toDate());
        XMLGregorianCalendar expectedFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(from);
        XMLGregorianCalendar actualFrom = fimHentYtelseskontraktListeRequest.getPeriode().getFom();
        assertEquals(expectedFrom.getDay(), actualFrom.getDay());
        assertEquals(expectedFrom.getMonth(), actualFrom.getMonth());
        assertEquals(expectedFrom.getYear(), actualFrom.getYear());

        GregorianCalendar to = new GregorianCalendar();
        to.setTime(ytelseskontraktRequest.getTo().toDate());
        XMLGregorianCalendar expectedTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(to);
        XMLGregorianCalendar actualTo = fimHentYtelseskontraktListeRequest.getPeriode().getTom();
        assertEquals(expectedTo.getDay(), actualTo.getDay());
        assertEquals(expectedTo.getMonth(), actualTo.getMonth());
        assertEquals(expectedTo.getYear(), actualTo.getYear());

        FimHentYtelseskontraktListeResponse fimHentYtelseskontraktListeResponse = new FimHentYtelseskontraktListeResponse();
        fimHentYtelseskontraktListeResponse.setBruker(new FimBruker().withRettighetsgruppe(new FimRettighetsgruppe().withRettighetsGruppe("Rettighetsgruppe:test")));
        FimDagpengekontrakt dagpengekontrakt = new FimDagpengekontrakt();
        LocalDate datoKravMottat = new LocalDate();
        dagpengekontrakt.setDatoKravMottatt(DateUtils.convertDateToXmlGregorianCalendar(datoKravMottat.toDate()));
        int dagerIgjen = 2;
        dagpengekontrakt.setAntallDagerIgjen(dagerIgjen);
        int ukerIgjen = 3;
        dagpengekontrakt.setAntallUkerIgjen(ukerIgjen);
        int dagerIgjenPermittering = 10;
        dagpengekontrakt.setAntallDagerIgjenUnderPermittering(dagerIgjenPermittering);
        int ukerIgjenPermittering = 2;
        dagpengekontrakt.setAntallUkerIgjenUnderPermittering(ukerIgjenPermittering);

        XMLGregorianCalendar fomGyldighetsperiode = createDate(1, 10, 2001);
        dagpengekontrakt.setFomGyldighetsperiode(fomGyldighetsperiode);
        XMLGregorianCalendar tomGyldighetsperiode = createDate(1, 12, 2001);
        dagpengekontrakt.setTomGyldighetsperiode(tomGyldighetsperiode);
        List<FimVedtak> vedtak = dagpengekontrakt.getIhtVedtak();
        vedtak.addAll(createVedtak());
        fimHentYtelseskontraktListeResponse.getYtelseskontraktListe().add(dagpengekontrakt);

        FimYtelseskontrakt kontrakt = new FimYtelseskontrakt();
        List<FimVedtak> vedtakList = createVedtak();
        vedtakList.get(0).setStatus(STATUS);
        kontrakt.getIhtVedtak().addAll(vedtakList);
        int bortfallsprosentDagerIgjen = 15;
        int bortfallsprosentUkerIgjen = 3;
        kontrakt.setBortfallsprosentDagerIgjen(bortfallsprosentDagerIgjen);
        kontrakt.setBortfallsprosentUkerIgjen(bortfallsprosentUkerIgjen);
        fimHentYtelseskontraktListeResponse.getYtelseskontraktListe().add(kontrakt);

        YtelseskontraktResponse ytelseskontraktResponse = mapper.map(fimHentYtelseskontraktListeResponse);

        assertEquals(fimHentYtelseskontraktListeResponse.getBruker().getRettighetsgruppe().getRettighetsGruppe(), ytelseskontraktResponse.getRettighetsgruppe());
        Dagpengeytelse dagpengeytelse = (Dagpengeytelse) ytelseskontraktResponse.getYtelser().get(0);
        assertEquals(Integer.valueOf(dagerIgjen), dagpengeytelse.getAntallDagerIgjen());
        assertEquals(Integer.valueOf(ukerIgjen), dagpengeytelse.getAntallUkerIgjen());
        assertEquals(Integer.valueOf(dagerIgjenPermittering), dagpengeytelse.getAntallDagerIgjenPermittering());
        assertEquals(Integer.valueOf(ukerIgjenPermittering), dagpengeytelse.getAntallUkerIgjenPermittering());
        assertEquals(datoKravMottat, ytelseskontraktResponse.getYtelser().get(0).getDatoKravMottat());
        DatatypeFactory.newInstance().newXMLGregorianCalendar(to);
        assertEquals(fomGyldighetsperiode, toXMLGregorian(dagpengeytelse.getFom()));
        assertEquals(tomGyldighetsperiode, toXMLGregorian(dagpengeytelse.getTom()));

        Ytelse ytelse = ytelseskontraktResponse.getYtelser().get(1);
        assertEquals(STATUS, (ytelse.getVedtak().get(0).getVedtakstatus()));
        assertEquals(AKTIVITET_FASE, (ytelse.getVedtak().get(0).getAktivitetsfase()));
        assertEquals(Integer.valueOf(bortfallsprosentDagerIgjen), (ytelse.getDagerIgjenMedBortfall()));
        assertEquals(Integer.valueOf(bortfallsprosentUkerIgjen), (ytelse.getUkerIgjenMedBortfall()));
        snapshot.assertMatches(fimHentYtelseskontraktListeRequest);
    }

    /**
     * Test for å sjekke at mocken ikke tryner
     */
    @Test
    public void testWithMock() {
        YtelseskontraktMapper mapper = YtelseskontraktMapper.getInstance();
        FimYtelseskontrakt ytelsesKontrakt = YtelseskontraktMockFactory.createYtelsesKontrakt();
        FimHentYtelseskontraktListeResponse fimResponse = new FimHentYtelseskontraktListeResponse();
        fimResponse.getYtelseskontraktListe().add(ytelsesKontrakt);
        YtelseskontraktResponse response = new YtelseskontraktResponse();
        Ytelse ytelse = mapper.mapYtelse(ytelsesKontrakt);

        snapshot.assertMatches(response);
        snapshot.assertMatches(ytelse);
    }

    private List<FimVedtak> createVedtak() {
        List<FimVedtak> fimVedtak = new ArrayList<>();
        FimVedtak vedtak = new FimVedtak();
        vedtak.setBeslutningsdato(createDate(10, 2, 2012));
        vedtak.setStatus("vedtakskontraktstatus");
        FimPeriode periode = new FimPeriode();
        periode.setFom(createDate(1, 12, 2000));
        periode.setTom(createDate(1, 12, 2001));
        vedtak.setVedtaksperiode(periode);
        vedtak.setVedtakstype("vedtakstype");
        vedtak.setAktivitetsfase(AKTIVITET_FASE);
        fimVedtak.add(vedtak);

        return fimVedtak;
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
