package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class NAVAnsattEndpointCacheTest extends CacheTest {

    public static final String CACHE_NAME = "asbogosysAnsatt";

    @Inject
    private GOSYSNAVansatt ansattWS;

    public NAVAnsattEndpointCacheTest() {
        super(CACHE_NAME);
    }


    @Test
    public void cacheManagerHarEntryForEndpointCacheEtterKallTilAnsattWS() throws HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        ASBOGOSYSNAVAnsatt req1 = new ASBOGOSYSNAVAnsatt();
        req1.setAnsattId("1");
        ASBOGOSYSNAVAnsatt req2 = new ASBOGOSYSNAVAnsatt();
        req1.setAnsattId("2");

        ansattWS.hentNAVAnsatt(req1);
        ansattWS.hentNAVAnsatt(req1);
        ansattWS.hentNAVAnsatt(req2);
        ansattWS.hentNAVAnsatt(req2);

        assertThat(getCache().getName(), is("asbogosysAnsatt"));
        assertThat(getCache().getKeys().size(), is(2));
    }

    @Configuration
    static class ContextConfiguration {

        @Bean
        public GOSYSNAVansatt gosysNavAnsatt() {
            return mock(GOSYSNAVansatt.class);
        }


    }

}
