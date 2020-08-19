package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import kotlin.Pair;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.naudit.AuditIdentifier;
import no.nav.sbl.dialogarena.naudit.AuditResources.Person;
import no.nav.sbl.dialogarena.naudit.Audit;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/varsler/{fnr}")
public class VarslerController {

    @Autowired
    VarslerService varslerService;

    @Autowired
    Tilgangskontroll tilgangskontroll;

    @GetMapping
    public List<Varsel> hentAlleVarsler(@PathVariable("fnr") String fnr) {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(Audit.Action.READ, Person.Varsler, new Pair<>(AuditIdentifier.FNR, fnr)), () -> varslerService
                        .hentAlleVarsler(fnr)
                        .orElse(emptyList())
                );
    }
}
