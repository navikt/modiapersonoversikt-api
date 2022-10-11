package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.commondomain.Periode
import no.nav.modiapersonoversikt.consumer.ereg.OrganisasjonService
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.HistoriskUtbetaling
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.KommendeUtbetaling
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.Kreditortrekk
import no.nav.modiapersonoversikt.rest.DATOFORMAT
import java.time.format.DateTimeFormatter

fun hentHistoriskeUtbetalinger(historiskeUtbetalinger: List<HistoriskUtbetaling>) =
    historiskeUtbetalinger.map {
        mapOf(
            "vedtak" to it.vedtak?.let { vedtak -> lagPeriode(vedtak) },
            "utbetalingsgrad" to it.utbetalingsgrad,
            "utbetalingsdato" to it.utbetalingsdato?.toString(DATOFORMAT),
            "nettobeløp" to it.nettobelop,
            "bruttobeløp" to it.bruttobeloep,
            "skattetrekk" to it.skattetrekk,
            "arbeidsgiverNavn" to it.arbeidsgiverNavn,
            "arbeidsgiverOrgNr" to it.arbeidsgiverOrgNr,
            "dagsats" to it.dagsats,
            "type" to it.type,
            "trekk" to it.trekk?.let { trekk -> hentKreditorTrekk(trekk) }
        )
    }

fun hentKommendeUtbetalinger(kommendeUtbetalinger: List<KommendeUtbetaling>) =
    kommendeUtbetalinger.map {
        mapOf(
            "vedtak" to it.vedtak?.let { vedtak -> lagPeriode(vedtak) },
            "utbetalingsgrad" to it.utbetalingsgrad,
            "utbetalingsdato" to it.utbetalingsdato?.toString(DATOFORMAT),
            "bruttobeløp" to it.bruttobeloep,
            "arbeidsgiverNavn" to it.arbeidsgiverNavn,
            "arbeidsgiverKontonr" to it.arbeidsgiverKontonr,
            "arbeidsgiverOrgNr" to it.arbeidsgiverOrgnr,
            "dagsats" to it.dagsats,
            "saksbehandler" to it.saksbehandler,
            "type" to it.type?.termnavn
        )
    }

private fun hentKreditorTrekk(kreditortrekk: List<Kreditortrekk>): List<Map<String, Any?>> {
    return kreditortrekk.map {
        mapOf(
            "kreditorsNavn" to it.kreditorsNavn,
            "beløp" to it.belop
        )
    }
}

fun hentArbeidsgiverNavn(organisasjonService: OrganisasjonService, orgnr: String): String =
    organisasjonService.hentNoekkelinfo(orgnr).orElse(null).navn

fun lagPeriode(periode: Periode) = mapOf(
    "fra" to periode.from?.toString(DATOFORMAT),
    "til" to periode.to?.toString(DATOFORMAT)
)

fun lagPleiepengePeriode(periode: no.nav.modiapersonoversikt.consumer.infotrygd.domain.pleiepenger.Periode) = mapOf(
    "fom" to periode.fraOgMed?.format(DateTimeFormatter.ofPattern(DATOFORMAT)),
    "tom" to periode.tilOgMed?.format(DateTimeFormatter.ofPattern(DATOFORMAT))
)
