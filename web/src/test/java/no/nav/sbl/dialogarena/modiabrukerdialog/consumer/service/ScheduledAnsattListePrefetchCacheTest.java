package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ScheduledAnsattListePrefetch.CACHE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ScheduledAnsattListePrefetchCacheTest extends CacheTest {

    @Autowired
    OrganisasjonEnhetV2Service organisasjonEnhetService;

    @Autowired
    ScheduledAnsattListePrefetch prefetch;


    public ScheduledAnsattListePrefetchCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeEach
    public void beforeTest() {
        List<AnsattEnhet> enheter = Arrays.asList(new AnsattEnhet("0100", "Nav Østfold"), new AnsattEnhet("2960", "Nav Drift"));
        when(organisasjonEnhetService.hentAlleEnheter(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE)).thenReturn(enheter);
    }


    @Test
    public void prefetchLagrerTilAnsattCache() {
        prefetch.prefetchAnsattListe();

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }

    @Test
    public void tidligereInnholdSlettesVedPrefetch() {
        getCache().put("0000", "innhold");
        assertThat(getNativeCache().estimatedSize(), is(1L));

        prefetch.prefetchAnsattListe();

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }

    @Configuration
    static class ContextConfiguration {

        @Bean
        public OrganisasjonEnhetV2Service organisasjonEnhetV2Service() {
            return mock(OrganisasjonEnhetV2Service.class);
        }

        @Bean
        public ScheduledAnsattListePrefetch scheduledAnsattListePrefetch() {
            return new ScheduledAnsattListePrefetch();
        }

        @Bean
        public GOSYSNAVansatt gosysNavAnsatt() {
            return mock(GOSYSNAVansatt.class);
        }


    }


}