package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.aktor;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.aktor.AktorEndpointConfig.AKTOER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        AktorEndpointConfig.class
})
public class AktorCacheTest extends CacheTest {

    public static final String AKTOR_CACHE = "aktorIdCache";

    @Inject
    private AktoerPortType aktoer;

    public AktorCacheTest() {
        super(AKTOR_CACHE);
    }

    @BeforeClass
    public static void setup() {
        //Problemfritt å kjøre med mock ettersom cacheannotasjon wrapper rundt switchingen
        System.setProperty(AKTOER_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForAktorCache_etterKallTilAktor() throws HentAktoerIdForIdentPersonIkkeFunnet {
        HentAktoerIdForIdentRequest request1 = new HentAktoerIdForIdentRequest("242424 55555");
        HentAktoerIdForIdentRequest request2 = new HentAktoerIdForIdentRequest("242424 55555");
        when(aktoer.hentAktoerIdForIdent(request1)).thenReturn(
                new HentAktoerIdForIdentResponse("1"),
                new HentAktoerIdForIdentResponse("2")
        );
        HentAktoerIdForIdentResponse resp1 = aktoer.hentAktoerIdForIdent(request1);
        HentAktoerIdForIdentResponse resp2 = aktoer.hentAktoerIdForIdent(request2);

        assertThat(resp1.getAktoerId(), is(resp2.getAktoerId()));
    }
}
