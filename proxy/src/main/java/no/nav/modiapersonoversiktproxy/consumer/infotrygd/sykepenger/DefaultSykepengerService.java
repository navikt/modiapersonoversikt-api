package no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger;

import kotlin.Pair;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.SykepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerResponse;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.Audit;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditResources;
import no.nav.tjeneste.virksomhet.sykepenger.v2.HentSykepengerListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import no.nav.tjeneste.virksomhet.sykepenger.v2.informasjon.FimsykBruker;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeRequest;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for sykmeldingsperioder.
 */
public class DefaultSykepengerService implements SykepengerServiceBi {
    private static final Audit.AuditDescriptor<FimsykBruker> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Sykepenger,
            (person) -> singletonList(new Pair<>(AuditIdentifier.FNR, ofNullable(person).map(FimsykBruker::getIdent).orElse("--")))
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
            logger.warn("HentSykepengerListeSikkerhetsbegrensning ved kall på hentSykepengerListe", ex);
            auditLogger.denied("Årsak: " + ex.getMessage());
            throw new RuntimeException(ex.getMessage(), ex);
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
