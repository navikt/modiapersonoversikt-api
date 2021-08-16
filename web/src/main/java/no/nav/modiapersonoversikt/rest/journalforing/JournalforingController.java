package no.nav.modiapersonoversikt.rest.journalforing;

import kotlin.Pair;
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak;
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService;
import no.nav.modiapersonoversikt.service.saker.EnhetIkkeSatt;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.BehandlingsIdTilgangData;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;
import static no.nav.modiapersonoversikt.legacy.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies.behandlingsIderTilhorerBruker;
import static no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies.tilgangTilBruker;
import static no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ;
import static no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.UPDATE;

@RestController
@RequestMapping("/rest/journalforing/{fnr}")
public class JournalforingController {

    public static final String FEILMELDING_UTEN_ENHET = "Det er dessverre ikke mulig å journalføre henvendelsen. Du må velge enhet du jobber på vegne av på nytt. Bekreft enhet med å trykke på \"Velg\"-knappen.";

    private final SakerService sakerService;
    private final Tilgangskontroll tilgangskontroll;


    @Autowired
    public JournalforingController(SakerService sakerService, Tilgangskontroll tilgangskontroll) {
        this.sakerService = sakerService;
        this.tilgangskontroll = tilgangskontroll;
    }

    @GetMapping("/saker/")
    public SakerService.Resultat hentSaker(@PathVariable("fnr") String fnr) {
        return tilgangskontroll
                .check(tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, Person.GsakSaker, new Pair<>(AuditIdentifier.FNR, fnr)), () ->  sakerService.hentSaker(fnr));
    }

    @PostMapping("/{traadId}")
    public ResponseEntity<Void> knyttTilSak(@PathVariable("fnr") String fnr, @PathVariable("traadId") String traadId, @RequestBody Sak sak, @RequestParam(value = "enhet", required = false) String enhet, HttpServletRequest request) {
        return tilgangskontroll
                .check(tilgangTilBruker.with(fnr))
                .check(behandlingsIderTilhorerBruker.with(new BehandlingsIdTilgangData(fnr, asList(traadId))))
                .get(Audit.describe(UPDATE, Person.Henvendelse.Journalfor, new Pair<>(AuditIdentifier.FNR, fnr), new Pair<>(AuditIdentifier.TRAAD_ID, traadId), new Pair<>(AuditIdentifier.SAK_ID, sak.saksId)), () -> {
                    String valgtEnhet = hentValgtEnhet(enhet, request);
                    try {
                        sakerService.knyttBehandlingskjedeTilSak(fnr, traadId, sak, valgtEnhet);
                    } catch (EnhetIkkeSatt exception) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, FEILMELDING_UTEN_ENHET, exception);
                    } catch (Exception exception) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ukjent feil", exception);
                    }

                    return new ResponseEntity<>(HttpStatus.OK);
                });
    }
}
