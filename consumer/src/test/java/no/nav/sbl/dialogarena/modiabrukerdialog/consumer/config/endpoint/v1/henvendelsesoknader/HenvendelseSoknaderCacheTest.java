package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.henvendelsesoknader;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HenvendelseSoknaderEndpointConfig.class})
public class HenvendelseSoknaderCacheTest extends CacheTest {

    public static final String CACHE_NAME = "endpointCache";

    @Inject
    private HenvendelseSoknaderPortType henvendelse;

    public HenvendelseSoknaderCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeAll
    public static void setup() {
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
