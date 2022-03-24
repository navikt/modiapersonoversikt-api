package no.nav.modiapersonoversikt.config.endpoint.oppfolgingsinfo;

import no.nav.common.types.identer.Fnr;
import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class OppfolgingsinfoApiCacheTest extends CacheTest {

    @Autowired
    private ArbeidsrettetOppfolging.Service oppfolgingsinfoApiService;

    public OppfolgingsinfoApiCacheTest() {
        super("oppfolgingsinfoCache");
    }

    @Test
    void cacheSetupMedRiktigKeyGenerator() {
        oppfolgingsinfoApiService.hentOppfolgingsinfo(Fnr.of("1234567910"));

        assertThat(getNativeCache().estimatedSize(), is(1L));
        assertThat(getKey(), is(generatedByUserKeyGenerator()));
    }
}