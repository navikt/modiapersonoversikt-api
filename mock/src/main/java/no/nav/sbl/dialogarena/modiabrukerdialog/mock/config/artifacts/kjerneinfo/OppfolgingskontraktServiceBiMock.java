package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kjerneinfo.common.mockutils.DateUtils;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.kontrakter.domain.oppfolging.SYFOPunkt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static no.nav.kjerneinfo.common.mockutils.DateUtils.convertDateToXmlGregorianCalendar;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getRandomDatePair;
import static no.nav.kontrakter.consumer.fim.ytelseskontrakt.mock.YtelseskontraktMockFactory.YTELSESSTATUS_AKTIV;
import static org.joda.time.LocalDate.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OppfolgingskontraktServiceBiMock {

    public static OppfolgingskontraktServiceBi getOppfolgingskontraktServiceBiMock() {
        OppfolgingskontraktServiceBi mock = mock(OppfolgingskontraktServiceBi.class);
        when(mock.hentOppfolgingskontrakter(any(OppfolgingskontraktRequest.class))).thenReturn(lagOppfolgingsMockRespons());
        return mock;
    }

    private static OppfolgingskontraktResponse lagOppfolgingsMockRespons() {
        WSHentOppfoelgingskontraktListeResponse respons = new WSHentOppfoelgingskontraktListeResponse();
        respons.getOppfoelgingskontraktListe().add(createOppfoelgingskontrakt());
        OppfolgingskontraktResponse returRespons = OppfolgingskontraktMapper.getInstance().map(respons, OppfolgingskontraktResponse.class);
        returRespons.setSyfoPunkter(createSYFOpunkter());
        return returRespons;
    }

    private static List<SYFOPunkt> createSYFOpunkter() {
        List<SYFOPunkt> syfoPunkter = new ArrayList<>();
        SYFOPunkt syfoPunkt = new SYFOPunkt();
        syfoPunkt.setSyfoHendelse("Mottatt rapport");
        syfoPunkt.setStatus("Godkjent");
        syfoPunkt.setDato(now().minusMonths(1));
        syfoPunkter.add(syfoPunkt);
        syfoPunkter.add(syfoPunkt);
        return syfoPunkter;
    }

    private static WSOppfoelgingskontrakt createOppfoelgingskontrakt() {
        WSOppfoelgingskontrakt oppfoelgingskontrakt = new WSOppfoelgingskontrakt();
        oppfoelgingskontrakt.setGjelderBruker(createBruker());
        oppfoelgingskontrakt.setStatus("Aktiv");
        oppfoelgingskontrakt.setIhtGjeldendeVedtak(createVedtak(null, null, null, null));
        oppfoelgingskontrakt.getAvYtelse().add(createYtelseskontrakt(null, null, DateUtils.getRandomDate()));

        return oppfoelgingskontrakt;
    }

    private static WSPeriode createPeriode(Date fom, Date tom) {
        WSPeriode periode = new WSPeriode();
        periode.setFom(convertDateToXmlGregorianCalendar(fom));
        periode.setTom(convertDateToXmlGregorianCalendar(tom));
        return periode;
    }

    private static WSPeriode createRandomPeriode() {
        Date[] datePair = getRandomDatePair();
        return createPeriode(datePair[0], datePair[1]);
    }

    private static WSYtelseskontrakt createYtelseskontrakt(String status, String type, Date datoKravMottat) {
        WSYtelseskontrakt ytelseskontrakt = new WSYtelseskontrakt();
        ytelseskontrakt.setDatoKravMottatt(datoKravMottat == null ? convertDateToXmlGregorianCalendar(new Date()) : convertDateToXmlGregorianCalendar(datoKravMottat));
        ytelseskontrakt.setStatus(status == null ? "YtelseskontraktStatus" : status);
        ytelseskontrakt.setYtelsestype(type == null ? "Ytelsestype" : type);
        return ytelseskontrakt;
    }

    private static WSVedtak createVedtak(String status, Date fra, Date til, Date datoKravMottatt) {
        WSVedtak vedtak = new WSVedtak();
        vedtak.setStatus(status == null ? YTELSESSTATUS_AKTIV : status);
        vedtak.setVedtaksperiode(fra == null ? createRandomPeriode() : createPeriode(fra, til));
        vedtak.setOmYtelse(datoKravMottatt == null ? createYtelseskontrakt(null, null, new Date()) : createYtelseskontrakt(null, null, datoKravMottatt));
        return vedtak;
    }

    private static WSBruker createBruker() {
        WSBruker bruker = new WSBruker();
        bruker.setFormidlingsgruppe("50000");
        bruker.getServicegruppe().add(createServicegruppe());
        bruker.getMeldeplikt().add(createMeldeplikt());
        return bruker;
    }

    private static WSMeldeplikt createMeldeplikt() {
        WSMeldeplikt meldeplikt = new WSMeldeplikt();
        meldeplikt.setMeldeplikt(true);
        return meldeplikt;
    }

    private static WSServiceGruppe createServicegruppe() {
        WSServiceGruppe serviceGruppe = new WSServiceGruppe();
        serviceGruppe.setServiceGruppe("Servicegruppe");
        return serviceGruppe;
    }
}
