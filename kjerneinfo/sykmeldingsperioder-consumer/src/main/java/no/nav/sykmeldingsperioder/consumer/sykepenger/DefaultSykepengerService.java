package no.nav.sykmeldingsperioder.consumer.sykepenger;

import no.nav.kjerneinfo.common.log.SporingUtils;
import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.common.SporingsLoggerFactory;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.SykepengerMapper;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.tjeneste.virksomhet.sykepenger.v2.HentSykepengerListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykBruker;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeRequest;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for sykmeldingsperioder.
 */
public class DefaultSykepengerService implements SykepengerServiceBi {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSykepengerService.class);
    private SykepengerV2 sykepengerService;
    private SykepengerMapper mapper;

    @Override
    public SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request) {
        FimHentSykepengerListeRequest rawRequest = mapper.map(request, FimHentSykepengerListeRequest.class);
        FimHentSykepengerListeResponse rawResponse = null;
        try {
            rawResponse = sykepengerService.hentSykepengerListe(rawRequest);
            if (rawResponse != null && !CollectionUtils.isEmpty(rawResponse.getSykmeldingsperiodeListe())) {
                logSporingsInformasjon(rawResponse.getSykmeldingsperiodeListe().get(0).getSykmeldt());
            }
        } catch (HentSykepengerListeSikkerhetsbegrensning ex) {
            logger.warn("HentSykepengerListeSikkerhetsbegrensning ved kall på hentSykepengerListe", ex.getMessage());
            throw new AuthorizationException(ex.getMessage(), ex);
        }
        return mapper.map(rawResponse, SykepengerResponse.class);
    }

    private void logSporingsInformasjon(FimsykBruker bruker) {
        try {
            SporingsLogger sporingsLogger = SporingsLoggerFactory.sporingsLogger(SporingUtils.configFileAsBufferedReader(getConfigAsInputStream(), "sykmeldingsperioder-sporing-config.txt"));
            sporingsLogger.logg(bruker, SporingsAksjon.Les);
        } catch (Exception e) {
            logger.error("hentSykmeldingsperioder:SporingsLogger ble ikke opprettet.", e);
        }
    }

    private InputStream getConfigAsInputStream() throws IOException {
        return getClass().getClassLoader().getResource("sykmeldingsperioder-sporing-config.txt").openStream();
    }

    public void setMapper(SykepengerMapper mapper) {
        this.mapper = mapper;
    }

    public void setSykepengerService(SykepengerV2 sykepengerService) {
        this.sykepengerService = sykepengerService;
    }




}
