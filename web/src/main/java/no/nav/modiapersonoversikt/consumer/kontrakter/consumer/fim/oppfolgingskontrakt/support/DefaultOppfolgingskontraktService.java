package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.support;

import kotlin.Pair;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.modiapersonoversikt.infrastructure.core.exception.AuthorizationException;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingskontraktListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.singletonList;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for oppfolgingskontraker.
 */
public class DefaultOppfolgingskontraktService implements OppfolgingskontraktServiceBi {
    private static Audit.AuditDescriptor<WSHentOppfoelgingskontraktListeRequest> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Kontrakter,
            (person) -> singletonList(new Pair<>(AuditIdentifier.FNR, person.getPersonidentifikator()))
    );

    private OppfoelgingPortType oppfolgingskontraktService = null;
    private OppfolgingskontraktMapper mapper = null;
    private Logger logger = LoggerFactory.getLogger(getClass());

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
            logger.warn("HentOppfoelgingskontraktListeSikkerhetsbegrensning ved kall på hentOppfoelgingskontraktListe", hentOppfoelgingskontraktBegrensning.getMessage());
            auditLogger.denied("Årsak: " + hentOppfoelgingskontraktBegrensning.getMessage());
            throw new AuthorizationException(hentOppfoelgingskontraktBegrensning.getMessage(), hentOppfoelgingskontraktBegrensning);
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
