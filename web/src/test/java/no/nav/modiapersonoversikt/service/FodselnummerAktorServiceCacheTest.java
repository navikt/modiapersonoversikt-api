package no.nav.modiapersonoversikt.service;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FodselnummerAktorServiceCacheTest extends CacheTest {
    private static final String AKTOR_CACHE = "pdlAktorFnrCache";

    @Autowired
    private FodselnummerAktorService fnrAktorService;

    FodselnummerAktorServiceCacheTest() {
        super(AKTOR_CACHE);
    }

    @Test
    void cacheManager_harEntryForAktorCache_etterKallTilAktor() {
        when(fnrAktorService.hentAktorIdForFnr(any())).thenReturn("987","654");
        when(fnrAktorService.hentFnrForAktorId(any())).thenReturn("789","456");

        String aktorId1 = fnrAktorService.hentAktorIdForFnr("123");
        String aktorId2 = fnrAktorService.hentAktorIdForFnr("123");

        String fnr1 = fnrAktorService.hentFnrForAktorId("321");
        String fnr2 = fnrAktorService.hentFnrForAktorId("321");
        String fnr3 = fnrAktorService.hentFnrForAktorId("322");

        assertThat(aktorId1, is(aktorId2));
        assertThat(fnr1, is(fnr2));
        assertThat(fnr3, is("456"));
    }
}
