package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt.support;

import kotlin.Pair;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import no.nav.modiapersonoversikt.infrastructure.core.exception.AuthorizationException;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.HentYtelseskontraktListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeRequest;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.FimHentYtelseskontraktListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.singletonList;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for ytelseskontrakter.
 */
public class DefaultYtelseskontraktService implements YtelseskontraktServiceBi {
    private static Audit.AuditDescriptor<FimHentYtelseskontraktListeRequest> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Ytelser,
            (ytelse) -> singletonList(new Pair<>(AuditIdentifier.FNR, ytelse.getPersonidentifikator()))
    );

    private YtelseskontraktV3 ytelseskontraktService = null;
    private YtelseskontraktMapper mapper = null;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request) {

        FimHentYtelseskontraktListeRequest rawRequest = mapper.map(request);
        FimHentYtelseskontraktListeResponse rawResponse = null;
        try {
            rawResponse = ytelseskontraktService.hentYtelseskontraktListe(rawRequest);
            if (!CollectionUtils.isEmpty(rawResponse.getYtelseskontraktListe())) {
                auditLogger.log(rawRequest);
            }
        } catch (HentYtelseskontraktListeSikkerhetsbegrensning hentYtelseskontraktBegrensning) {
            logger.warn("HentYtelseskontraktListeSikkerhetsbegrensning ved kall på hentYtelseskontraktListe", hentYtelseskontraktBegrensning.getMessage());
            auditLogger.denied("Årsak: " + hentYtelseskontraktBegrensning.getMessage());
            throw new AuthorizationException(hentYtelseskontraktBegrensning.getMessage(), hentYtelseskontraktBegrensning);
        }

        return mapper.map(rawResponse);
    }

    public void setYtelseskontraktService(YtelseskontraktV3 ytelseskontraktService) {
        this.ytelseskontraktService = ytelseskontraktService;
    }

    public void setMapper(YtelseskontraktMapper mapper) {
        this.mapper = mapper;
    }


}
