package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.aktoer.v2.Aktoer_v2;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentResponse;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AktorCacheTest extends CacheTest {
    private static final String AKTOR_CACHE = "aktorIdCache";

    @Autowired
    private Aktoer_v2 aktoer;

    AktorCacheTest() {
        super(AKTOR_CACHE);
    }

    @Test
    void cacheManager_harEntryForAktorCache_etterKallTilAktor() throws HentAktoerIdForIdentPersonIkkeFunnet {
        when(aktoer.hentAktoerIdForIdent(any())).thenReturn(
                new WSHentAktoerIdForIdentResponse().withAktoerId("123"),
                new WSHentAktoerIdForIdentResponse().withAktoerId("567")
        );

        WSHentAktoerIdForIdentRequest request1 = new WSHentAktoerIdForIdentRequest().withIdent("242424 55555");
        WSHentAktoerIdForIdentRequest request2 = new WSHentAktoerIdForIdentRequest().withIdent("242424 55555");

        WSHentAktoerIdForIdentResponse resp1 = aktoer.hentAktoerIdForIdent(request1);
        WSHentAktoerIdForIdentResponse resp2 = aktoer.hentAktoerIdForIdent(request2);

        assertThat(resp1.getAktoerId(), is(resp2.getAktoerId()));
    }
}
