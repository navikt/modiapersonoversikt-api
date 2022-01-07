package no.nav.modiapersonoversikt.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ScheduledAnsattListePrefetchTest {

    @Mock
    NorgApi norgApi;
    @Mock
    GOSYSNAVansatt ansattWS;
    @Mock
    CacheManager cacheManager;

    @Captor
    ArgumentCaptor<ASBOGOSYSNavEnhet> captor;

    @InjectMocks
    ScheduledAnsattListePrefetch scheduler;
    private List<NorgDomain.Enhet> enheter;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        enheter = Arrays.asList(
                new NorgDomain.Enhet("0100", "Nav Østfold", NorgDomain.EnhetStatus.AKTIV),
                new NorgDomain.Enhet("2960", "Nav Drift", NorgDomain.EnhetStatus.AKTIV)
        );
        when(norgApi.hentEnheter(null, NorgDomain.OppgaveBehandlerFilter.KUN_OPPGAVEBEHANDLERE, NorgApi.getIKKE_NEDLAGT())).thenReturn(enheter);
        when(cacheManager.getCache(anyString())).thenReturn(mock(Cache.class));
    }

    @Test
    public void skalKalleHentAnsatteMedRettEnhetsId() throws Exception {
        scheduler.prefetchAnsattListe();
        verify(ansattWS, times(2)).hentNAVAnsattListe(captor.capture());

        List<ASBOGOSYSNavEnhet> requestEnheter = captor.getAllValues();
        assertThat(requestEnheter.get(0).getEnhetsId(), is(enheter.get(0).getEnhetId()));
        assertThat(requestEnheter.get(0).getEnhetsNavn(), is(enheter.get(0).getEnhetNavn()));
        assertThat(requestEnheter.get(1).getEnhetsId(), is(enheter.get(1).getEnhetId()));
        assertThat(requestEnheter.get(1).getEnhetsNavn(), is(enheter.get(1).getEnhetNavn()));
    }

    @Test
    public void skalIkkeFeileDersomAnsattWsKalles() throws Exception {
        when(ansattWS.hentNAVAnsattListe(any(ASBOGOSYSNavEnhet.class))).thenThrow(new RuntimeException());
        scheduler.prefetchAnsattListe();
    }
}
