package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.commondomain.Periode
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.HistoriskUtbetaling
import no.nav.modiapersonoversikt.consumer.infotrygd.domain.KommendeUtbetaling
import no.nav.modiapersonoversikt.infotrgd.CommonHistoriskUtbetaling
import no.nav.modiapersonoversikt.infotrgd.CommonKommendeUtbetaling
import no.nav.modiapersonoversikt.infotrgd.CommonKreditortrekk
import no.nav.modiapersonoversikt.infotrgd.CommonPeriode
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

fun toCommonPeriode(period: Periode) = CommonPeriode(period.from?.toString(JODA_DATOFORMAT), period.to?.toString(JODA_DATOFORMAT))

private fun hentKreditorTrekk(
    kreditortrekk: List<no.nav.modiapersonoversikt.consumer.infotrygd.domain.Kreditortrekk>,
): List<CommonKreditortrekk> =
    kreditortrekk.map {
        CommonKreditortrekk(
            kreditorsNavn = it.kreditorsNavn,
            belop = it.belop,
        )
    }
