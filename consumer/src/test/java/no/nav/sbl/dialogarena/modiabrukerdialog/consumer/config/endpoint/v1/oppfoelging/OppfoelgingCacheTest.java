package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.oppfoelging;

import no.nav.kontrakter.consumer.fim.config.OppfolgingskontraktConsumerConfig;
import no.nav.metrics.proxy.MetricProxy;
import no.nav.metrics.proxy.TimerProxy;
import no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.Oppfoelgingskontrakt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.HentOppfoelgingskontraktListeRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.HentOppfoelgingskontraktListeResponse;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.HentOppfoelgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.HentOppfoelgingsstatusResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import static java.lang.System.setProperty;
import static no.nav.kontrakter.consumer.fim.config.OppfolgingskontraktConsumerConfig.MOCK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OppfolgingskontraktConsumerConfig.class})
public class OppfoelgingCacheTest extends CacheTest {

    private static final String OPPFOELGING_CACHE = "oppfolgingCache";
    private static final String FODSELSNUMMER_1 = "10108000398";
    private static final String FODSELSNUMMER_2 = "06128074978";
    public static final String OPPFOELGINGSENHET_1 = "0118";
    public static final String OPPFOELGINGSENHET_2 = "0119";

    public OppfoelgingCacheTest() {
        super(OPPFOELGING_CACHE);
    }

    @Inject
    private OppfoelgingPortType oppfolgingPortType;

    @BeforeClass
    public static void setUp() {
        setProperty(MOCK_KEY, "true");
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
    }

    @Before
    public void setUpMock() throws Exception {
        OppfoelgingPortType unwrapped = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        reset(unwrapped);

        HentOppfoelgingsstatusResponse response1 = new HentOppfoelgingsstatusResponse();
        response1.setNavOppfoelgingsenhet(OPPFOELGINGSENHET_1);
        HentOppfoelgingsstatusResponse response2 = new HentOppfoelgingsstatusResponse();
        response2.setNavOppfoelgingsenhet(OPPFOELGINGSENHET_2);

        when(unwrapped.hentOppfoelgingsstatus(any(HentOppfoelgingsstatusRequest.class)))
                .thenReturn(response1, response2);

        HentOppfoelgingskontraktListeResponse response3 = new HentOppfoelgingskontraktListeResponse();
        response3.getOppfoelgingskontraktListe().add(new Oppfoelgingskontrakt());
        HentOppfoelgingskontraktListeResponse response4 = new HentOppfoelgingskontraktListeResponse();
        response4.getOppfoelgingskontraktListe().add(new Oppfoelgingskontrakt());
        response4.getOppfoelgingskontraktListe().add(new Oppfoelgingskontrakt());

        when(unwrapped.hentOppfoelgingskontraktListe(any(HentOppfoelgingskontraktListeRequest.class)))
                .thenReturn(response3, response4);
    }

    @Test
    public void toKallTilHentOppfoelgingsstatusMedSammeIdentGirBareEttTjenestekall() throws Exception {
        HentOppfoelgingsstatusRequest request1 = new HentOppfoelgingsstatusRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_1);

        HentOppfoelgingsstatusResponse response1 = oppfolgingPortType.hentOppfoelgingsstatus(request1);
        HentOppfoelgingsstatusResponse response2 = oppfolgingPortType.hentOppfoelgingsstatus(request1);

        OppfoelgingPortType unwrappedOppfolgingsinfo = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        verify(unwrappedOppfolgingsinfo, times(1)).hentOppfoelgingsstatus(any());

        assertThat(response1.getNavOppfoelgingsenhet(), is(response2.getNavOppfoelgingsenhet()));
    }

    @Test
    public void toKallTilHentOppfoelgingsstatusMedForskjelligeIdenterGirToTjenestekall() throws Exception {
        HentOppfoelgingsstatusRequest request1 = new HentOppfoelgingsstatusRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_1);
        HentOppfoelgingsstatusRequest request2 = new HentOppfoelgingsstatusRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_2);

        HentOppfoelgingsstatusResponse response1 = oppfolgingPortType.hentOppfoelgingsstatus(request1);
        HentOppfoelgingsstatusResponse response2 = oppfolgingPortType.hentOppfoelgingsstatus(request2);

        OppfoelgingPortType unwrappedOppfolgingsinfo = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        verify(unwrappedOppfolgingsinfo, times(2)).hentOppfoelgingsstatus(any());

        assertThat(response1.getNavOppfoelgingsenhet(), is(not(response2.getNavOppfoelgingsenhet())));
    }

    @Test
    public void toKallTilHentOppfoelgingskontraktListeMedSammeIdentGirBareEttTjenestekall() throws Exception {
        HentOppfoelgingskontraktListeRequest request1 = new HentOppfoelgingskontraktListeRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_1);

        HentOppfoelgingskontraktListeResponse response1 = oppfolgingPortType.hentOppfoelgingskontraktListe(request1);
        HentOppfoelgingskontraktListeResponse response2 = oppfolgingPortType.hentOppfoelgingskontraktListe(request1);

        OppfoelgingPortType unwrappedOppfolgingsinfo = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        verify(unwrappedOppfolgingsinfo, times(1)).hentOppfoelgingskontraktListe(any());

        assertThat(response1.getOppfoelgingskontraktListe(), is(response2.getOppfoelgingskontraktListe()));
    }

    @Test
    public void toKallTilHentOppfoelgingskontraktListeMedForskjelligeIdenterGirToTjenestekall() throws Exception {
        HentOppfoelgingskontraktListeRequest request1 = new HentOppfoelgingskontraktListeRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_1);
        HentOppfoelgingskontraktListeRequest request2 = new HentOppfoelgingskontraktListeRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_2);

        HentOppfoelgingskontraktListeResponse response1 = oppfolgingPortType.hentOppfoelgingskontraktListe(request1);
        HentOppfoelgingskontraktListeResponse response2 = oppfolgingPortType.hentOppfoelgingskontraktListe(request2);

        OppfoelgingPortType unwrappedOppfolgingsinfo = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        verify(unwrappedOppfolgingsinfo, times(2)).hentOppfoelgingskontraktListe(any());

        assertThat(response1.getOppfoelgingskontraktListe(), is(not(response2.getOppfoelgingskontraktListe())));
    }

}
