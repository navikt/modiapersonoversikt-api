package no.nav.modiapersonoversikt.rest.utbetaling

import no.nav.modiapersonoversikt.rest.DATOFORMAT
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*

object UtbetalingMapper {
    fun hentUtbetalinger(utbetalinger: List<WSUtbetaling>): List<UtbetalingDTO> {
        return utbetalinger.map { utbetaling ->
            UtbetalingDTO(
                posteringsdato = utbetaling.posteringsdato.toString(DATOFORMAT),
                utbetalingsdato = utbetaling.utbetalingsdato?.toString(DATOFORMAT),
                forfallsdato = utbetaling.forfallsdato?.toString(DATOFORMAT),
                utbetaltTil = utbetaling.utbetaltTil.navn.trim(),
                erUtbetaltTilPerson = (utbetaling.utbetaltTil is WSPerson),
                erUtbetaltTilOrganisasjon = (utbetaling.utbetaltTil is WSOrganisasjon),
                erUtbetaltTilSamhandler = (utbetaling.utbetaltTil is WSSamhandler),
                nettobelop = utbetaling.utbetalingNettobeloep,
                melding = utbetaling.utbetalingsmelding?.trim(),
                metode = utbetaling.utbetalingsmetode.trim(),
                status = utbetaling.utbetalingsstatus.trim(),
                konto = utbetaling.utbetaltTilKonto?.kontonummer?.trim(),
                ytelser = utbetaling.ytelseListe?.map(::hentYtelserForUtbetaling) ?: emptyList()
            )
        }
    }

    private fun hentYtelserForUtbetaling(ytelse: WSYtelse): YtelseDTO {
        return YtelseDTO(
            type = ytelse.ytelsestype?.value?.trim(),
            ytelseskomponentListe = ytelse.ytelseskomponentListe?.map(::hentYtelsekomponentListe) ?: emptyList(),
            ytelseskomponentersum = ytelse.ytelseskomponentersum,
            trekkListe = ytelse.trekkListe?.map(::hentTrekkListe) ?: emptyList(),
            trekksum = ytelse.trekksum,
            skattListe = ytelse.skattListe?.map(::hentSkattListe) ?: emptyList(),
            skattsum = ytelse.skattsum,
            periode = hentYtelsesperiode(ytelse.ytelsesperiode),
            nettobelop = ytelse.ytelseNettobeloep,
            bilagsnummer = ytelse.bilagsnummer?.trim(),
            arbeidsgiver = ytelse.refundertForOrg?.let { orgnr -> hentArbeidsgiver(orgnr) }
        )
    }

    private fun hentYtelsekomponentListe(ytelseskomponent: WSYtelseskomponent): YtelseKomponentDTO {
        return YtelseKomponentDTO(
            ytelseskomponenttype = ytelseskomponent.ytelseskomponenttype.trim(),
            satsbelop = ytelseskomponent.satsbeloep,
            satstype = ytelseskomponent.satstype?.trim(),
            satsantall = ytelseskomponent.satsantall,
            ytelseskomponentbelop = ytelseskomponent.ytelseskomponentbeloep,
        )
    }

    private fun hentTrekkListe(trekk: WSTrekk): TrekkDTO {
        return TrekkDTO(
            trekktype = trekk.trekktype.trim(),
            trekkbelop = trekk.trekkbeloep,
            kreditor = trekk.kreditor?.trim(),
        )
    }

    private fun hentSkattListe(skatt: WSSkatt): SkattDTO {
        return SkattDTO(
            skattebelop = skatt.skattebeloep,
        )
    }

    private fun hentYtelsesperiode(periode: WSPeriode): YtelsePeriodeDTO {
        return YtelsePeriodeDTO(
            start = periode.fom.toString(DATOFORMAT),
            slutt = periode.tom.toString(DATOFORMAT),
        )
    }

    private fun hentArbeidsgiver(it: WSAktoer): ArbeidgiverDTO {
        return ArbeidgiverDTO(
            orgnr = it.aktoerId,
            navn = it.navn,
        )
    }
}
