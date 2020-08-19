package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ScheduledAnsattListePrefetchTest {

    @Mock
    OrganisasjonEnhetV2Service organisasjonEnhetService;
    @Mock
    GOSYSNAVansatt ansattWS;
    @Mock
    CacheManager cacheManager;

    @Captor
    ArgumentCaptor<ASBOGOSYSNavEnhet> captor;

    @InjectMocks
    ScheduledAnsattListePrefetch scheduler;
    private List<AnsattEnhet> enheter;

    @BeforeEach
    public void setUp() throws Exception {
        initMocks(this);
        enheter = Arrays.asList(new AnsattEnhet("0100", "Nav Østfold"), new AnsattEnhet("2960", "Nav Drift"));
        when(organisasjonEnhetService.hentAlleEnheter(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE)).thenReturn(enheter);
        when(cacheManager.getCache(anyString())).thenReturn(mock(Cache.class));
    }

    @Test
    public void skalKalleHentAnsatteMedRettEnhetsId() throws Exception {
        scheduler.prefetchAnsattListe();
        verify(ansattWS, times(2)).hentNAVAnsattListe(captor.capture());

        List<ASBOGOSYSNavEnhet> requestEnheter = captor.getAllValues();
        assertThat(requestEnheter.get(0).getEnhetsId(), is(enheter.get(0).enhetId));
        assertThat(requestEnheter.get(0).getEnhetsNavn(), is(enheter.get(0).enhetNavn));
        assertThat(requestEnheter.get(1).getEnhetsId(), is(enheter.get(1).enhetId));
        assertThat(requestEnheter.get(1).getEnhetsNavn(), is(enheter.get(1).enhetNavn));
    }

    @Test
    public void skalIkkeFeileDersomAnsattWsKalles() throws Exception {
        when(ansattWS.hentNAVAnsattListe(any(ASBOGOSYSNavEnhet.class))).thenThrow(new RuntimeException());
        scheduler.prefetchAnsattListe();
    }
}
