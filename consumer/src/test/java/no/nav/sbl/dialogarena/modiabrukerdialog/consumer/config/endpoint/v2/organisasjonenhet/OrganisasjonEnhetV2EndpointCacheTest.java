package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhet;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.Diskresjonskoder;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.Geografi;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.FinnNAVKontorRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.HentEnhetBolkRequest;
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
@ContextConfiguration(classes = {OrganisasjonEnhetV2EndpointConfig.class})
public class OrganisasjonEnhetV2EndpointCacheTest extends CacheTest {

    private static final String CACHE_NAME = "organisasjonEnhetV2";

    @Inject
    private OrganisasjonEnhetV2 organisasjonEnhetService;

    public OrganisasjonEnhetV2EndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeAll
    public static void setup() {
        setupKeyAndTrustStore();
        System.setProperty("no.nav.modig.security.sts.url", "");
        System.setProperty("no.nav.modig.security.systemuser.username", "");
        System.setProperty("no.nav.modig.security.systemuser.password", "");
    }

    @Test
    public void cacheManagerHarEntryForEndpointCacheEtterKallTilEnhet() {
        final HentEnhetBolkRequest request_1 = new HentEnhetBolkRequest();
        request_1.getEnhetIdListe().add("1234");

        final HentEnhetBolkRequest request_2 = new HentEnhetBolkRequest();
        request_2.getEnhetIdListe().add("1234");

        organisasjonEnhetService.hentEnhetBolk(request_1);
        organisasjonEnhetService.hentEnhetBolk(request_2);
        organisasjonEnhetService.hentEnhetBolk(request_1);
        organisasjonEnhetService.hentEnhetBolk(request_2);

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getCache().getKeys().size(), is(2));
    }

    @Test
    public void cacheManagerCacherKallTilFinnNAVKontor() throws FinnNAVKontorUgyldigInput {
        final FinnNAVKontorRequest request_1 = lagFinnNAVKontorRequest("1234", "1234");
        final FinnNAVKontorRequest request_2 = lagFinnNAVKontorRequest("4231", "4231");

        organisasjonEnhetService.finnNAVKontor(request_1);
        organisasjonEnhetService.finnNAVKontor(request_2);
        organisasjonEnhetService.finnNAVKontor(request_1);
        organisasjonEnhetService.finnNAVKontor(request_2);

        assertThat(getCache().getName(), is(CACHE_NAME));
        assertThat(getCache().getKeys().size(), is(2));
    }

    private FinnNAVKontorRequest lagFinnNAVKontorRequest(String geografiskTilhorighet, String diskresjonskode) {
        FinnNAVKontorRequest request = new FinnNAVKontorRequest();
        Geografi geografi = new Geografi();
        geografi.setValue(geografiskTilhorighet);
        request.setGeografiskTilknytning(geografi);
        Diskresjonskoder diskresjonskoder = new Diskresjonskoder();
        diskresjonskoder.setValue(diskresjonskode);
        request.setDiskresjonskode(diskresjonskoder);

        return request;
    }
}
