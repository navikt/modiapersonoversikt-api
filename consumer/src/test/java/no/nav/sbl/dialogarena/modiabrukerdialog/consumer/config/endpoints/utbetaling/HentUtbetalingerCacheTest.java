package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.CacheConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSPeriode;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSYtelsesfilter;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingEndpointConfig.UTBETALING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CacheConfig.class, UtbetalingWrapperTestConfig.class, UtbetalingEndpointConfig.class})
public class HentUtbetalingerCacheTest {

    @Inject
    private UtbetalingPortType utbetalingPortType;

    @Inject
    @Qualifier("utbetalingPortTypeWrapperMock")
    private Wrapper<UtbetalingPortType> mockPortWrapper;

    @Inject
    @Qualifier("utbetalingPortTypeWrapper")
    private Wrapper<UtbetalingPortType> realPortWrapper;

    @BeforeClass
    public static void setUp() {
        setProperty("utbetalingendpoint.v2.url", "https://service-gw-t11.test.local/");
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setProperty(UTBETALING_KEY, "true");
    }

    @Test
    public void identicalRequestsOnlySentOnce()
            throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeUgyldigDato, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig {

        DateTime fom = now().minusDays(10);
        DateTime tom = now();

        final WSHentUtbetalingListeRequest request = createRequest(fom, tom);
        final WSHentUtbetalingListeRequest request2 = createRequest(fom, tom);

        utbetalingPortType.hentUtbetalingListe(request);
        utbetalingPortType.hentUtbetalingListe(request2);

        verify(realPortWrapper.wrappedObject, times(0)).hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class));
        verify(mockPortWrapper.wrappedObject, times(1)).hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class));
    }

    private WSHentUtbetalingListeRequest createRequest(DateTime fom, DateTime tom) {
        return new WSHentUtbetalingListeRequest()
                .withMottaker("11223312345")
                .withYtelsesfilter(new WSYtelsesfilter())
                .withPeriode(new WSPeriode()
                        .withFom(fom)
                        .withTom(tom));
    }
}
