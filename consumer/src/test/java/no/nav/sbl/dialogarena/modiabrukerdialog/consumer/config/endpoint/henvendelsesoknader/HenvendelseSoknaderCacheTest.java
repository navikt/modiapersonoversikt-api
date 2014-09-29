package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader.HenvendelseSoknaderEndpointConfig.HENVENDELSESOKNADER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static java.lang.System.setProperty;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HenvendelseSoknaderEndpointConfig.class
})
public class HenvendelseSoknaderCacheTest extends CacheTest {

    public static final String CACHE_NAME = "endpointCache";

    @Inject
    private HenvendelseSoknaderPortType henvendelse;

    public HenvendelseSoknaderCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeClass
    public static void setup() {
        setProperty(HENVENDELSESOKNADER_KEY, "true");
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilHenvendelse() {
        String request1 = "string1";
        String request2 = "string2";

        henvendelse.hentSoknadListe(request1);
        henvendelse.hentSoknadListe(request1);
        henvendelse.hentSoknadListe(request2);
        henvendelse.hentSoknadListe(request2);

        int antallCacheinstanser = getCache().getSize();

        assertThat(antallCacheinstanser, is(2));
    }

}
