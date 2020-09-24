package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ExtendWith(SpringExtension.class)
class NAVAnsattEndpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "asbogosysAnsatt";

    @Autowired
    private GOSYSNAVansatt ansattWS;

    NAVAnsattEndpointCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    void cacheManagerHarEntryForEndpointCacheEtterKallTilAnsattWS() throws HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {
        ASBOGOSYSNAVAnsatt req1 = new ASBOGOSYSNAVAnsatt();
        req1.setAnsattId("1");
        ASBOGOSYSNAVAnsatt req2 = new ASBOGOSYSNAVAnsatt();
        req1.setAnsattId("2");

        ansattWS.hentNAVAnsatt(req1);
        ansattWS.hentNAVAnsatt(req1);
        ansattWS.hentNAVAnsatt(req2);
        ansattWS.hentNAVAnsatt(req2);

        assertThat(getCache().getName(), is("asbogosysAnsatt"));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }
}
