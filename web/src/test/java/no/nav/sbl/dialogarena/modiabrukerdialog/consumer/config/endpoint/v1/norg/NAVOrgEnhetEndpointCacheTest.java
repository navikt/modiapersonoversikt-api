package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ExtendWith(SpringExtension.class)
class NAVOrgEnhetEndpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "asbogosysEnhet";

    @Autowired
    private GOSYSNAVOrgEnhet enhetWS;

    NAVOrgEnhetEndpointCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    void cacheManager_harEntryForEndpointCache_etterKallTilEnhetWS() throws Exception {
        ASBOGOSYSNavEnhet enhet1 = new ASBOGOSYSNavEnhet();
        enhet1.setEnhetsId("1231");
        ASBOGOSYSNavEnhet enhet2 = new ASBOGOSYSNavEnhet();
        enhet2.setEnhetsId("3211");

        enhetWS.hentNAVEnhet(enhet1);
        enhetWS.hentNAVEnhet(enhet1);
        enhetWS.hentNAVEnhet(enhet2);
        enhetWS.hentNAVEnhet(enhet2);

        assertThat(getCache().getName(), is("asbogosysEnhet"));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }
}
