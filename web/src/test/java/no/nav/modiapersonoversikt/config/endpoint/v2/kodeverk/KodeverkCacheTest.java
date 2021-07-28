package no.nav.modiapersonoversikt.config.endpoint.v2.kodeverk;


import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

class KodeverkCacheTest extends CacheTest {

    private static final String CACHE_NAME = "kodeverkCache";

    @Autowired
    private KodeverkPortType kodeverk;

    KodeverkCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    void cacheManager_harEntryForKodeverk_etterKallTilKodeverk() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        XMLHentKodeverkRequest request1 = new XMLHentKodeverkRequest().withNavn("navn1");
        XMLHentKodeverkRequest request2 = new XMLHentKodeverkRequest().withNavn("navn2");

        kodeverk.hentKodeverk(request1);
        kodeverk.hentKodeverk(request1);
        kodeverk.hentKodeverk(request2);
        kodeverk.hentKodeverk(request2);

        assertThat(getNativeCache().estimatedSize(), is(2L));
        assertThat(getKey(), is(generatedByDefaultKeyGenerator()));
    }

    @Test
    void cacheKeysSkalVareUnikeForUlikeMetoder() {
        verifyUniqueCacheKeys(
                () -> kodeverk.finnKodeverkListe(new XMLFinnKodeverkListeRequest()),
                () -> kodeverk.hentKodeverk(new XMLHentKodeverkRequest())
        );
    }
}
