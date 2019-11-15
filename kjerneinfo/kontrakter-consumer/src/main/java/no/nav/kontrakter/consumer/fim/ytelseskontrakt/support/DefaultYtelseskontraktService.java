package no.nav.kontrakter.consumer.fim.ytelseskontrakt.support;

import no.nav.kjerneinfo.common.log.SporingUtils;
import no.nav.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.common.SporingsLoggerFactory;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.HentYtelseskontraktListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeRequest;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for ytelseskontrakter.
 */
public class DefaultYtelseskontraktService implements YtelseskontraktServiceBi {
    private YtelseskontraktV3 ytelseskontraktService = null;
    private YtelseskontraktMapper mapper = null;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request) {

        FimHentYtelseskontraktListeRequest rawRequest = mapper.map(request, FimHentYtelseskontraktListeRequest.class);
        FimHentYtelseskontraktListeResponse rawResponse = null;
        try {
            rawResponse = ytelseskontraktService.hentYtelseskontraktListe(rawRequest);
            if (!CollectionUtils.isEmpty(rawResponse.getYtelseskontraktListe())) {
                logSporingsInformasjon(rawRequest);
            }
        } catch (HentYtelseskontraktListeSikkerhetsbegrensning hentYtelseskontraktBegrensning) {
            logger.warn("HentYtelseskontraktListeSikkerhetsbegrensning ved kall på hentYtelseskontraktListe", hentYtelseskontraktBegrensning.getMessage());
            throw new AuthorizationException(hentYtelseskontraktBegrensning.getMessage(), hentYtelseskontraktBegrensning);
        }

        return mapper.map(rawResponse, YtelseskontraktResponse.class);
    }

    private void logSporingsInformasjon(FimHentYtelseskontraktListeRequest bruker) {
        try {
            SporingsLogger sporingsLogger = SporingsLoggerFactory.sporingsLogger(SporingUtils.configFileAsBufferedReader(getConfigAsInputStream(), "kontrakter-sporing-config.txt"));
            sporingsLogger.logg(bruker, SporingsAksjon.Les);
        } catch (Exception e) {
            logger.error("hentYtelseskontrakter:SporingsLogger ble ikke opprettet.", e);
        }
    }

    private InputStream getConfigAsInputStream() throws IOException {
        return getClass().getClassLoader().getResource("kontrakter-sporing-config.txt").openStream();
    }

    public void setYtelseskontraktService(YtelseskontraktV3 ytelseskontraktService) {
        this.ytelseskontraktService = ytelseskontraktService;
    }

    public void setMapper(YtelseskontraktMapper mapper) {
        this.mapper = mapper;
    }


}
