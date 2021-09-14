package no.nav.modiapersonoversikt.config.endpoint.oppfolgingsinfo;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class OppfolgingsinfoApiCacheTest extends CacheTest {

    @Autowired
    private OppfolgingsinfoApiService oppfolgingsinfoApiService;

    public OppfolgingsinfoApiCacheTest() {
        super("oppfolgingsinfoCache");
    }

    @Test
    void cacheSetupMedRiktigKeyGenerator() {
        oppfolgingsinfoApiService.hentOppfolgingsinfo("1234567910", null);

        assertThat(getNativeCache().estimatedSize(), is(1L));
        assertThat(getKey(), is(generatedByUserKeyGenerator()));
    }
}