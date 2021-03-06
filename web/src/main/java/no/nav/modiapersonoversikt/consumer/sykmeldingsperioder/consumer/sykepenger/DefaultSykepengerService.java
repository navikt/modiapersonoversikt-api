package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger;

import kotlin.Pair;
import no.nav.modiapersonoversikt.infrastructure.core.exception.AuthorizationException;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.mapping.SykepengerMapper;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.tjeneste.virksomhet.sykepenger.v2.HentSykepengerListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykBruker;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeRequest;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.singletonList;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for sykmeldingsperioder.
 */
public class DefaultSykepengerService implements SykepengerServiceBi {
    private static Audit.AuditDescriptor<FimsykBruker> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Sykepenger,
            (person) -> singletonList(new Pair<>(AuditIdentifier.FNR, person.getIdent()))
    );
    private static final Logger logger = LoggerFactory.getLogger(DefaultSykepengerService.class);
    private SykepengerV2 sykepengerService;
    private SykepengerMapper mapper;

    @Override
    public SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request) {
        FimHentSykepengerListeRequest rawRequest = mapper.map(request);
        FimHentSykepengerListeResponse rawResponse = null;
        try {
            rawResponse = sykepengerService.hentSykepengerListe(rawRequest);
            if (rawResponse != null && !CollectionUtils.isEmpty(rawResponse.getSykmeldingsperiodeListe())) {
                auditLogger.log(rawResponse.getSykmeldingsperiodeListe().get(0).getSykmeldt());
            }
        } catch (HentSykepengerListeSikkerhetsbegrensning ex) {
            logger.warn("HentSykepengerListeSikkerhetsbegrensning ved kall på hentSykepengerListe", ex.getMessage());
            auditLogger.denied("Årsak: " + ex.getMessage());
            throw new AuthorizationException(ex.getMessage(), ex);
        }
        return mapper.map(rawResponse);
    }

    public void setMapper(SykepengerMapper mapper) {
        this.mapper = mapper;
    }

    public void setSykepengerService(SykepengerV2 sykepengerService) {
        this.sykepengerService = sykepengerService;
    }


}
