package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.NORG_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NAVOrgEnhetEndpointConfig.class})
public class NAVOrgEnhetEndpointCacheTest extends CacheTest {

    public static final String CACHE_NAME = "asbogosysEnhet";

    @Inject
    private GOSYSNAVOrgEnhet enhetWS;

    public NAVOrgEnhetEndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeClass
    public static void setup() {
        System.setProperty(NORG_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilEnhetWS() throws HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg, HentNAVEnhetFaultGOSYSGeneriskMsg {
        ASBOGOSYSNavEnhet enhet1 = new ASBOGOSYSNavEnhet();
        enhet1.setEnhetsId("1231");
        ASBOGOSYSNavEnhet enhet2 = new ASBOGOSYSNavEnhet();
        enhet2.setEnhetsId("3211");

        enhetWS.hentNAVEnhet(enhet1);
        enhetWS.hentNAVEnhet(enhet1);
        enhetWS.hentNAVEnhet(enhet2);
        enhetWS.hentNAVEnhet(enhet2);

        assertThat(getCache().getName(), is("asbogosysEnhet"));
        assertThat(getCache().getKeys().size(), is(2));
    }
}
