package no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger;

import kotlin.Pair;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.Audit;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversiktproxy.infrastructure.naudit.AuditResources;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.HentForeldrepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimPerson;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetRequest;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for foreldrepenger.
 */
public class DefaultForeldrepengerService implements ForeldrepengerServiceBi {
    private static final Audit.AuditDescriptor<FimPerson> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Foreldrepenger,
            (person) -> singletonList(new Pair<>(AuditIdentifier.FNR, ofNullable(person).map(FimPerson::getIdent).orElse("--")))
    );
    private static final Logger logger = LoggerFactory.getLogger(DefaultForeldrepengerService.class);
    private ForeldrepengerV2 foreldrepengerService;
    private ForeldrepengerMapper mapper;

    @Override
    public ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request) {

        FimHentForeldrepengerettighetRequest rawRequest = mapper.map(request);
        FimHentForeldrepengerettighetResponse rawResponse = null;
        try {
            rawResponse = foreldrepengerService.hentForeldrepengerettighet(rawRequest);
            if (rawResponse != null && rawResponse.getForeldrepengerettighet() != null && rawResponse.getForeldrepengerettighet().getForelder() != null) {
                auditLogger.log(rawResponse.getForeldrepengerettighet().getForelder());
            }
        } catch (HentForeldrepengerettighetSikkerhetsbegrensning ex) {
            logger.warn("HentForeldrepengerListeSikkerhetsbegrensning ved kall på hentForeldrepengerListe", ex);
            auditLogger.denied("Årsak: " + ex.getMessage());
            throw new RuntimeException(ex.getMessage(), ex);
        }
        return mapper.map(rawResponse);
    }

    public void setMapper(ForeldrepengerMapper mapper) {
        this.mapper = mapper;
    }

    public void setForeldrepengerService(ForeldrepengerV2 foreldrepengerService) {
        this.foreldrepengerService = foreldrepengerService;
    }


}
