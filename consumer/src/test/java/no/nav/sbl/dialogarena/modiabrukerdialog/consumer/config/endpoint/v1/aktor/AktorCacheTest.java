package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AktorCacheTest extends CacheTest {
    private static final String AKTOR_CACHE = "aktorIdCache";

    @Inject
    private AktoerPortType aktoer;

    AktorCacheTest() {
        super(AKTOR_CACHE);
    }

    @Test
    void cacheManager_harEntryForAktorCache_etterKallTilAktor() throws HentAktoerIdForIdentPersonIkkeFunnet {
        when(aktoer.hentAktoerIdForIdent(any())).thenReturn(
                new HentAktoerIdForIdentResponse("1"),
                new HentAktoerIdForIdentResponse("2")
        );

        HentAktoerIdForIdentRequest request1 = new HentAktoerIdForIdentRequest("242424 55555");
        HentAktoerIdForIdentRequest request2 = new HentAktoerIdForIdentRequest("242424 55555");

        HentAktoerIdForIdentResponse resp1 = aktoer.hentAktoerIdForIdent(request1);
        HentAktoerIdForIdentResponse resp2 = aktoer.hentAktoerIdForIdent(request2);

        assertThat(resp1.getAktoerId(), is(resp2.getAktoerId()));
    }
}
