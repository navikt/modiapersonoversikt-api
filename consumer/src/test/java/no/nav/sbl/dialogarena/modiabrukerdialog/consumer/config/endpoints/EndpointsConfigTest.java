package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.TestBeans;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
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
    private BesvareHenvendelsePortType besvareHenvendelsePortType;
    @Inject
    private HenvendelseMeldingerPortType henvendelseMeldingerPortType;
    @Inject
    private OppgavebehandlingPortType oppgavebehandlingPortType;
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
    @Named("besvareHenvendelsePing")
    private Pingable besvareHenvendelsePing;
    @Inject
    @Named("utbetalingPing")
    private Pingable utbetalingPing;
    @Inject
    @Named("henvendelsePing")
    private Pingable henvendelsePing;
    @Inject
    @Named("oppgavebehandlingPing")
    private Pingable oppgavebehandlingPing;

    @BeforeClass
    public static void setupStatic() {
        setFrom("environment-local.properties");
        setupKeyAndTrustStore();
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void shouldHavePortTypes() {
        assertThat(aktoerPortType, is(notNullValue()));
        assertThat(besvareHenvendelsePortType, is(notNullValue()));
        assertThat(henvendelseMeldingerPortType, is(notNullValue()));
        assertThat(oppgavebehandlingPortType, is(notNullValue()));
        assertThat(sakOgBehandlingPortType, is(notNullValue()));
        assertThat(utbetalingPortType, is(notNullValue()));
        assertThat(kodeverkPortType, is(notNullValue()));
    }

    @Test
    public void shouldHavePingPortTypes() {
        assertThat(aktorIdPing, is(notNullValue()));
        assertThat(besvareHenvendelsePing, is(notNullValue()));
        assertThat(utbetalingPing, is(notNullValue()));
        assertThat(henvendelsePing, is(notNullValue()));
        assertThat(oppgavebehandlingPing, is(notNullValue()));

    }


}
