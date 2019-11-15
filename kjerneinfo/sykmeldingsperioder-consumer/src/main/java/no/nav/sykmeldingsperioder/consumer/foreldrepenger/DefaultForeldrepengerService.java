package no.nav.sykmeldingsperioder.consumer.foreldrepenger;

import no.nav.kjerneinfo.common.log.SporingUtils;
import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.common.SporingsLoggerFactory;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.HentForeldrepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimPerson;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetRequest;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for foreldrepenger.
 */
public class DefaultForeldrepengerService implements ForeldrepengerServiceBi {

    private static final Logger logger = LoggerFactory.getLogger(DefaultForeldrepengerService.class);
    private ForeldrepengerV2 foreldrepengerService;
    private ForeldrepengerMapper mapper;

    @Override
    public ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request) {

        FimHentForeldrepengerettighetRequest rawRequest = mapper.map(request, FimHentForeldrepengerettighetRequest.class);
        FimHentForeldrepengerettighetResponse rawResponse = null;
        try {
            rawResponse = foreldrepengerService.hentForeldrepengerettighet(rawRequest);
            if (rawResponse != null && rawResponse.getForeldrepengerettighet() != null && rawResponse.getForeldrepengerettighet().getForelder() != null) {
                logSporingsInformasjon(rawResponse.getForeldrepengerettighet().getForelder());
            }
        } catch (HentForeldrepengerettighetSikkerhetsbegrensning ex) {
            logger.warn("HentForeldrepengerListeSikkerhetsbegrensning ved kall på hentForeldrepengerListe", ex.getMessage());
            throw new AuthorizationException(ex.getMessage(), ex);
        }
        return mapper.map(rawResponse, ForeldrepengerListeResponse.class);
    }

    private void logSporingsInformasjon(FimPerson bruker) {
        try {
            SporingsLogger sporingsLogger = SporingsLoggerFactory.sporingsLogger(SporingUtils.configFileAsBufferedReader(getConfigAsInputStream(), "sykmeldingsperioder-sporing-config.txt"));
            sporingsLogger.logg(bruker, SporingsAksjon.Les);
        } catch (Exception e) {
            logger.error("hentForeldrepengerListe:SporingsLogger ble ikke opprettet.", e);
        }
    }

    private InputStream getConfigAsInputStream() throws IOException {
        return getClass().getClassLoader().getResource("sykmeldingsperioder-sporing-config.txt").openStream();
    }

    public void setMapper(ForeldrepengerMapper mapper) {
        this.mapper = mapper;
    }

    public void setForeldrepengerService(ForeldrepengerV2 foreldrepengerService) {
        this.foreldrepengerService = foreldrepengerService;
    }


}
