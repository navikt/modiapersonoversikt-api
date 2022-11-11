package no.nav.modiapersonoversikt.rest.hode;

import kotlin.Pair;
import no.nav.modiapersonoversikt.commondomain.Veileder;
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ;
import static no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.UPDATE;
import static no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Saksbehandler.*;

@RestController
@RequestMapping("/rest/hode")
public class HodeController {
    @Autowired
    private AnsattService ansattService;

    @Autowired
    Tilgangskontroll tilgangskontroll;

    record Me(String ident, String navn, String fornavn, String etternavn) {
    }

    record Enheter(String ident, List<Enhet> enhetliste) {
    }

    record Enhet(String enhetId, String navn) {
    }

    @GetMapping("/me")
    public Me hentSaksbehandler() {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, NavnOgEnheter), () -> {
                    String ident = AuthContextUtils.requireIdent();
                    Veileder veileder = hentSaksbehandlerNavn();
                    return new Me(ident, veileder.getNavn(), veileder.getFornavn(), veileder.getEtternavn());
                });
    }

    @GetMapping("/enheter")
    public Enheter hentEnheter() {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Enheter), () -> {
                    String ident = AuthContextUtils.requireIdent();
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
                .get(Audit.describe(UPDATE, ValgtEnhet, new Pair<>(AuditIdentifier.ENHET_ID, enhetId)), () -> {
                    CookieUtil.setSaksbehandlersValgteEnhet(response, enhetId);
                    return enhetId;
                });
    }

    private Veileder hentSaksbehandlerNavn() {
        return AuthContextUtils.getNavIdent()
                .map(ansattService::hentVeileder)
                .orElseThrow(() -> new RuntimeException("Fant ikke ident til saksbehandler"));
    }
}
