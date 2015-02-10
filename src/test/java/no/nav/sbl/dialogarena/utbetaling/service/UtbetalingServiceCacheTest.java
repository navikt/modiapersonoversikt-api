package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.cache.CacheConfig;
import no.nav.sbl.dialogarena.utbetaling.config.UtbetalingPortTypeStubConfig;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = {UtbetalingLamellContext.class, UtbetalingPortTypeStubConfig.class, CacheConfig.class})
public class UtbetalingServiceCacheTest {

    @Inject
    private EhCacheCacheManager cacheManager;

    @Inject
    private UtbetalingService utbetalingService;

    @Test
    public void toLikeKallHvorSisteBlirCachet() throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {
        String fnr = "***REMOVED***";
        LocalDate fom = new LocalDate(2015, 1, 1);
        LocalDate tom = new LocalDate(2015, 1, 2);

        utbetalingService.hentUtbetalinger(fnr, fom, tom);
        utbetalingService.hentUtbetalinger(fnr, fom, tom);

        int i = ((EhCacheCache) cacheManager.getCache("endpointCache")).getNativeCache().getSize();
        assertThat(i, equalTo(1));
    }

    @Test
    public void toUlikeKallHvorSisteIkkeBlirCachet() throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {
        String fnr = "***REMOVED***";
        LocalDate fom = new LocalDate(2015, 1, 1);
        LocalDate tom = new LocalDate(2015, 1, 2);

        LocalDate tom2 = new LocalDate(2015, 1, 3);

        utbetalingService.hentUtbetalinger(fnr, fom, tom);
        utbetalingService.hentUtbetalinger(fnr, fom, tom2);

        int i = ((EhCacheCache) cacheManager.getCache("endpointCache")).getNativeCache().getSize();
        assertThat(i, equalTo(2));
    }
}
