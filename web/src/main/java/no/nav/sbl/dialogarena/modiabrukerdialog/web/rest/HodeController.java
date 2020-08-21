package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import kotlin.Pair;
import no.nav.common.auth.subject.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.CookieUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.naudit.AuditIdentifier;
import no.nav.sbl.dialogarena.naudit.AuditResources.Saksbehandler;
import no.nav.sbl.dialogarena.naudit.Audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.naudit.Audit.Action.*;

@RestController
@RequestMapping("/rest/hode")
public class HodeController {

    @Autowired
    private LDAPService ldapService;

    @Autowired
    private AnsattService ansattService;

    @Autowired
    private OrganisasjonEnhetV2Service organisasjonEnhetService;

    @Autowired
    Tilgangskontroll tilgangskontroll;

    class Me {
        public final String ident, navn, fornavn, etternavn, enhetId, enhetNavn;

        public Me(String ident, String fornavn, String etternavn, String enhetId, String enhetNavn) {
            this.ident = ident;
            this.fornavn = fornavn;
            this.etternavn = etternavn;
            this.navn = fornavn + " " + etternavn;
            this.enhetId = enhetId;
            this.enhetNavn = enhetNavn;
        }
    }

    class Enhet {
        public final String enhetId, navn;

        public Enhet(String enhetId, String navn) {
            this.enhetId = enhetId;
            this.navn = navn;
        }
    }

    class Enheter {
        public final String ident;
        public final List<Enhet> enhetliste;

        public Enheter(String ident, List<Enhet> enhetliste) {
            this.ident = ident;
            this.enhetliste = enhetliste;
        }
    }

    @GetMapping("/me")
    public Me hentSaksbehandler(HttpServletRequest request) {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Saksbehandler.NavnOgEnheter), () -> {
                    String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
                    Pair<String, String> saksbehandler = hentSaksbehandlerNavn();
                    String enhetId = hentValgtEnhet(request);
                    String enhetNavn = organisasjonEnhetService.hentEnhetGittEnhetId(enhetId, OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.UFILTRERT)
                            .map((enhet) -> enhet.enhetNavn)
                            .orElse("[Ukjent enhetId: " + enhetId + "]");
                    return new Me(ident, saksbehandler.getFirst(), saksbehandler.getSecond(), enhetId, enhetNavn);
                });
    }

    @GetMapping("/enheter")
    public Enheter hentEnheter() {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Saksbehandler.Enheter), () -> {
                    String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
                    List<Enhet> enheter = ansattService.hentEnhetsliste()
                            .stream()
                            .map((ansattEnhet) -> new Enhet(ansattEnhet.enhetId, ansattEnhet.enhetNavn))
                            .collect(Collectors.toList());

                    return new Enheter(ident, enheter);
                });
    }

    @PostMapping("/velgenhet")
    public String settValgtEnhet(HttpServletResponse response, String enhetId) {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(UPDATE, Saksbehandler.ValgtEnhet, new Pair<>(AuditIdentifier.ENHET_ID, enhetId)), () -> {
                    CookieUtil.setSaksbehandlersValgteEnhet(response, enhetId);
                    return enhetId;
                });
    }

    private Pair<String, String> hentSaksbehandlerNavn() {
        Person saksbehandler = SubjectHandler.getIdent()
                .map(ldapService::hentSaksbehandler)
                .orElseThrow(() -> new RuntimeException("Fant ikke ident til saksbehandler"));
        return new Pair<>(saksbehandler.fornavn, saksbehandler.etternavn);
    }
}
