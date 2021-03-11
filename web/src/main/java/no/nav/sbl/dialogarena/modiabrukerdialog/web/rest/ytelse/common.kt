package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import no.nav.kjerneinfo.consumer.organisasjon.OrganisasjonService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATOFORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagPeriode
import no.nav.sykmeldingsperioder.domain.HistoriskUtbetaling
import no.nav.sykmeldingsperioder.domain.KommendeUtbetaling
import no.nav.sykmeldingsperioder.domain.Kreditortrekk

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

fun hentArbeidsgiverNavn(organisasjonService: OrganisasjonService, orgnr: String): String? =
    organisasjonService.hentNoekkelinfo(orgnr).orElse(null).navn
