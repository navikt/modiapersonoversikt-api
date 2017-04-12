package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhet;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSHentEnhetBolkRequest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OrganisasjonEnhetV2EndpointConfig.class})
public class OrganisasjonEnhetV2EndpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "organisasjonEnhetV2";

    @Inject
    private OrganisasjonEnhetV2 enhetWS;

    public OrganisasjonEnhetV2EndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeClass
    public static void setup() {
        System.setProperty("start.organisasjonenhet.v2.withmock", "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
        System.setProperty("no.nav.modig.security.sts.url", "");
        System.setProperty("no.nav.modig.security.systemuser.username", "");
        System.setProperty("no.nav.modig.security.systemuser.password", "");
    }


    //TODO: Wtf? classloading.
    @Ignore
    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilEnhetWS() {
        final WSHentEnhetBolkRequest request_1 = new WSHentEnhetBolkRequest()
                .withEnhetIdListe("1234");

        final WSHentEnhetBolkRequest request_2 = new WSHentEnhetBolkRequest()
                .withEnhetIdListe("4321");

        enhetWS.hentEnhetBolk(request_1);
        enhetWS.hentEnhetBolk(request_2);
        enhetWS.hentEnhetBolk(request_1);
        enhetWS.hentEnhetBolk(request_2);

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getCache().getKeys().size(), is(2));
    }
}
