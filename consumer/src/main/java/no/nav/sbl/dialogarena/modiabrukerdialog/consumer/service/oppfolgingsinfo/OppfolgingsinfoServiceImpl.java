package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.rest.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsenhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoService;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class OppfolgingsinfoServiceImpl implements OppfolgingsinfoService {

    private static final Logger logger = LoggerFactory.getLogger(OppfolgingsinfoServiceImpl.class);

    private final OppfolgingsinfoV1 oppfolgingsinfoV1;
    private final LDAPService ldapService;
    private final AktoerPortType aktoerPortType;
    private final OppfolgingsenhetService oppfolgingsenhetService;

    public OppfolgingsinfoServiceImpl(OppfolgingsinfoV1 oppfolgingsinfoV1, LDAPService ldapService,
                                      AktoerPortType aktoerPortType, OppfolgingsenhetService oppfolgingsenhetService) {
        this.oppfolgingsinfoV1 = oppfolgingsinfoV1;
        this.ldapService = ldapService;
        this.aktoerPortType = aktoerPortType;
        this.oppfolgingsenhetService = oppfolgingsenhetService;
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

        AnsattEnhet oppfolgingsenhet = oppfolgingsenhetService.hentOppfolgingsenhet(fodselsnummer)
                .orElse(null);

        return Optional.of(new Oppfolgingsinfo(oppfolgingsdata.isErUnderOppfolging(), saksbehandler, oppfolgingsenhet));
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
