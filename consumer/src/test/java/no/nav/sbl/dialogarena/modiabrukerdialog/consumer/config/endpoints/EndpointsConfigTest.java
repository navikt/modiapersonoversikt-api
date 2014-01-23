package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.TestBeans;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;

import static java.lang.System.setProperty;
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
    private UtbetalingPortType utbetalingPortType;
    @Inject
    private KodeverkPortType kodeverkPortType;
    @Inject
    @Named("utbetalingPing")
    private Pingable utbetalingPing;


    @BeforeClass
    public static void setupStatic() {
        setupKeyAndTrustStore();
        setFrom("test.properties");
        setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void shouldHavePortTypes() {
        assertThat(utbetalingPortType, is(notNullValue()));
        assertThat(kodeverkPortType, is(notNullValue()));
    }

    @Test
    public void shouldHavePingPortTypes() {
        assertThat(utbetalingPing.ping().size(), is(equalTo(1)));
    }


}
