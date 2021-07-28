package no.nav.modiapersonoversikt.config.endpoint.v4.organisasjon;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentNoekkelinfoOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.common.utils.SslUtils.setupTruststore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrganisasjonEnpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "organisasjonCache";

    @Autowired
    private OrganisasjonV4 organisasjon;

    OrganisasjonEnpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeAll
    static void setup() {
        setupTruststore();
    }

    @Test
    void cacheManager_harEntryForEndpointCache_etterKallTilHentNoekkelinfo() throws HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet, HentNoekkelinfoOrganisasjonUgyldigInput {
        WSHentNoekkelinfoOrganisasjonRequest req1 = new WSHentNoekkelinfoOrganisasjonRequest()
                .withOrgnummer("123456789");
        WSHentNoekkelinfoOrganisasjonRequest req2 = new WSHentNoekkelinfoOrganisasjonRequest()
                .withOrgnummer("123456789");

        when(organisasjon.hentNoekkelinfoOrganisasjon(any(WSHentNoekkelinfoOrganisasjonRequest.class))).thenReturn(
                new WSHentNoekkelinfoOrganisasjonResponse().withOrgnummer("first"),
                new WSHentNoekkelinfoOrganisasjonResponse().withOrgnummer("second")
        );

        String resp1 = organisasjon.hentNoekkelinfoOrganisasjon(req1).getOrgnummer();
        String resp2 = organisasjon.hentNoekkelinfoOrganisasjon(req2).getOrgnummer();

        assertThat(resp1, is(resp2));
    }

    @Test
    void cacheKeysSkalVareUnikeForUlikeMetoder() {
        verifyUniqueAndStableCacheKeys(
                () -> organisasjon.hentOrganisasjon(new WSHentOrganisasjonRequest()),
                () -> organisasjon.hentOrganisasjonsnavnBolk(new WSHentOrganisasjonsnavnBolkRequest()),
                () -> organisasjon.finnOrganisasjonsendringerListe(new WSFinnOrganisasjonsendringerListeRequest()),
                () -> organisasjon.finnOrganisasjon(new WSFinnOrganisasjonRequest()),
                () -> organisasjon.hentNoekkelinfoOrganisasjon(new WSHentNoekkelinfoOrganisasjonRequest()),
                () -> organisasjon.hentVirksomhetsOrgnrForJuridiskOrgnrBolk(new WSHentVirksomhetsOrgnrForJuridiskOrgnrBolkRequest())
        );
    }
}
