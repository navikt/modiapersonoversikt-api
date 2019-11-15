package no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support;

import no.nav.kjerneinfo.common.log.SporingUtils;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.common.SporingsLoggerFactory;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingskontraktListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for oppfolgingskontraker.
 */
public class DefaultOppfolgingskontraktService implements OppfolgingskontraktServiceBi {

    private OppfoelgingPortType oppfolgingskontraktService = null;
    private OppfolgingskontraktMapper mapper = null;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request) {
        WSHentOppfoelgingskontraktListeRequest rawRequest = mapper.map(request, WSHentOppfoelgingskontraktListeRequest.class);
        WSHentOppfoelgingskontraktListeResponse rawResponse = null;
        try {
            rawResponse = oppfolgingskontraktService.hentOppfoelgingskontraktListe(rawRequest);
            if (!CollectionUtils.isEmpty(rawResponse.getOppfoelgingskontraktListe())) {
                logSporingsInformasjon(rawRequest);
            }
        } catch (HentOppfoelgingskontraktListeSikkerhetsbegrensning hentOppfoelgingskontraktBegrensning) {
            logger.warn("HentOppfoelgingskontraktListeSikkerhetsbegrensning ved kall på hentOppfoelgingskontraktListe", hentOppfoelgingskontraktBegrensning.getMessage());
            throw new AuthorizationException(hentOppfoelgingskontraktBegrensning.getMessage(), hentOppfoelgingskontraktBegrensning);
        }

        return mapper.map(rawResponse, OppfolgingskontraktResponse.class);
    }

    private void logSporingsInformasjon(WSHentOppfoelgingskontraktListeRequest bruker) {
        try {
            SporingsLogger sporingsLogger = SporingsLoggerFactory.sporingsLogger(SporingUtils.configFileAsBufferedReader(getConfigAsInputStream(), "kontrakter-sporing-config.txt"));
            sporingsLogger.logg(bruker, SporingsAksjon.Les);
        } catch (Exception e) {
            logger.error("hentOppfolgingskontrakter:SporingsLogger ble ikke opprettet.", e);
        }
    }

    private InputStream getConfigAsInputStream() throws IOException {
        return getClass().getClassLoader().getResource("kontrakter-sporing-config.txt").openStream();
    }

    public void setOppfolgingskontraktService(OppfoelgingPortType oppfolgingskontraktService) {
        this.oppfolgingskontraktService = oppfolgingskontraktService;
    }

    public void setMapper(OppfolgingskontraktMapper mapper) {
        this.mapper = mapper;
    }


}
