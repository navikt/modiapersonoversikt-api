package no.nav.sbl.dialogarena.common.kodeverk;


import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLSammensattKodeverk;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integrasjonstest for kodeverk. Bør sjekkes inn med @Ignore, siden bygget kan brekke hvis kodeverktjenesten går ned.
 *
 * Denne testen er fin å bruke lokalt for å se på data som mottas fra tjenesten. Fjern @Ignore lokalt for å kjøre.
 */
@Ignore
public class KodeverkIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(KodeverkIntegrationTest.class);

    private static final String KODEVERK_ENDPOINT = "https://modapp-t11.adeo.no/kodeverk-ws/Kodeverk/v2";

    private DefaultKodeverkClient defaultKodeverkClient;
    private CachingKodeverkClient cachingClient;

    private final File dumpDir = new File("target", "kodeverkdump/" + UUID.randomUUID().toString());

    @Before
    public void setUp() {
        defaultKodeverkClient = new DefaultKodeverkClient(createKodeverkPortType());
        cachingClient = new CachingKodeverkClient(defaultKodeverkClient, of(dumpDir));
    }

    @Test
    public void skalHenteKodeverk() {
        XMLEnkeltKodeverk kodeverk = (XMLEnkeltKodeverk) defaultKodeverkClient.hentKodeverk("Byer");
        logger.info(kodeverk.toString());
    }

    @Test
    public void skalHenteFoersteTermnavnForKode() {
        String termnavn = defaultKodeverkClient.hentFoersteTermnavnForKode("aa0013", "Tema");
        assertThat(termnavn, is("Forsikring"));
    }

    @Test
    public void skalHenteHierarkiskKodeverk() {
        XMLSammensattKodeverk kodeverk = (XMLSammensattKodeverk) defaultKodeverkClient.hentKodeverk("teste igjen");
        logger.info(kodeverk.toString());
    }

    @Test
    public void skalHenteKodeverkMedCaching() {
        XMLEnkeltKodeverk kodeverk = (XMLEnkeltKodeverk) cachingClient.hentKodeverk("Byer");
        logger.info(kodeverk.toString());
    }

    private KodeverkPortType createKodeverkPortType() {
        JaxWsProxyFactoryBean jaxwsClient = new JaxWsProxyFactoryBean();
        jaxwsClient.setServiceClass(KodeverkPortType.class);
        jaxwsClient.getFeatures().add(new LoggingFeature());
        jaxwsClient.setAddress(KODEVERK_ENDPOINT);
        jaxwsClient.setWsdlURL(classpathUrl("kodeverk/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl"));
        return jaxwsClient.create(KodeverkPortType.class);
    }

    private String classpathUrl(String classpathLocation) {
        if (getClass().getClassLoader().getResource(classpathLocation) == null) {
            throw new RuntimeException(classpathLocation + " does not exist on classpath!");
        }
        return "classpath:" + classpathLocation;
    }

}
