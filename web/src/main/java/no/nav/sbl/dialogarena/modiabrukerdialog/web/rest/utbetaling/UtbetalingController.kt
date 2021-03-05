package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.utbetaling

import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATOFORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagRiktigDato
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/utbetaling/{fnr}")
class UtbetalingController @Autowired constructor(private val service: UtbetalingService, private val tilgangskontroll: Tilgangskontroll) {

    @GetMapping
    fun hent(
        @PathVariable("fnr") fnr: String,
        @RequestParam("startDato") start: String?,
        @RequestParam("sluttDato") slutt: String?
    ): ResponseEntity<out Any> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Utbetalinger, AuditIdentifier.FNR to fnr)) {
                val startDato = lagRiktigDato(start)
                val sluttDato = lagRiktigDato(slutt)

                if (startDato == null || sluttDato == null) {
                    ResponseEntity("queryparam ?startDato=yyyy-MM-dd&sluttDato=yyyy-MM-dd må være satt", HttpStatus.BAD_REQUEST)
                } else {
                    val utbetalinger = service.hentWSUtbetalinger(
                        fnr,
                        startDato,
                        sluttDato
                    )

                    ResponseEntity(
                        mapOf(
                            "utbetalinger" to hentUtbetalinger(utbetalinger),
                            "periode" to mapOf(
                                "startDato" to startDato.toString(DATOFORMAT),
                                "sluttDato" to sluttDato.toString(DATOFORMAT)
                            )
                        ),
                        HttpStatus.OK
                    )
                }
            }
    }

    private fun hentUtbetalinger(utbetalinger: List<WSUtbetaling>): List<Map<String, Any?>> {
        return utbetalinger.map {
            mapOf(
                "posteringsdato" to it.posteringsdato?.toString(DATOFORMAT),
                "utbetalingsdato" to it.utbetalingsdato?.toString(DATOFORMAT),
                "forfallsdato" to it.forfallsdato?.toString(DATOFORMAT),
                "utbetaltTil" to it.utbetaltTil?.navn?.trim(),
                "erUtbetaltTilPerson" to (it.utbetaltTil is WSPerson),
                "erUtbetaltTilOrganisasjon" to (it.utbetaltTil is WSOrganisasjon),
                "erUtbetaltTilSamhandler" to (it.utbetaltTil is WSSamhandler),
                "nettobeløp" to it.utbetalingNettobeloep,
                "melding" to it.utbetalingsmelding?.trim(),
                "metode" to it.utbetalingsmetode?.trim(),
                "status" to it.utbetalingsstatus?.trim(),
                "konto" to it.utbetaltTilKonto?.kontonummer?.trim(),
                "ytelser" to it.ytelseListe?.let { ytelser -> hentYtelserForUtbetaling(ytelser) }
            )
        }
    }

    private fun hentYtelserForUtbetaling(ytelser: List<WSYtelse>): List<Map<String, Any?>> {
        return ytelser.map {
            mapOf(
                "type" to it.ytelsestype?.value?.trim(),
                "ytelseskomponentListe" to it.ytelseskomponentListe?.let { ytelser -> hentYtelsekomponentListe(ytelser) },
                "ytelseskomponentersum" to it.ytelseskomponentersum,
                "trekkListe" to it.trekkListe?.let { trekkliste -> hentTrekkListe(trekkliste) },
                "trekksum" to it.trekksum,
                "skattListe" to it.skattListe?.let { skattListe -> hentSkattListe(skattListe) },
                "skattsum" to it.skattsum,
                "periode" to it.ytelsesperiode?.let { ytelsesperiode -> hentYtelsesperiode(ytelsesperiode) },
                "nettobeløp" to it.ytelseNettobeloep,
                "bilagsnummer" to it.bilagsnummer?.trim(),
                "arbeidsgiver" to it.refundertForOrg?.let { orgnr -> hentArbeidsgiver(orgnr) }
            )
        }
    }

    private fun hentYtelsekomponentListe(ytelseskomponenter: List<WSYtelseskomponent>): List<Map<String, Any?>> {
        return ytelseskomponenter.map {
            mapOf(
                "ytelseskomponenttype" to it.ytelseskomponenttype?.trim(),
                "satsbeløp" to it.satsbeloep,
                "satstype" to it.satstype?.trim(),
                "satsantall" to it.satsantall,
                "ytelseskomponentbeløp" to it.ytelseskomponentbeloep
            )
        }
    }

    private fun hentTrekkListe(trekk: List<WSTrekk>): List<Map<String, Any?>> {
        return trekk.map {
            mapOf(
                "trekktype" to it.trekktype?.trim(),
                "trekkbeløp" to it.trekkbeloep,
                "kreditor" to it.kreditor?.trim()
            )
        }
    }

    private fun hentSkattListe(skatt: List<WSSkatt>): List<Map<String, Any?>> {
        return skatt.map {
            mapOf(
                "skattebeløp" to it.skattebeloep
            )
        }
    }

    private fun hentYtelsesperiode(periode: WSPeriode): Map<String, Any?> {
        return mapOf(
            "start" to periode.fom?.toString(DATOFORMAT),
            "slutt" to periode.tom?.toString(DATOFORMAT)
        )
    }

    private fun hentArbeidsgiver(it: WSAktoer): Map<String, String> {
        return mapOf(
            "orgnr" to it.aktoerId,
            "navn" to it.navn
        )
    }
}
