package no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger;

import kotlin.Pair;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.Audit;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditResources;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.PleiepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.to.PleiepengerListeResponse;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetUgyldigIdentNr;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPleiepengerettighet;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetRequest;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

public class PleiepengerServiceImpl implements PleiepengerService {
    private static final Audit.AuditDescriptor<WSPerson> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Pleiepenger,
            (person) -> singletonList(new Pair<>(AuditIdentifier.FNR, ofNullable(person).map(WSPerson::getIdent).orElse("--")))
    );
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
        getOmsorgsperson(pleiepengerrettighet).ifPresent(auditLogger::log);
    }

    private Optional<WSPerson> getOmsorgsperson(List<WSPleiepengerettighet> pleiepengerrettighet) {
        return ofNullable(pleiepengerrettighet)
                .flatMap(rettighet -> rettighet.stream()
                        .findFirst()
                        .map(WSPleiepengerettighet::getOmsorgsperson));
    }

    private PleiepengerListeResponse handterSikkerhetsbegresning(HentPleiepengerettighetSikkerhetsbegrensning e) {
        logger.warn("HentPleiepengerListeSikkerhetsbegrensning ved kall på hentPleiepengerListe", e);
        throw new RuntimeException(e.getMessage(), e);
    }

    private PleiepengerListeResponse handterUgyldigIdent(HentPleiepengerettighetUgyldigIdentNr e) {
        logger.warn("Ugyldig identnummer ved kall på hentPleiepengerListe", e);
        throw new RuntimeException("Ugyldig identnummer ved kall på hentPleiepengerListe", e);
    }

}
