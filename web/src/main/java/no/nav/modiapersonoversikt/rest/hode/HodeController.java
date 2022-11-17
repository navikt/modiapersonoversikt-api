package no.nav.modiapersonoversikt.rest.hode;

import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ;
import static no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Saksbehandler.Enheter;
import static no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Saksbehandler.NavnOgEnheter;

@RestController
@RequestMapping("/rest/hode")
@SuppressWarnings("unchecked")
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
                    final var navIdent = AuthContextUtils.requireNavIdent();
                    final var veileder = ansattService.hentVeileder(navIdent);
                    return new Me(navIdent.get(), veileder.getNavn(), veileder.getFornavn(), veileder.getEtternavn());
                });
    }

    @GetMapping("/enheter")
    public Enheter hentEnheter() {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.describe(READ, Enheter), () -> {
                    final var navIdent = AuthContextUtils.requireNavIdent();
                    final var enheter = ansattService.hentEnhetsliste(navIdent)
                            .stream()
                            .map((enhet) -> new Enhet(enhet.enhetId, enhet.enhetNavn))
                            .collect(Collectors.toList());
                    return new Enheter(navIdent.get(), enheter);
                });
    }
}
