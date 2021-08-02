package no.nav.modiapersonoversikt.config.endpoint.v2.organisasjonenhet;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSDiskresjonskoder;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSGeografi;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSFinnNAVKontorRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSHentEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSHentFullstendigEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.WSHentOverordnetEnhetListeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class OrganisasjonEnhetV2EndpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "organisasjonEnhetV2";

    @Autowired
    private OrganisasjonEnhetV2 organisasjonEnhetService;

    OrganisasjonEnhetV2EndpointCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    void cacheManagerHarEntryForEndpointCacheEtterKallTilEnhet() {
        final WSHentEnhetBolkRequest request_1 = new WSHentEnhetBolkRequest();
        request_1.getEnhetIdListe().add("1234");

        final WSHentEnhetBolkRequest request_2 = new WSHentEnhetBolkRequest();
        request_2.getEnhetIdListe().add("4321");

        organisasjonEnhetService.hentEnhetBolk(request_1);
        organisasjonEnhetService.hentEnhetBolk(request_2);
        organisasjonEnhetService.hentEnhetBolk(request_1);
        organisasjonEnhetService.hentEnhetBolk(request_2);

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }

    @Test
    void cacheManagerCacherKallTilFinnNAVKontor() throws FinnNAVKontorUgyldigInput {
        final WSFinnNAVKontorRequest request_1 = lagFinnNAVKontorRequest("1234", "1234");
        final WSFinnNAVKontorRequest request_2 = lagFinnNAVKontorRequest("4231", "4231");

        organisasjonEnhetService.finnNAVKontor(request_1);
        organisasjonEnhetService.finnNAVKontor(request_2);
        organisasjonEnhetService.finnNAVKontor(request_1);
        organisasjonEnhetService.finnNAVKontor(request_2);

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getNativeCache().estimatedSize(), is(2L));
    }

    @Test
    void cacheKeysSkalVareUnikeForUlikeMetoder() {
        verifyUniqueAndStableCacheKeys(
                () -> organisasjonEnhetService.hentFullstendigEnhetListe(new WSHentFullstendigEnhetListeRequest()),
                () -> organisasjonEnhetService.hentOverordnetEnhetListe(new WSHentOverordnetEnhetListeRequest()),
                () -> organisasjonEnhetService.finnNAVKontor(new WSFinnNAVKontorRequest()),
                () -> organisasjonEnhetService.hentEnhetBolk(new WSHentEnhetBolkRequest())
        );
    }

    private WSFinnNAVKontorRequest lagFinnNAVKontorRequest(String geografiskTilhorighet, String diskresjonskode) {
        WSFinnNAVKontorRequest request = new WSFinnNAVKontorRequest();
        WSGeografi geografi = new WSGeografi();
        geografi.setValue(geografiskTilhorighet);
        request.setGeografiskTilknytning(geografi);
        WSDiskresjonskoder diskresjonskoder = new WSDiskresjonskoder();
        diskresjonskoder.setValue(diskresjonskode);
        request.setDiskresjonskode(diskresjonskoder);

        return request;
    }
}
