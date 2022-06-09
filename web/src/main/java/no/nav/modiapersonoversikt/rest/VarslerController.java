package no.nav.modiapersonoversikt.rest;

import kotlin.Pair;
import no.nav.common.types.identer.Fnr;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.service.varsel.domain.Varsel;
import no.nav.modiapersonoversikt.service.varsel.VarslerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/rest/varsler/{fnr}")
public class VarslerController {

    @Autowired
    VarslerService varslerService;

    @Autowired
    Tilgangskontroll tilgangskontroll;

    @GetMapping
    public List<Varsel> hentAlleVarsler(@PathVariable("fnr") String fnr) {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr.of(fnr)))
                .get(Audit.describe(Audit.Action.READ, Person.Varsler, new Pair<>(AuditIdentifier.FNR, fnr)), () -> varslerService
                        .hentAlleVarsler(fnr)
                        .orElse(emptyList())
                );
    }
}
