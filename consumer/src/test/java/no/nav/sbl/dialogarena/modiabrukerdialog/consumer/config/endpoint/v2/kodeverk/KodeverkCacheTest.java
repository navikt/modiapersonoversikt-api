package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.kodeverk;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

class KodeverkCacheTest extends CacheTest {

    private static final String CACHE_NAME = "kodeverkCache";

    @Inject
    private KodeverkPortType kodeverk;

    KodeverkCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    void cacheManager_harEntryForKodeverk_etterKallTilKodeverk() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        XMLHentKodeverkRequest request1 = new XMLHentKodeverkRequest().withNavn("navn1");
        XMLHentKodeverkRequest request2 = new XMLHentKodeverkRequest().withNavn("navn2");

        kodeverk.hentKodeverk(request1);
        kodeverk.hentKodeverk(request1);
        kodeverk.hentKodeverk(request2);
        kodeverk.hentKodeverk(request2);

        int antallCacheinstanser = getCache().getSize();

        assertThat(antallCacheinstanser, is(2));
    }
}
