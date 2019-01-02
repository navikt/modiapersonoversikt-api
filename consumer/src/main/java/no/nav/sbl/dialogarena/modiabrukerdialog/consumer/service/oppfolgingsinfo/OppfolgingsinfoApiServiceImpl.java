package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.rest.OppfolgingsEnhetOgVeileder;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.rest.OppfolgingsStatus;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.rest.Oppfolgingsenhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.sbl.rest.RestUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class OppfolgingsinfoApiServiceImpl implements OppfolgingsinfoApiService {

    private String apiUrl;

    @Inject
    public OppfolgingsinfoApiServiceImpl(String apiUrl) {
        this.apiUrl = sluttMedSlash(apiUrl);
    }

    private String sluttMedSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    @Override
    public Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, LDAPService ldapService) {
        OppfolgingsStatus status = hentOppfolgingStatus(fodselsnummer);
        OppfolgingsEnhetOgVeileder enhetOgVeileder;
        if (status.isUnderOppfolging() == true) {
            enhetOgVeileder = hentOppfolgingsEnhetOgVeileder(fodselsnummer);
        } else {
            enhetOgVeileder = setTomEnhet();
        }

        return new Oppfolgingsinfo(status.isUnderOppfolging())
                .withVeileder(hentSaksbehandler(enhetOgVeileder.getVeilederId(), ldapService))
                .withOppfolgingsenhet(new AnsattEnhet(enhetOgVeileder.getOppfolgingsenhet().getEnhetId(), enhetOgVeileder.getOppfolgingsenhet().getNavn()));
    }

    @NotNull
    private OppfolgingsEnhetOgVeileder setTomEnhet() {
        OppfolgingsEnhetOgVeileder enhetOgVeileder;
        enhetOgVeileder = new OppfolgingsEnhetOgVeileder();
        Oppfolgingsenhet oppfolgingsenhet = new Oppfolgingsenhet();
        oppfolgingsenhet.setEnhetId("");
        oppfolgingsenhet.setNavn("");
        enhetOgVeileder.setOppfolgingsenhet(oppfolgingsenhet);
        enhetOgVeileder.setVeilederId("");
        return enhetOgVeileder;
    }

    public void ping() {
        gjorSporring(hentPingURL(), String.class);
    }

    private Saksbehandler hentSaksbehandler(String veilederIdent, LDAPService ldapService) {
        if (veilederIdent == null) {
            return null;
        }
        return ldapService.hentSaksbehandler(veilederIdent);
    }

    private OppfolgingsEnhetOgVeileder hentOppfolgingsEnhetOgVeileder(String fodselsnummer) {
        return gjorSporring(hentOppfolgingsEnhetOgVeilederURL(fodselsnummer), OppfolgingsEnhetOgVeileder.class);
    }

    private OppfolgingsStatus hentOppfolgingStatus(String fodselsnummer) {
        return gjorSporring(hentOppfolgingsStatusURL(fodselsnummer), OppfolgingsStatus.class);
    }

    private String hentOppfolgingsEnhetOgVeilederURL(String fodselsnummer) {
        return apiUrl + String.format("person/%s/oppfolgingsstatus", fodselsnummer);
    }

    private String hentOppfolgingsStatusURL(String fodselsnummer) {
        return apiUrl + String.format("oppfolging?fnr=%s", fodselsnummer);
    }

    private String hentPingURL() {
        return apiUrl + String.format("ping");
    }

    private <T> T gjorSporring(String url, Class<T> targetClass) {
        return RestUtils.withClient(client -> client
                .target(url)
                .request()
                .header(AUTHORIZATION, "Bearer " + SubjectHandler.getSubjectHandler().getInternSsoToken())
                .get(targetClass)
        );
    }

}
