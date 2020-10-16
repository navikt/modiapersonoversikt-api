package no.nav.sykmeldingsperioder.consumer.foreldrepenger;

import kotlin.Pair;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sbl.dialogarena.naudit.Audit;
import no.nav.sbl.dialogarena.naudit.AuditIdentifier;
import no.nav.sbl.dialogarena.naudit.AuditResources;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.HentForeldrepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimPerson;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetRequest;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten for foreldrepenger.
 */
public class DefaultForeldrepengerService implements ForeldrepengerServiceBi {
    private static Audit.AuditDescriptor<FimPerson> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Foreldrepenger,
            (person) -> singletonList(new Pair<>(AuditIdentifier.FNR, person.getIdent()))
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
            logger.warn("HentForeldrepengerListeSikkerhetsbegrensning ved kall på hentForeldrepengerListe", ex.getMessage());
            auditLogger.denied("Årsak: " + ex.getMessage());
            throw new AuthorizationException(ex.getMessage(), ex);
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
