package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v4.organisasjon;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentNoekkelinfoOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentNoekkelinfoOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentNoekkelinfoOrganisasjonResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.test.ssl.SSLTestUtils.setupKeyAndTrustStore;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrganisasjonEnpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "endpointCache";

    @Inject
    private OrganisasjonV4 organisasjon;

    OrganisasjonEnpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeAll
    static void setup() {
        setupKeyAndTrustStore();
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
}
