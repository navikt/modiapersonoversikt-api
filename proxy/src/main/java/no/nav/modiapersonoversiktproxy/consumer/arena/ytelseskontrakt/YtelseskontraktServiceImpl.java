package no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt;

import kotlin.Pair;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.Audit;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditResources;
import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain.YtelseskontraktRequest;
import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain.YtelseskontraktResponse;
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
public class YtelseskontraktServiceImpl implements YtelseskontraktService {
    private static final Audit.AuditDescriptor<FimHentYtelseskontraktListeRequest> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Ytelser,
            (ytelse) -> singletonList(new Pair<>(AuditIdentifier.FNR, ytelse.getPersonidentifikator()))
    );

    private YtelseskontraktV3 ytelseskontraktService = null;
    private YtelseskontraktMapper mapper = null;
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            logger.warn("HentYtelseskontraktListeSikkerhetsbegrensning ved kall på hentYtelseskontraktListe", hentYtelseskontraktBegrensning);
            auditLogger.denied("Årsak: " + hentYtelseskontraktBegrensning.getMessage());
            throw new RuntimeException(hentYtelseskontraktBegrensning.getMessage(), hentYtelseskontraktBegrensning);
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
