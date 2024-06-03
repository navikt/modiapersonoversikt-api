package no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt;

import kotlin.Pair;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.Audit;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditResources;
import no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktRequest;
import no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktResponse;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingskontraktListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for oppfolgingskontraker.
 */
@CacheConfig(cacheNames=("oppfolgingCache"), keyGenerator = "userkeygenerator")
public class OppfolgingskontraktServiceImpl implements OppfolgingskontraktService {
    private static final Audit.AuditDescriptor<WSHentOppfoelgingskontraktListeRequest> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Kontrakter,
            (person) -> singletonList(new Pair<>(AuditIdentifier.FNR, ofNullable(person).map(WSHentOppfoelgingskontraktListeRequest::getPersonidentifikator).orElse("--")))
    );

    private OppfoelgingPortType oppfolgingskontraktService = null;
    private OppfolgingskontraktMapper mapper = null;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Cacheable
    @Override
    public OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request) {
        WSHentOppfoelgingskontraktListeRequest rawRequest = mapper.map(request);
        WSHentOppfoelgingskontraktListeResponse rawResponse = null;
        try {
            rawResponse = oppfolgingskontraktService.hentOppfoelgingskontraktListe(rawRequest);
            if (!CollectionUtils.isEmpty(rawResponse.getOppfoelgingskontraktListe())) {
                auditLogger.log(rawRequest);
            }
        } catch (HentOppfoelgingskontraktListeSikkerhetsbegrensning hentOppfoelgingskontraktBegrensning) {
            logger.warn("HentOppfoelgingskontraktListeSikkerhetsbegrensning ved kall på hentOppfoelgingskontraktListe", hentOppfoelgingskontraktBegrensning);
            auditLogger.denied("Årsak: " + hentOppfoelgingskontraktBegrensning.getMessage());
            throw new RuntimeException(hentOppfoelgingskontraktBegrensning.getMessage(), hentOppfoelgingskontraktBegrensning);
        }

        return mapper.map(rawResponse);
    }

    public void setOppfolgingskontraktService(OppfoelgingPortType oppfolgingskontraktService) {
        this.oppfolgingskontraktService = oppfolgingskontraktService;
    }

    public void setMapper(OppfolgingskontraktMapper mapper) {
        this.mapper = mapper;
    }


}
