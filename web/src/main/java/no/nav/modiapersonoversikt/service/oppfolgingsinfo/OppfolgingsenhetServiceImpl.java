package no.nav.modiapersonoversikt.service.oppfolgingsinfo;

import no.nav.modiapersonoversikt.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.api.service.oppfolgingsinfo.OppfolgingsenhetService;
import no.nav.modiapersonoversikt.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusUgyldigInput;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingsstatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static no.nav.modiapersonoversikt.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.UFILTRERT;

public class OppfolgingsenhetServiceImpl implements OppfolgingsenhetService {

    private static final Logger logger = LoggerFactory.getLogger(OppfolgingsenhetServiceImpl.class);

    private final OppfoelgingPortType oppfoelgingPortType;
    private final OrganisasjonEnhetV2Service organisasjonEnhetV2Service;

    public OppfolgingsenhetServiceImpl(OppfoelgingPortType oppfoelgingPortType,
                                   OrganisasjonEnhetV2Service organisasjonEnhetV2Service) {
        this.oppfoelgingPortType = oppfoelgingPortType;
        this.organisasjonEnhetV2Service = organisasjonEnhetV2Service;
    }

    public Optional<AnsattEnhet> hentOppfolgingsenhet(String fodselsnummer) {
        return hentOppfoelgingsenhetId(fodselsnummer)
                .map(this::hentOppfolgingsenhetForEnhetId);
    }

    private AnsattEnhet hentOppfolgingsenhetForEnhetId(String enhetId) {
        return organisasjonEnhetV2Service.hentEnhetGittEnhetId(enhetId, UFILTRERT).orElse(null);
    }

    private Optional<String> hentOppfoelgingsenhetId(String fodselsnummer) {
        WSHentOppfoelgingsstatusRequest request = new WSHentOppfoelgingsstatusRequest();
        request.setPersonidentifikator(fodselsnummer);
        try {
            return ofNullable(oppfoelgingPortType.hentOppfoelgingsstatus(request).getNavOppfoelgingsenhet());
        } catch (HentOppfoelgingsstatusPersonIkkeFunnet hentOppfoelgingsstatusPersonIkkeFunnet) {
            return empty();
        } catch (HentOppfoelgingsstatusUgyldigInput | HentOppfoelgingsstatusSikkerhetsbegrensning e) {
            logger.error("Feil ved henting av oppfølgingsstatus", e);
            throw new IllegalStateException(e);
        }
    }

}
