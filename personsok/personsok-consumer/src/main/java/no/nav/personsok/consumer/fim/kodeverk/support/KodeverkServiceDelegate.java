package no.nav.personsok.consumer.fim.kodeverk.support;

import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

/**
 * Delegate that handles web services calls.
 * <p/>
 * Separation is required for caching to work, as internal method calls cannot be cached using annotations.
 */
public class KodeverkServiceDelegate {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKodeverkManager.class);
    private KodeverkPortType kodeverkPortType;

    public void setKodeverkPortType(KodeverkPortType kodeverkPortType) {
        this.kodeverkPortType = kodeverkPortType;
    }

    /**
     * Retrieves kodeverk from the kodeverk service.
     * <p/>
     * This method fails silently.
     *
     * @param request
     * @return kodeverk if exists, else empty kodeverk.
     */
    @Cacheable(value = "kodeverk_consumer.kodeverkCache", key = "#request.getNavn()")
    public XMLHentKodeverkResponse hentKodeverk(XMLHentKodeverkRequest request) {
        try {
            XMLHentKodeverkResponse xmlHentKodeverkResponse = kodeverkPortType.hentKodeverk(request);
            return xmlHentKodeverkResponse;
        } catch (HentKodeverkHentKodeverkKodeverkIkkeFunnet ex) {
            logger.warn("Attempt to retrieve a nonexisting kodeverk {}", request.getNavn(), ex);
        } catch (Exception ex) {
            logger.error("Exception while retrieving kodeverk {}", request.getNavn(), ex);
        }
        return new XMLHentKodeverkResponse().withKodeverk(new XMLEnkeltKodeverk());
    }
}
