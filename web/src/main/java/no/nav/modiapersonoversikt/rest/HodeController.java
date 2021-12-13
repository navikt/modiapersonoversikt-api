package no.nav.modiapersonoversikt.rest;

import kotlin.Pair;
import no.nav.common.auth.context.AuthContextHolderThreadLocal;
import no.nav.modiapersonoversikt.legacy.api.domain.Person;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService;
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.modiapersonoversikt.legacy.api.utils.http.CookieUtil;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Saksbehandler;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.modiapersonoversikt.legacy.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*;

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

    static class Me {
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

    static class Enhet {
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
                    String ident = AuthContextHolderThreadLocal.instance().requireSubject();
                    Pair<String, String> saksbehandler = hentSaksbehandlerNavn();
                    String enhetId = hentValgtEnhet(null, request);
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
                    String ident = AuthContextHolderThreadLocal.instance().requireSubject();
                    List<Enhet> enheter = ansattService.hentEnhetsliste()
                            .stream()
                            .map((ansattEnhet) -> new Enhet(ansattEnhet.enhetId, ansattEnhet.enhetNavn))
                            .collect(Collectors.toList());

                    return new Enheter(ident, enheter);
                });
    }

    @PostMapping("/velgenhet")
    public String settValgtEnhet(HttpServletResponse response, @RequestBody String enhetId) {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(UPDATE, Saksbehandler.ValgtEnhet, new Pair<>(AuditIdentifier.ENHET_ID, enhetId)), () -> {
                    CookieUtil.setSaksbehandlersValgteEnhet(response, enhetId);
                    return enhetId;
                });
    }

    private Pair<String, String> hentSaksbehandlerNavn() {
        Person saksbehandler = AuthContextHolderThreadLocal.instance().getSubject()
                .map(ldapService::hentSaksbehandler)
                .orElseThrow(() -> new RuntimeException("Fant ikke ident til saksbehandler"));
        return new Pair<>(saksbehandler.fornavn, saksbehandler.etternavn);
    }
}
