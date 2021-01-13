package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import kotlin.Pair;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak.EnhetIkkeSatt;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.BehandlingsIdTilgangData;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.naudit.AuditIdentifier;
import no.nav.sbl.dialogarena.naudit.AuditResources.Person;
import no.nav.sbl.dialogarena.naudit.Audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies.behandlingsIderTilhorerBruker;
import static no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies.tilgangTilBruker;
import static no.nav.sbl.dialogarena.naudit.Audit.Action.*;

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

    @GetMapping("/saker/sammensatte")
    public List<Sak> hentSammensatteSaker(@PathVariable("fnr") String fnr) {
        return tilgangskontroll
                .check(tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, Person.GsakSaker, new Pair<>(AuditIdentifier.FNR, fnr)), () -> sakerService.hentSammensatteSaker(fnr));
    }

    @GetMapping("/saker/pensjon")
    public List<Sak> hentPensjonSaker(@PathVariable("fnr") String fnr) {
        return tilgangskontroll
                .check(tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, Person.PesysSaker, new Pair<>(AuditIdentifier.FNR, fnr)), () -> sakerService.hentPensjonSaker(fnr));
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
