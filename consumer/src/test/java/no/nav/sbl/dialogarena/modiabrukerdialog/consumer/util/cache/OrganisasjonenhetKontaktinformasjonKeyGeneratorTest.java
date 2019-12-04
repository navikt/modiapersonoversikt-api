package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.WSHentKontaktinformasjonForEnhetBolkRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class OrganisasjonenhetKontaktinformasjonKeyGeneratorTest {

    private OrganisasjonenhetKontaktinformasjonKeyGenerator generator;
    private Method method;

    @BeforeEach
    void beforeEach() throws NoSuchMethodException {
        generator = new OrganisasjonenhetKontaktinformasjonKeyGenerator();
        method = OrganisasjonEnhetKontaktinformasjonV1.class.getMethod("hentKontaktinformasjonForEnhetBolk", WSHentKontaktinformasjonForEnhetBolkRequest.class);
    }

    @Test
    @DisplayName("Null som argument til generate skal kaste feil")
    void nullParamkasterFeil() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.generate(mock(OrganisasjonEnhetKontaktinformasjonV1.class), method);
        });
    }

    @Test
    @DisplayName("Et annet objekt som argument til generate skal kaste feil")
    void feilObjektKasterFeil() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.generate(mock(OrganisasjonEnhetKontaktinformasjonV1.class), method, "String objekt");
        });
    }

    @Test
    @DisplayName("Hvis ingen enhet er spesifisert, returneres en tom key")
    void ingenEnhetKasterFeil() {
        String key = (String) generator.generate(mock(OrganisasjonEnhetKontaktinformasjonV1.class), method, new WSHentKontaktinformasjonForEnhetBolkRequest());

        assertEquals("", key);
    }

    @Test
    @DisplayName("Key for en enkelt enhet")
    void lagerKeyForEnEnhet() throws NoSuchMethodException {
        String key = (String) generator.generate(mock(OrganisasjonEnhetKontaktinformasjonV1.class), method, lagRequest("1337"));

        assertEquals("1337", key);
    }

    @Test
    @DisplayName("Key for tre enheter i request")
    void lagerKeyForTreEnheter() throws NoSuchMethodException {
        String key = (String) generator.generate(mock(OrganisasjonEnhetKontaktinformasjonV1.class), method, lagRequest("1337", "2000", "3001"));

        assertEquals("1337,2000,3001", key);
    }

    @Test
    @DisplayName("Key for tre enheter i request er sortert numerisk")
    void lagerSortertKeyForTreEnheter() throws NoSuchMethodException {
        String key = (String) generator.generate(mock(OrganisasjonEnhetKontaktinformasjonV1.class), method, lagRequest("3001", "2000", "15337"));

        assertEquals("2000,3001,15337", key);
    }

    private WSHentKontaktinformasjonForEnhetBolkRequest lagRequest(String... enhetIder) {
        WSHentKontaktinformasjonForEnhetBolkRequest request = new WSHentKontaktinformasjonForEnhetBolkRequest();
        for (String enhetId: enhetIder) {
            request.getEnhetIdListe().add(enhetId);
        }
        return request;
    }

}