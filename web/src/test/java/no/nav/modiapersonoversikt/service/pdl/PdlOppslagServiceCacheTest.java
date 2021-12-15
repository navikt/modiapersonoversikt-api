package no.nav.modiapersonoversikt.service.pdl;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PdlOppslagServiceCacheTest extends CacheTest {
    private static final String AKTOR_CACHE = "pdlCache";

    @Autowired
    private PdlOppslagService pdlOppslagService;


    PdlOppslagServiceCacheTest() {
        super(AKTOR_CACHE);
    }

    @Test
    void cacheManager_pdl_cache() {
        when(pdlOppslagService.hentAktorId(any())).thenReturn("987","654");
        when(pdlOppslagService.hentFnr(any())).thenReturn("789","456");

        when(pdlOppslagService.hentGeografiskTilknyttning(any())).thenReturn("0101", "0202");

        String aktorId1 = pdlOppslagService.hentAktorId("123");
        String aktorId2 = pdlOppslagService.hentAktorId("123");

        String fnr1 = pdlOppslagService.hentFnr("321");
        String fnr2 = pdlOppslagService.hentFnr("321");
        String fnr3 = pdlOppslagService.hentFnr("322");

        String gt1 = pdlOppslagService.hentGeografiskTilknyttning("123");
        String gt2 = pdlOppslagService.hentGeografiskTilknyttning("123");

        assertThat(aktorId1, is(aktorId2));
        assertThat(fnr1, is(fnr2));
        assertThat(fnr3, is("456"));
        assertThat(gt1, is(gt2));
        assertThat(getKey(), is(generatedByUserKeyGenerator()));
    }
}
