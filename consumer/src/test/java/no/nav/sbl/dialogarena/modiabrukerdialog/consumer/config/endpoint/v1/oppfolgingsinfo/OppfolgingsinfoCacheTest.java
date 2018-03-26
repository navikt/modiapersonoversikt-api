package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.oppfolgingsinfo.OppfolgingsinfoEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.oppfolgingsinfo.OppfolgingsinfoEndpointConfig.MOCK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OppfolgingsinfoEndpointConfig.class})
public class OppfolgingsinfoCacheTest extends CacheTest {

    private static final String OPPFOLGINGSINFO_CACHE = "oppfolgingsinfoCache";
    private static final String AKTOERID_1 = "***REMOVED***";
    private static final String AKTOERID_2 = "***REMOVED***";

    public OppfolgingsinfoCacheTest() {
        super(OPPFOLGINGSINFO_CACHE);
    }

    @Inject
    private OppfolgingsinfoV1 oppfolgingsinfoV1;

    @BeforeAll
    public static void setUp() {
        setProperty(MOCK_KEY, "true");
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
    }

    @BeforeEach
    public void setUpMock() throws Exception {
        OppfolgingsinfoV1 unwrapped = (OppfolgingsinfoV1) unwrapProxy(oppfolgingsinfoV1);
        reset(unwrapped);

        when(unwrapped.hentOppfolgingsstatus(any(OppfolgingsstatusRequest.class)))
                .thenReturn(
                        new OppfolgingsstatusResponse().withWsOppfolgingsdata(new WSOppfolgingsdata().withErUnderOppfolging(true)),
                        new OppfolgingsstatusResponse().withWsOppfolgingsdata(new WSOppfolgingsdata().withErUnderOppfolging(false))
                );
    }

    @Test
    public void toKallTilHentOppfolgingsstatusMedSammeIdentGirBareEttTjenestekall() throws Exception {
        OppfolgingsstatusRequest request1 = new OppfolgingsstatusRequest().withAktorId(AKTOERID_1);
        OppfolgingsstatusRequest request2 = new OppfolgingsstatusRequest().withAktorId(AKTOERID_1);

        OppfolgingsstatusResponse response1 = oppfolgingsinfoV1.hentOppfolgingsstatus(request1);
        OppfolgingsstatusResponse response2 = oppfolgingsinfoV1.hentOppfolgingsstatus(request2);

        OppfolgingsinfoV1 unwrappedOppfolgingsinfo = (OppfolgingsinfoV1) unwrapProxy(oppfolgingsinfoV1);
        verify(unwrappedOppfolgingsinfo, times(1)).hentOppfolgingsstatus(any());

        assertThat(response1.getWsOppfolgingsdata().isErUnderOppfolging(), is(response2.getWsOppfolgingsdata().isErUnderOppfolging()));
    }

    @Test
    public void toKallTilHentOppfolgingsstatusMedForskjelligeIdenterGirToTjenestekall() throws Exception {
        OppfolgingsstatusRequest request1 = new OppfolgingsstatusRequest().withAktorId(AKTOERID_1);
        OppfolgingsstatusRequest request2 = new OppfolgingsstatusRequest().withAktorId(AKTOERID_2);

        OppfolgingsstatusResponse response1 = oppfolgingsinfoV1.hentOppfolgingsstatus(request1);
        OppfolgingsstatusResponse response2 = oppfolgingsinfoV1.hentOppfolgingsstatus(request2);

        OppfolgingsinfoV1 unwrappedOppfolgingsinfo = (OppfolgingsinfoV1) unwrapProxy(oppfolgingsinfoV1);
        verify(unwrappedOppfolgingsinfo, times(2)).hentOppfolgingsstatus(any());

        assertThat(response1.getWsOppfolgingsdata().isErUnderOppfolging(), is(not(response2.getWsOppfolgingsdata().isErUnderOppfolging())));
    }

}
