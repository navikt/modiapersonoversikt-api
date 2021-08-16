package no.nav.modiapersonoversikt.rest.journalforing;

import kotlin.Pair;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.BehandlingsIdTilgangData;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak;
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService;
import no.nav.modiapersonoversikt.legacy.api.utils.RestUtils;
import no.nav.modiapersonoversikt.service.saker.EnhetIkkeSatt;
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;
import static no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ;
import static no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.UPDATE;
import static no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies.behandlingsIderTilhorerBruker;
import static no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies.tilgangTilBruker;
import static no.nav.modiapersonoversikt.legacy.api.utils.RestUtils.hentValgtEnhet;

@RestController
@RequestMapping("/rest/sf-legacy-journalforing/{fnr}")
public class SfLegacyJournalforingController {
    private final SakerService sakerService;
    private final SfHenvendelseService sfHenvendelseService;
    private final Tilgangskontroll tilgangskontroll;


    @Autowired
    public SfLegacyJournalforingController(
            SakerService sakerService,
            SfHenvendelseService sfHenvendelseService,
            Tilgangskontroll tilgangskontroll
    ) {
        this.sakerService = sakerService;
        this.sfHenvendelseService = sfHenvendelseService;
        this.tilgangskontroll = tilgangskontroll;
    }

    @GetMapping("/saker/")
    public SakerService.Resultat hentSaker(@PathVariable("fnr") String fnr) {
        return tilgangskontroll
                .check(tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, Person.GsakSaker, new Pair<>(AuditIdentifier.FNR, fnr)), () ->  sakerService.hentSaker(fnr));
    }

    @PostMapping("/{traadId}")
    public ResponseEntity<Void> knyttTilSak(
            HttpServletRequest request,
            @PathVariable("fnr") String fnr,
            @PathVariable("traadId") String traadId,
            @RequestParam(value = "enhet", required = false) String enhet,
            @RequestBody Sak sak
    ) {
        return tilgangskontroll
                .check(tilgangTilBruker.with(fnr))
                .check(behandlingsIderTilhorerBruker.with(new BehandlingsIdTilgangData(fnr, asList(traadId))))
                .get(Audit.describe(UPDATE, Person.Henvendelse.Journalfor, new Pair<>(AuditIdentifier.FNR, fnr), new Pair<>(AuditIdentifier.TRAAD_ID, traadId), new Pair<>(AuditIdentifier.SAK_ID, sak.saksId)), () -> {
                    sfHenvendelseService.journalforHenvendelse(
                            RestUtils.hentValgtEnhet(enhet, request),
                            traadId,
                            sak.saksId,
                            sak.temaKode
                    );
                    return new ResponseEntity<>(HttpStatus.OK);
                });
    }
}
