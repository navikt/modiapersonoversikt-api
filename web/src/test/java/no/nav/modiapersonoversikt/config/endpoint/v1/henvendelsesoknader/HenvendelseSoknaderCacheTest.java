package no.nav.modiapersonoversikt.config.endpoint.v1.henvendelsesoknader;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class HenvendelseSoknaderCacheTest extends CacheTest {

    public static final String CACHE_NAME = "endpointCache";

    @Autowired
    private HenvendelseSoknaderPortType henvendelse;

    public HenvendelseSoknaderCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilHenvendelse() {
        String request1 = "string1";
        String request2 = "string2";

        henvendelse.hentSoknadListe(request1);
        henvendelse.hentSoknadListe(request1);
        henvendelse.hentSoknadListe(request2);
        henvendelse.hentSoknadListe(request2);

        assertThat(getNativeCache().estimatedSize(), is(2L));
    }

}
