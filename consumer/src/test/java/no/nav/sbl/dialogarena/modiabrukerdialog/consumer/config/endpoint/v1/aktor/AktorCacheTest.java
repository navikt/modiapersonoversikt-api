package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AktorEndpointConfig.class})
public class AktorCacheTest extends CacheTest {

    static {
        System.setProperty("no.nav.modig.security.sts.url", "https://sts-t4.test.local/SecurityTokenServiceProvider/");
        System.setProperty("no.nav.modig.security.systemuser.username", "");
        System.setProperty("no.nav.modig.security.systemuser.password", "");
    }

    public static final String AKTOR_CACHE = "aktorIdCache";

    @Inject
    private AktoerPortType aktoer;

    public AktorCacheTest() {
        super(AKTOR_CACHE);
    }

    @BeforeAll
    public static void setup() {
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
