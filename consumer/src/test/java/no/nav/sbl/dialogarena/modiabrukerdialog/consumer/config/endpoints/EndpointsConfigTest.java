package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.TestBeans;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingHentBehandlingBehandlingIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EndpointsConfig.class, TestBeans.class})
public class EndpointsConfigTest {
    @Inject
    private AktoerPortType aktoerPortType;
    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;
    @Inject
    private UtbetalingPortType utbetalingPortType;
    @Inject
    private KodeverkPortType kodeverkPortType;
    @Inject
    @Named("aktorIdPing")
    private Pingable aktorIdPing;
    @Inject
    @Named("utbetalingPing")
    private Pingable utbetalingPing;

    @BeforeClass
    public static void setupStatic() {
        setFrom("environment-local.properties");
        setFrom("start_test.properties");
        setupKeyAndTrustStore();
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void shouldHavePortTypes() {
        assertThat(aktoerPortType, is(notNullValue()));
        assertThat(sakOgBehandlingPortType, is(notNullValue()));
        assertThat(utbetalingPortType, is(notNullValue()));
        assertThat(kodeverkPortType, is(notNullValue()));
    }

    @Test
    public void sakOgBehandlingPortType_finnSakOgBehandlingskjedeListe(){
        FinnSakOgBehandlingskjedeListeRequest request = new FinnSakOgBehandlingskjedeListeRequest();
        request.setAktoerREF("1234");
        assertThat(sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(request), is(notNullValue()));
    }

    @Test
    public void sakOgBehandlingPortType_hentBehandlingskjedensBehandlinger() throws HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
        HentBehandlingskjedensBehandlingerRequest  request = new HentBehandlingskjedensBehandlingerRequest();
        request.setBehandlingskjedeREF("");
        assertThat(sakOgBehandlingPortType.hentBehandlingskjedensBehandlinger(request), is(notNullValue()));
    }
    @Test
    public void hentBehandling() throws HentBehandlingHentBehandlingBehandlingIkkeFunnet {
        HentBehandlingRequest  request = new HentBehandlingRequest();
        request.setBehandlingsREF("");
        assertThat(sakOgBehandlingPortType.hentBehandling(request), is(notNullValue()));
    }
    @Test
    public void sakOgBehandlingPortType_ping(){
        sakOgBehandlingPortType.ping();
    }


    @Test
    public void shouldHavePingPortTypes() {
        assertThat(aktorIdPing.ping().size(), is(equalTo(1)));
        assertThat(utbetalingPing.ping().size(), is(equalTo(1)));
    }


}
