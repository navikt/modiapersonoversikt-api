package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.commondomain.Periode
import no.nav.modiapersonoversikt.consumer.ereg.OrganisasjonService
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.HistoriskUtbetaling
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.KommendeUtbetaling
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.Kreditortrekk
import no.nav.modiapersonoversikt.rest.JODA_DATOFORMAT

fun hentHistoriskeUtbetalinger(historiskeUtbetalinger: List<HistoriskUtbetaling>) =
    historiskeUtbetalinger.map {
        CommonHistoriskUtbetaling(
            vedtak = it.vedtak?.let { vedtak -> toCommonPeriode(vedtak) },
            utbetalingsgrad = it.utbetalingsgrad,
            utbetalingsdato = it.utbetalingsdato?.toString(JODA_DATOFORMAT),
            nettobelop = it.nettobelop,
            bruttobelop = it.bruttobeloep,
            skattetrekk = it.skattetrekk,
            arbeidsgiverNavn = it.arbeidsgiverNavn,
            arbeidsgiverOrgNr = it.arbeidsgiverOrgNr,
            dagsats = it.dagsats,
            type = it.type,
            trekk = it.trekk?.let { trekk -> hentKreditorTrekk(trekk) },
        )
    }

fun hentKommendeUtbetalinger(kommendeUtbetalinger: List<KommendeUtbetaling>) =
    kommendeUtbetalinger.map {
        CommonKommendeUtbetaling(
            vedtak = it.vedtak?.let { vedtak -> toCommonPeriode(vedtak) },
            utbetalingsgrad = it.utbetalingsgrad,
            utbetalingsdato = it.utbetalingsdato?.toString(JODA_DATOFORMAT),
            bruttobelop = it.bruttobeloep,
            arbeidsgiverNavn = it.arbeidsgiverNavn,
            arbeidsgiverKontonr = it.arbeidsgiverKontonr,
            arbeidsgiverOrgNr = it.arbeidsgiverOrgnr,
            dagsats = it.dagsats,
            saksbehandler = it.saksbehandler,
            type = it.type?.termnavn,
        )
    }

fun toCommonPeriode(period: Periode) = CommonPeriode(period.from.toString(JODA_DATOFORMAT), period.to.toString(JODA_DATOFORMAT))

private fun hentKreditorTrekk(kreditortrekk: List<Kreditortrekk>): List<CommonKreditortrekk> =
    kreditortrekk.map {
        CommonKreditortrekk(
            kreditorsNavn = it.kreditorsNavn,
            belop = it.belop,
        )
    }

fun hentArbeidsgiverNavn(
    organisasjonService: OrganisasjonService,
    orgnr: String,
): String = organisasjonService.hentNoekkelinfo(orgnr)?.navn ?: ""

data class CommonHistoriskUtbetaling(
    val vedtak: CommonPeriode?,
    val utbetalingsgrad: Double?,
    val utbetalingsdato: String?,
    val nettobelop: Double?,
    val bruttobelop: Double?,
    val skattetrekk: Double?,
    val arbeidsgiverNavn: String?,
    val arbeidsgiverOrgNr: String?,
    val dagsats: Double?,
    val type: String?,
    val trekk: List<CommonKreditortrekk>?,
)

data class CommonKommendeUtbetaling(
    val vedtak: CommonPeriode?,
    val utbetalingsgrad: Double?,
    val utbetalingsdato: String?,
    val bruttobelop: Double?,
    val arbeidsgiverNavn: String?,
    val arbeidsgiverOrgNr: String?,
    val arbeidsgiverKontonr: String?,
    val dagsats: Double?,
    val type: String?,
    val saksbehandler: String?,
)

data class CommonKreditortrekk(
    val kreditorsNavn: String?,
    val belop: Double?,
)

data class CommonPeriode(
    val fra: String?,
    val til: String?,
)
