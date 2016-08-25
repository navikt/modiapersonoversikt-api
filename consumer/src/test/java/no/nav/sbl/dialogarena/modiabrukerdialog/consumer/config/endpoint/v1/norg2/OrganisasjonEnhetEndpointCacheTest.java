package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg2;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.FinnNAVKontorForGeografiskNedslagsfeltBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.OrganisasjonEnhetV1;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OrganisasjonEnhetEndpointConfig.class})
public class OrganisasjonEnhetEndpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "organisasjonEnhet";

    @Inject
    private OrganisasjonEnhetV1 enhetWS;

    public OrganisasjonEnhetEndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeClass
    public static void setup() {
        System.setProperty("start.norg2.organisasjonenhet.withmock", "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
        System.setProperty("no.nav.modig.security.sts.url", "");
        System.setProperty("no.nav.modig.security.systemuser.username", "");
        System.setProperty("no.nav.modig.security.systemuser.password", "");
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilEnhetWS() throws FinnNAVKontorForGeografiskNedslagsfeltBolkUgyldigInput {
        final WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest request_1 = new WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest();
        request_1.withGeografiskNedslagsfeltListe("1234");
        final WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest request_2 = new WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest();
        request_2.withGeografiskNedslagsfeltListe("4321");
        enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(request_1);
        enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(request_2);
        enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(request_1);
        enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(request_2);

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getCache().getKeys().size(), is(2));
    }
}
