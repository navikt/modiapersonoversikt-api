package no.nav.sykmeldingsperioder.consumer.pleiepenger;

import no.nav.kjerneinfo.common.log.SporingUtils;
import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.common.SporingsLoggerFactory;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.PleiepengerMapper;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeResponse;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetUgyldigIdentNr;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPleiepengerettighet;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetRequest;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class PleiepengerServiceImpl implements PleiepengerService {

    private static final Logger logger = LoggerFactory.getLogger(PleiepengerServiceImpl.class);

    private final PleiepengerV1 pleiepengerV1;
    private final PleiepengerMapper mapper;

    public PleiepengerServiceImpl(PleiepengerV1 pleiepengerPortType) {
        this.pleiepengerV1 = pleiepengerPortType;
        this.mapper = new PleiepengerMapper();
    }

    @Override
    public PleiepengerListeResponse hentPleiepengerListe(PleiepengerListeRequest request) {
        WSHentPleiepengerettighetRequest rawRequest = mapper.map(request);
        try {
            return hentPleiepenger(rawRequest);
        } catch (HentPleiepengerettighetSikkerhetsbegrensning e) {
            return handterSikkerhetsbegresning(e);
        } catch (HentPleiepengerettighetUgyldigIdentNr hentPleiepengerettighetUgyldigIdentNr) {
            return handterUgyldigIdent(hentPleiepengerettighetUgyldigIdentNr);
        }
    }

    private PleiepengerListeResponse hentPleiepenger(WSHentPleiepengerettighetRequest rawRequest)
            throws HentPleiepengerettighetUgyldigIdentNr, HentPleiepengerettighetSikkerhetsbegrensning {
        WSHentPleiepengerettighetResponse rawResponse = ofNullable(pleiepengerV1.hentPleiepengerettighet(rawRequest))
                .orElseGet(WSHentPleiepengerettighetResponse::new);
        loggSporingsinformasjon(rawResponse.getPleiepengerettighetListe());
        return mapper.map(rawResponse);
    }

    private void loggSporingsinformasjon(List<WSPleiepengerettighet> pleiepengerrettighet) {
        getOmsorgsperson(pleiepengerrettighet).ifPresent(this::loggSporingsInformasjon);
    }

    private Optional<WSPerson> getOmsorgsperson(List<WSPleiepengerettighet> pleiepengerrettighet) {
        return ofNullable(pleiepengerrettighet)
                .flatMap(rettighet -> rettighet.stream()
                        .findFirst()
                        .map(WSPleiepengerettighet::getOmsorgsperson));
    }

    private void loggSporingsInformasjon(WSPerson bruker) {
        try {
            SporingsLogger sporingsLogger = SporingsLoggerFactory.sporingsLogger(SporingUtils.configFileAsBufferedReader(getConfigAsInputStream(), "sykmeldingsperioder-sporing-config.txt"));
            sporingsLogger.logg(bruker, SporingsAksjon.Les);
        } catch (Exception e) {
            logger.error("hentPleiepengerListe:SporingsLogger ble ikke opprettet.", e);
        }
    }

    private InputStream getConfigAsInputStream() throws IOException {
        return getClass().getClassLoader().getResource("sykmeldingsperioder-sporing-config.txt").openStream();
    }

    private PleiepengerListeResponse handterSikkerhetsbegresning(HentPleiepengerettighetSikkerhetsbegrensning e) {
        logger.warn("HentPleiepengerListeSikkerhetsbegrensning ved kall på hentPleiepengerListe", e.getMessage());
        throw new AuthorizationException(e.getMessage(), e);
    }

    private PleiepengerListeResponse handterUgyldigIdent(HentPleiepengerettighetUgyldigIdentNr e) {
        logger.warn("Ugyldig identnummer ved kall på hentPleiepengerListe", e.getMessage());
        throw new ApplicationException("Ugyldig identnummer ved kall på hentPleiepengerListe", e);
    }

}
