package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.domain.Oppfolgingsinfo;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse;

public class OppfolgingsinfoServiceImpl implements OppfolgingsinfoService{

    private final OppfolgingsinfoV1 oppfolgingsinfoV1;
    private final LDAPService ldapService;

    public OppfolgingsinfoServiceImpl(OppfolgingsinfoV1 oppfolgingsinfoV1, LDAPService ldapService) {
        this.oppfolgingsinfoV1 = oppfolgingsinfoV1;
        this.ldapService = ldapService;
    }

    public Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer) {
        String aktoerId = mapTilAktoerId(fodselsnummer);

        OppfolgingsstatusResponse oppfolgingsstatusResponse = hentOppfolgingsstatus(aktoerId);
        Saksbehandler saksbehandler = hentSaksbehandler(oppfolgingsstatusResponse.getWsOppfolgingsdata().getVeilederIdent());

        return new Oppfolgingsinfo(oppfolgingsstatusResponse.getWsOppfolgingsdata(), saksbehandler);
    }

    private Saksbehandler hentSaksbehandler(String veilederIdent) {
        return ldapService.hentSaksbehandler(veilederIdent);
    }

    private String mapTilAktoerId(String fodselsnummer) {
        return fodselsnummer;
    }

    private OppfolgingsstatusResponse hentOppfolgingsstatus(String aktorId) {
        return oppfolgingsinfoV1.hentOppfolgingsstatus(lagRequest(aktorId));
    }

    private OppfolgingsstatusRequest lagRequest(String aktorId) {
        return new OppfolgingsstatusRequest().withAktorId(aktorId);
    }

}
