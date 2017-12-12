package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.rest.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusUgyldigInput;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.HentOppfoelgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.UFILTRERT;

public class OppfolgingsinfoServiceImpl implements OppfolgingsinfoService {

    private static final Logger logger = LoggerFactory.getLogger(OppfolgingsinfoServiceImpl.class);

    private final OppfolgingsinfoV1 oppfolgingsinfoV1;
    private final LDAPService ldapService;
    private final AktoerPortType aktoerPortType;
    private final OppfoelgingPortType oppfoelgingPortType;
    private final OrganisasjonEnhetV2Service organisasjonEnhetV2Service;

    public OppfolgingsinfoServiceImpl(OppfolgingsinfoV1 oppfolgingsinfoV1, LDAPService ldapService,
                                      AktoerPortType aktoerPortType, OppfoelgingPortType oppfoelgingPortType,
                                      OrganisasjonEnhetV2Service organisasjonEnhetV2Service) {
        this.oppfolgingsinfoV1 = oppfolgingsinfoV1;
        this.ldapService = ldapService;
        this.aktoerPortType = aktoerPortType;
        this.oppfoelgingPortType = oppfoelgingPortType;
        this.organisasjonEnhetV2Service = organisasjonEnhetV2Service;
    }

    public Optional<Oppfolgingsinfo> hentOppfolgingsinfo(String fodselsnummer) {
        String aktoerId = mapTilAktoerId(fodselsnummer);

        WSOppfolgingsdata oppfolgingsdata = hentOppfolgingsstatus(aktoerId);

        if (oppfolgingsdata == null) {
            return empty();
        }

        Saksbehandler saksbehandler = ofNullable(oppfolgingsdata.getVeilederIdent())
                .map(this::hentSaksbehandler)
                .orElse(null);

        AnsattEnhet oppfolgingsenhet = hentOppfoelgingsenhetId(fodselsnummer)
                .map(this::hentOppfolgingsenhet)
                .orElse(null);

        return Optional.of(new Oppfolgingsinfo(oppfolgingsdata.isErUnderOppfolging(), saksbehandler, oppfolgingsenhet));
    }

    private AnsattEnhet hentOppfolgingsenhet(String enhetId) {
        return organisasjonEnhetV2Service.hentEnhetGittEnhetId(enhetId, UFILTRERT).orElse(null);
    }

    private Optional<String> hentOppfoelgingsenhetId(String fodselsnummer) {
        HentOppfoelgingsstatusRequest request = new HentOppfoelgingsstatusRequest();
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

    private Saksbehandler hentSaksbehandler(String veilederIdent) {
        return ldapService.hentSaksbehandler(veilederIdent);
    }

    private String mapTilAktoerId(String fodselsnummer) {
        try {
            return aktoerPortType.hentAktoerIdForIdent(new HentAktoerIdForIdentRequest(fodselsnummer)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            logger.error("Fant ikke aktør-id for fnr: " + fodselsnummer);
            throw new RuntimeException(hentAktoerIdForIdentPersonIkkeFunnet);
        }
    }

    private WSOppfolgingsdata hentOppfolgingsstatus(String aktorId) {
        OppfolgingsstatusResponse oppfolgingsstatusResponse = ofNullable(oppfolgingsinfoV1.hentOppfolgingsstatus(lagRequest(aktorId)))
                .orElseThrow(() -> new IllegalStateException("Oppfolgingsinfo returnerte null"));

        if (oppfolgingsstatusResponse.getWsSikkerhetsbegrensning() != null) {
            throw new RuntimeException("Saksbehandler har ikke tilgang til bruker med aktor-id: " + aktorId);
        }

        return oppfolgingsstatusResponse.getWsOppfolgingsdata();
    }

    private OppfolgingsstatusRequest lagRequest(String aktorId) {
        return new OppfolgingsstatusRequest().withAktorId(aktorId);
    }

}
