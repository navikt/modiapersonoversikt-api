package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.CacheConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingWrapperTestConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.UtbetalingPortTypeWrapper;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;


@DirtiesContext(classMode = AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CacheConfig.class, UtbetalingWrapperTestConfig.class, UtbetalingEndpointConfig.class})
public class CacheUtbetalingrequestsTest {

    @Inject
    private UtbetalingPortType utbetalingPortType;

    @Inject
    @Qualifier("utbetalingPortTypeWrapperMock")
    private UtbetalingPortTypeWrapper mockPortWrapper;

    @Inject
    @Qualifier("utbetalingPortTypeWrapper")
    private UtbetalingPortTypeWrapper realPortWrapper;

    @BeforeClass
    public static void setUp() {
        System.setProperty("utbetalingendpoint.v2.url", "https://service-gw-t11.test.local/");
        System.setProperty(MockUtil.TILLATMOCKSETUP_PROPERTY, "http://ja.no");
        System.setProperty(UtbetalingEndpointConfig.UTBETALING_KEY, "yes");
    }

    @Test
    public void identicalRequestsOnlySentOnce() throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeUgyldigDato, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig {

        DateTime fom = now().minusDays(10);
        DateTime tom = now();

        final WSHentUtbetalingListeRequest request = createRequest(fom, tom);
        final WSHentUtbetalingListeRequest request2 = createRequest(fom, tom);

        utbetalingPortType.hentUtbetalingListe(request);
        utbetalingPortType.hentUtbetalingListe(request2);

        Mockito.verify(realPortWrapper.getPortType(), times(0)).hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class));
        Mockito.verify(mockPortWrapper.getPortType(), times(1)).hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class));
    }

    private WSHentUtbetalingListeRequest createRequest(DateTime fom, DateTime tom) {
        return new WSHentUtbetalingListeRequest()
                .withMottaker("11223312345")
                .withYtelsesfilter(new WSYtelsesfilter())
                .withPeriode(new WSPeriode()
                        .withFom(fom)
                        .withTom(tom)
                );
    }
}
