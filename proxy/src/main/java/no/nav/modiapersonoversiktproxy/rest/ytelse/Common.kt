package no.nav.modiapersonoversiktproxy.rest.ytelse

import no.nav.modiapersonoversiktproxy.commondomain.Periode
import no.nav.modiapersonoversiktproxy.consumer.ereg.OrganisasjonService
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.HistoriskUtbetaling
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.KommendeUtbetaling
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.Kreditortrekk
import no.nav.modiapersonoversiktproxy.rest.DATOFORMAT
import no.nav.modiapersonoversiktproxy.rest.JODA_DATOFORMAT

fun hentHistoriskeUtbetalinger(historiskeUtbetalinger: List<HistoriskUtbetaling>) =
    historiskeUtbetalinger.map {
        mapOf(
            "vedtak" to it.vedtak?.let { vedtak -> lagPeriode(vedtak) },
            "utbetalingsgrad" to it.utbetalingsgrad,
            "utbetalingsdato" to it.utbetalingsdato?.toString(JODA_DATOFORMAT),
            "nettobeløp" to it.nettobelop,
            "bruttobeløp" to it.bruttobeloep,
            "skattetrekk" to it.skattetrekk,
            "arbeidsgiverNavn" to it.arbeidsgiverNavn,
            "arbeidsgiverOrgNr" to it.arbeidsgiverOrgNr,
            "dagsats" to it.dagsats,
            "type" to it.type,
            "trekk" to it.trekk?.let { trekk -> hentKreditorTrekk(trekk) },
        )
    }

fun hentKommendeUtbetalinger(kommendeUtbetalinger: List<KommendeUtbetaling>) =
    kommendeUtbetalinger.map {
        mapOf(
            "vedtak" to it.vedtak?.let { vedtak -> lagPeriode(vedtak) },
            "utbetalingsgrad" to it.utbetalingsgrad,
            "utbetalingsdato" to it.utbetalingsdato?.toString(JODA_DATOFORMAT),
            "bruttobeløp" to it.bruttobeloep,
            "arbeidsgiverNavn" to it.arbeidsgiverNavn,
            "arbeidsgiverKontonr" to it.arbeidsgiverKontonr,
            "arbeidsgiverOrgNr" to it.arbeidsgiverOrgnr,
            "dagsats" to it.dagsats,
            "saksbehandler" to it.saksbehandler,
            "type" to it.type?.termnavn,
        )
    }

private fun hentKreditorTrekk(kreditortrekk: List<Kreditortrekk>): List<Map<String, Any?>> =
    kreditortrekk.map {
        mapOf(
            "kreditorsNavn" to it.kreditorsNavn,
            "beløp" to it.belop,
        )
    }

fun hentArbeidsgiverNavn(
    organisasjonService: OrganisasjonService,
    orgnr: String,
): String = organisasjonService.hentNoekkelinfo(orgnr).orElse(null).navn

fun lagPeriode(periode: Periode) =
    mapOf(
        "fra" to periode.from?.toString(JODA_DATOFORMAT),
        "til" to periode.to?.toString(JODA_DATOFORMAT),
    )

fun lagPleiepengePeriode(periode: no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Periode) =
    mapOf(
        "fom" to periode.fraOgMed?.format(DATOFORMAT),
        "tom" to periode.tilOgMed?.format(DATOFORMAT),
    )
