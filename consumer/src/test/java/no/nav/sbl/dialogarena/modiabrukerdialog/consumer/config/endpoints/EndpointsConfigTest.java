package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

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

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EndpointsConfig.class})
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

    @BeforeClass
    public static void setupStatic() {
        setFrom("environment-local.properties");
        setupKeyAndTrustStore();
    }

    @Test
    public void shouldSetupAppContext() {
        assertThat(aktoerPortType, is(notNullValue()));
        assertThat(besvareHenvendelsePortType, is(notNullValue()));
        assertThat(henvendelseMeldingerPortType, is(notNullValue()));
        assertThat(oppgavebehandlingPortType, is(notNullValue()));
        assertThat(sakOgBehandlingPortType, is(notNullValue()));
        assertThat(utbetalingPortType, is(notNullValue()));
        assertThat(kodeverkPortType, is(notNullValue()));
    }


}
