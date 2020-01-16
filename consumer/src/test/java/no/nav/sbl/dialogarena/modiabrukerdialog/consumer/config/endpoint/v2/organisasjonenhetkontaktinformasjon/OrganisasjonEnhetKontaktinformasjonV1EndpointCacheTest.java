package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon;


import net.sf.ehcache.Cache;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.WSHentKontaktinformasjonForEnhetBolkRequest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

class OrganisasjonEnhetKontaktinformasjonV1EndpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "organisasjonEnhetKontaktinformasjonCache";
    private static final String ENHET_ID = "1337";
    private static final String ENHET_ID_OPPSLAG_2 = "1500";

    @Inject
    private OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1;

    OrganisasjonEnhetKontaktinformasjonV1EndpointCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    void cacheEksisterer() {
        Cache organisasjonEnhetKontantinformasjonCache = getCache().getCacheManager().getCache(CACHE_NAME);
        assertThat(organisasjonEnhetKontantinformasjonCache, is(notNullValue()));
    }

    @Test
    void gjorIkkeSammeOppslagToGangerPaRad() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));

        Cache cache = getCache().getCacheManager().getCache(CACHE_NAME);
        assertThat(cache.getSize(), is(1));
    }

    @Test
    void gjorNyttOppslagHvisAnnenEnhetId() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID_OPPSLAG_2));

        Cache cache = getCache().getCacheManager().getCache(CACHE_NAME);
        assertThat(cache.getSize(), is(2));
    }

    private WSHentKontaktinformasjonForEnhetBolkRequest lagRequest(String enhetId) {
        WSHentKontaktinformasjonForEnhetBolkRequest request = new WSHentKontaktinformasjonForEnhetBolkRequest();
        request.getEnhetIdListe().add(enhetId);
        return request;
    }

}