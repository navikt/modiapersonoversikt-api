package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling;

import no.nav.modig.cache.CacheConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSForespurtPeriode;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSIdent;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling.UtbetalingEndpointConfig.UTBETALING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CacheConfig.class, UtbetalingWrapperTestConfig.class, UtbetalingEndpointConfig.class})
public class HentUtbetalingerCacheTest {

    @Inject
    private UtbetalingV1 utbetalingPortType;

    @Inject
    private EhCacheCacheManager cm;

    @Inject
    @Qualifier("utbetalingPortTypeWrapperMock")
    private Wrapper<UtbetalingV1> mockPortWrapper;

    @Inject
    @Qualifier("utbetalingPortTypeWrapper")
    private Wrapper<UtbetalingV1> realPortWrapper;

    @BeforeClass
    public static void setUp() {
        setProperty("utbetalingendpoint.v2.url", "https://service-gw-t11.test.local/");
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setProperty(UTBETALING_KEY, "true");
    }

    @Test
    public void identicalRequestsOnlySentOnce() throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {

        DateTime fom = now().minusDays(10);
        DateTime tom = now();

        final WSHentUtbetalingsinformasjonRequest request = createRequest(fom, tom);
        final WSHentUtbetalingsinformasjonRequest request2 = createRequest(fom, tom);

        utbetalingPortType.hentUtbetalingsinformasjon(request);
        utbetalingPortType.hentUtbetalingsinformasjon(request2);

        verify(realPortWrapper.wrappedObject, times(0)).hentUtbetalingsinformasjon(any(WSHentUtbetalingsinformasjonRequest.class));
        verify(mockPortWrapper.wrappedObject, times(1)).hentUtbetalingsinformasjon(any(WSHentUtbetalingsinformasjonRequest.class));
    }

    private WSHentUtbetalingsinformasjonRequest createRequest(DateTime fom, DateTime tom) {
        return new WSHentUtbetalingsinformasjonRequest()
                .withId(new WSIdent().withIdent("11223312345"))
                .withPeriode(new WSForespurtPeriode()
                        .withFom(fom)
                        .withTom(tom));
    }

    @After
    public void shutdown() {
        cm.getCacheManager().shutdown();
    }


}
