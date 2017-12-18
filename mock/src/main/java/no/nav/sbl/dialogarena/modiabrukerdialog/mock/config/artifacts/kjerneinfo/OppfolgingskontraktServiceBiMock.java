package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kjerneinfo.common.mockutils.DateUtils;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.kontrakter.domain.oppfolging.SYFOPunkt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.HentOppfoelgingskontraktListeResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static no.nav.kjerneinfo.common.mockutils.DateUtils.convertDateToXmlGregorianCalendar;
import static no.nav.kjerneinfo.common.mockutils.DateUtils.getRandomDatePair;
import static no.nav.kontrakter.consumer.fim.ytelseskontrakt.mock.YtelseskontraktMockFactory.YTELSESSTATUS_AKTIV;
import static org.joda.time.LocalDate.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OppfolgingskontraktServiceBiMock {

    public static OppfolgingskontraktServiceBi getOppfolgingskontraktServiceBiMock() {
        OppfolgingskontraktServiceBi mock = mock(OppfolgingskontraktServiceBi.class);
        when(mock.hentOppfolgingskontrakter(any(OppfolgingskontraktRequest.class))).thenReturn(lagOppfolgingsMockRespons());
        return mock;
    }

    private static OppfolgingskontraktResponse lagOppfolgingsMockRespons() {
        HentOppfoelgingskontraktListeResponse respons = new HentOppfoelgingskontraktListeResponse();
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

    private static Oppfoelgingskontrakt createOppfoelgingskontrakt() {
        Oppfoelgingskontrakt oppfoelgingskontrakt = new Oppfoelgingskontrakt();
        oppfoelgingskontrakt.setGjelderBruker(createBruker());
        oppfoelgingskontrakt.setStatus("Aktiv");
        oppfoelgingskontrakt.setIhtGjeldendeVedtak(createVedtak(null, null, null, null));
        oppfoelgingskontrakt.getAvYtelse().add(createYtelseskontrakt(null, null, DateUtils.getRandomDate()));

        return oppfoelgingskontrakt;
    }

    private static Periode createPeriode(Date fom, Date tom) {
        Periode periode = new Periode();
        periode.setFom(convertDateToXmlGregorianCalendar(fom));
        periode.setTom(convertDateToXmlGregorianCalendar(tom));
        return periode;
    }

    private static Periode createRandomPeriode() {
        Date[] datePair = getRandomDatePair();
        return createPeriode(datePair[0], datePair[1]);
    }

    private static Ytelseskontrakt createYtelseskontrakt(String status, String type, Date datoKravMottat) {
        Ytelseskontrakt ytelseskontrakt = new Ytelseskontrakt();
        ytelseskontrakt.setDatoKravMottatt(datoKravMottat == null ? convertDateToXmlGregorianCalendar(new Date()) : convertDateToXmlGregorianCalendar(datoKravMottat));
        ytelseskontrakt.setStatus(status == null ? "YtelseskontraktStatus" : status);
        ytelseskontrakt.setYtelsestype(type == null ? "Ytelsestype" : type);
        return ytelseskontrakt;
    }

    private static Vedtak createVedtak(String status, Date fra, Date til, Date datoKravMottatt) {
        Vedtak vedtak = new Vedtak();
        vedtak.setStatus(status == null ? YTELSESSTATUS_AKTIV : status);
        vedtak.setVedtaksperiode(fra == null ? createRandomPeriode() : createPeriode(fra, til));
        vedtak.setOmYtelse(datoKravMottatt == null ? createYtelseskontrakt(null, null, new Date()) : createYtelseskontrakt(null, null, datoKravMottatt));
        return vedtak;
    }

    private static Bruker createBruker() {
        Bruker bruker = new Bruker();
        bruker.setFormidlingsgruppe("50000");
        bruker.getServicegruppe().add(createServicegruppe());
        bruker.getMeldeplikt().add(createMeldeplikt());
        return bruker;
    }

    private static Meldeplikt createMeldeplikt() {
        Meldeplikt meldeplikt = new Meldeplikt();
        meldeplikt.setMeldeplikt(true);
        return meldeplikt;
    }

    private static ServiceGruppe createServicegruppe() {
        ServiceGruppe serviceGruppe = new ServiceGruppe();
        serviceGruppe.setServiceGruppe("Servicegruppe");
        return serviceGruppe;
    }
}
