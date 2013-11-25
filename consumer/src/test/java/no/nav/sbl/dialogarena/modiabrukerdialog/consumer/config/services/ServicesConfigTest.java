package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
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
@ContextConfiguration(classes = {ServicesConfig.class, EndpointsConfig.class})
public class ServicesConfigTest {
    @BeforeClass
    public static void setupStatic() {
        setFrom("environment-local.properties");
        setupKeyAndTrustStore();
    }

    @Inject
    private UtbetalingService utbetalingService;

    @Test
    public void shouldSetupAppContext() {
        assertThat(utbetalingService, is(notNullValue()));
    }

}
