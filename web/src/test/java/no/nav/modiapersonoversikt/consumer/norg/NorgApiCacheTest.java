package no.nav.modiapersonoversikt.consumer.norg;

import no.nav.common.types.identer.EnhetId;
import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class NorgApiCacheTest extends CacheTest {

    private static final String CACHE_NAME = "norgApi";

    @Autowired
    private NorgApi norgApi;

    NorgApiCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    void cacheManagerHarEntryForEndpointCacheEtterKallTilEnhet() {
        norgApi.hentEnheter("1234", null, null);
        norgApi.hentEnheter("4321", null, null);
        norgApi.hentEnheter("1234", null, null);
        norgApi.hentEnheter("4321", null, null);

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }

    @Test
    void cacheManagerCacherKallTilFinnNAVKontor() {
        norgApi.finnNavKontor("1234", NorgDomain.DiskresjonsKode.ANY);
        norgApi.finnNavKontor("4231", NorgDomain.DiskresjonsKode.ANY);
        norgApi.finnNavKontor("1234", NorgDomain.DiskresjonsKode.ANY);
        norgApi.finnNavKontor("4231", NorgDomain.DiskresjonsKode.ANY);

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }

    @Test
    void cacheKeysSkalVareUnikeForUlikeMetoder() {
        verifyUniqueAndStableCacheKeys(
                () -> norgApi.hentGeografiskTilknyttning(EnhetId.of("1234")),
                () -> norgApi.hentGeografiskTilknyttning(EnhetId.of("4567")),
                () -> norgApi.hentEnheter(null, NorgDomain.OppgaveBehandlerFilter.UFILTRERT, NorgApi.getIKKE_NEDLAGT()),
                () -> norgApi.hentEnheter(null, NorgDomain.OppgaveBehandlerFilter.KUN_OPPGAVEBEHANDLERE, NorgApi.getIKKE_NEDLAGT()),
                () -> norgApi.hentEnheter("1234", NorgDomain.OppgaveBehandlerFilter.KUN_OPPGAVEBEHANDLERE, NorgApi.getIKKE_NEDLAGT()),
                () -> norgApi.finnNavKontor("1234", NorgDomain.DiskresjonsKode.ANY),
                () -> norgApi.finnNavKontor("4567", NorgDomain.DiskresjonsKode.ANY),
                () -> norgApi.hentBehandlendeEnheter(null, null, null, null, null, null),
                () -> norgApi.hentBehandlendeEnheter(null, "1234", null, null, null, null),
                () -> norgApi.hentBehandlendeEnheter(null, "4567", null, null, null, null),
                () -> norgApi.hentKontaktinfo("1234"),
                () -> norgApi.hentKontaktinfo("1235")
        );
        assertThat(getKey(), Matchers.is(generatedByMethodAwareKeyGenerator()));
    }
}
