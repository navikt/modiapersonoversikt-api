package no.nav.modiapersonoversikt.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain;
import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static no.nav.modiapersonoversikt.service.ScheduledAnsattListePrefetch.CACHE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class ScheduledAnsattListePrefetchCacheTest extends CacheTest {
    @Autowired
    NorgApi norgApi;
    @Autowired
    GOSYSNAVansatt gosysnaVansatt;
    @Autowired
    ScheduledAnsattListePrefetch prefetch;

    final List<NorgDomain.Enhet> enheter = Arrays.asList(
            new NorgDomain.Enhet("0100", "Nav Østfold", NorgDomain.EnhetStatus.AKTIV),
            new NorgDomain.Enhet("2960", "Nav Drift", NorgDomain.EnhetStatus.AKTIV)
    );
    public ScheduledAnsattListePrefetchCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    public void tidligereInnholdSlettesVedPrefetch() {
        when(norgApi.hentEnheter(any(), any(), any())).thenReturn(enheter);

        getCache().put("0000", "innhold");
        assertThat(getNativeCache().estimatedSize(), is(1L));

        prefetch.prefetchAnsattListe();

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }
}
