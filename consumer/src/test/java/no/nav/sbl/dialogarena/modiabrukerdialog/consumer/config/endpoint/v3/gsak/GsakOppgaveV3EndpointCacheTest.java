package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgaveV3EndpointConfig.GSAK_V3_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        GsakOppgaveV3EndpointConfig.class
})
public class GsakOppgaveV3EndpointCacheTest extends CacheTest {
    public static final String CACHE_NAME = "endpointCache";

    @Inject
    private OppgaveV3 gsak;


    public GsakOppgaveV3EndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeClass
    public static void setup() {
        System.setProperty(GSAK_V3_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilHentOppgave() throws HentOppgaveOppgaveIkkeFunnet {
//        WSHentOppgaveRequest req1 = new WSHentOppgaveRequest().withOppgaveId("1");
//        WSHentOppgaveRequest req2 = new WSHentOppgaveRequest().withOppgaveId("1");
//
//        when(gsak.hentOppgave(req1)).thenReturn(
//                new WSHentOppgaveResponse().withOppgave(new WSOppgave().withBeskrivelse("a")),
//                new WSHentOppgaveResponse().withOppgave(new WSOppgave().withBeskrivelse("b"))
//        );
//
//        String resp1 = gsak.hentOppgave(req1).getOppgave().getBeskrivelse();
//        String resp2 = gsak.hentOppgave(req2).getOppgave().getBeskrivelse();
//
//        assertThat(resp1, is(resp2));
    }

}
