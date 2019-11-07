package no.nav.sbl.dialogarena.utbetaling.lamell.context;

import no.nav.sbl.dialogarena.utbetaling.config.ApplicationTestConfig;
import no.nav.sbl.dialogarena.utbetaling.config.UtbetalingPortTypeTestConfig;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ApplicationTestConfig.class,
        UtbetalingLamellContext.class,
        UtbetalingPortTypeTestConfig.class})
public class UtbetalingLamellContextTest {

    @Inject
    @Named("arenaUtbetalingUrl")
    private String arenaUtbetalingUrl;

    @Inject
    private UtbetalingService utbetalingService;

    @BeforeClass
    public static void setUp() {
        System.setProperty("SERVER_ARENA_URL", "http://dummy.url/foo");
    }

    @AfterClass
    public static void tearDown() {
        System.clearProperty("SERVER_ARENA_URL");
    }

    @Test
    public void utbetalingServiceBeanFinnes() {
        assertNotNull(utbetalingService);
    }

    @Test
    public void arenaUtbetalingUrlRendrerKorrekt() {
        assertThat(arenaUtbetalingUrl, is("http://dummy.url/foo?oppstart_skj=UB_22_MELDEHISTORIKK&fodselsnr="));
    }
}