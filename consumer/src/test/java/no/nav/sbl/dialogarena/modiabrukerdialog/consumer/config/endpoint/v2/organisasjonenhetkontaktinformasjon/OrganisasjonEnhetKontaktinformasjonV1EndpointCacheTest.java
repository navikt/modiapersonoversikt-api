package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon;


import net.sf.ehcache.Cache;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Config.class})
public class OrganisasjonEnhetKontaktinformasjonV1EndpointCacheTest extends CacheTest {

    public static final String CACHE_NAME = "organisasjonEnhetKontaktinformasjonCache";
    public static final String ENHET_ID = "1337";
    public static final String ENHET_ID_OPPSLAG_2 = "1500";
    public static final String ENHETSNAVN = "NAV Molde";
    public static final String ENHETSNAVN_OPPSLAG_2 = "NAV Trondheim";

    @Inject
    private OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1;

    public OrganisasjonEnhetKontaktinformasjonV1EndpointCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    public void cacheEksisterer() {
        Cache organisasjonEnhetKontantinformasjonCache = getCache().getCacheManager().getCache(CACHE_NAME);
        assertThat(organisasjonEnhetKontantinformasjonCache, is(notNullValue()));
    }

    @Test
    public void gjorIkkeSammeOppslagToGangerPaRad() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));

        Cache cache = getCache().getCacheManager().getCache(CACHE_NAME);
        assertThat(cache.getSize(), is(1));
    }

    @Test
    public void gjorNyttOppslagHvisAnnenEnhetId() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID_OPPSLAG_2));

        Cache cache = getCache().getCacheManager().getCache(CACHE_NAME);
        assertThat(cache.getSize(), is(2));
    }

    private HentKontaktinformasjonForEnhetBolkRequest lagRequest(String enhetId) {
        HentKontaktinformasjonForEnhetBolkRequest request = new HentKontaktinformasjonForEnhetBolkRequest();
        request.getEnhetIdListe().add(enhetId);
        return request;
    }

}