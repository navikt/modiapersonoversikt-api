package no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class KodeverksmapperCacheTest extends CacheTest {

    @Autowired
    private Kodeverksmapper kodeverksmapper;

    public KodeverksmapperCacheTest() {
        super("kodeverksmapperCache");
    }

    @Test
    void cacheSetupMedRiktigKeyGenerator() throws Exception {
        kodeverksmapper.hentOppgavetype();

        assertThat(getNativeCache().estimatedSize(), is(1L));
        assertThat(getKey(), is(generatedByDefaultKeyGenerator()));
    }

    @Test
    void cacheKeysSkalVareUnikeForUlikeMetoder() {
        verifyUniqueAndStableCacheKeys(
                () -> kodeverksmapper.hentUnderkategori(),
                () -> kodeverksmapper.hentOppgavetype()
        );
    }
}