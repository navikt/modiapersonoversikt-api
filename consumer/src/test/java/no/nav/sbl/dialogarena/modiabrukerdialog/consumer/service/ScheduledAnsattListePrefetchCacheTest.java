package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import net.sf.ehcache.Element;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.EnhetService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ScheduledAnsattListePrefetch.CACHE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ScheduledAnsattListePrefetchCacheTest extends CacheTest {

    @Inject
    EnhetService enhetService;

    @Inject
    ScheduledAnsattListePrefetch prefetch;


    public ScheduledAnsattListePrefetchCacheTest() {
        super(CACHE_NAME);
    }

    @Before
    public void beforeTest() {
        List<AnsattEnhet> enheter = Arrays.asList(new AnsattEnhet("0100", "Nav Østfold"), new AnsattEnhet("2960", "Nav Drift"));
        when(enhetService.hentAlleEnheter()).thenReturn(enheter);
    }


    @Test
    public void prefetchLagrerTilAnsattCache() throws HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        prefetch.prefetchAnsattListe();

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getCache().getKeys().size(), is(2));
    }

    @Test
    public void tidligereInnholdSlettesVedPrefetch() throws HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        getCache().put(new Element("0000", "innhold"));
        assertThat(getCache().getKeys().size(), is(1));

        prefetch.prefetchAnsattListe();

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getCache().getKeys().size(), is(2));
    }

    @Configuration
    static class ContextConfiguration {

        @Bean
        public EnhetService enhetService() {
            return mock(EnhetService.class);
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