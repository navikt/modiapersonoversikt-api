package no.nav.modiapersonoversikt.service.utbetaling

import no.nav.modiapersonoversikt.rest.DATOFORMAT
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*

object WSUtbetalingMapper {
    fun hentUtbetalinger(utbetalinger: List<WSUtbetaling>): List<UtbetalingDomain.Utbetaling> {
        return utbetalinger.map { utbetaling ->
            UtbetalingDomain.Utbetaling(
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
                ytelser = utbetaling.ytelseListe?.map(WSUtbetalingMapper::hentYtelserForUtbetaling) ?: emptyList()
            )
        }
    }

    private fun hentYtelserForUtbetaling(ytelse: WSYtelse): UtbetalingDomain.Ytelse {
        return UtbetalingDomain.Ytelse(
            type = ytelse.ytelsestype?.value?.trim(),
            ytelseskomponentListe = ytelse.ytelseskomponentListe?.map(WSUtbetalingMapper::hentYtelsekomponentListe) ?: emptyList(),
            ytelseskomponentersum = ytelse.ytelseskomponentersum,
            trekkListe = ytelse.trekkListe?.map(WSUtbetalingMapper::hentTrekkListe) ?: emptyList(),
            trekksum = ytelse.trekksum,
            skattListe = ytelse.skattListe?.map(WSUtbetalingMapper::hentSkattListe) ?: emptyList(),
            skattsum = ytelse.skattsum,
            periode = hentYtelsesperiode(ytelse.ytelsesperiode),
            nettobelop = ytelse.ytelseNettobeloep,
            bilagsnummer = ytelse.bilagsnummer?.trim(),
            arbeidsgiver = ytelse.refundertForOrg?.let { orgnr -> hentArbeidsgiver(orgnr) }
        )
    }

    private fun hentYtelsekomponentListe(ytelseskomponent: WSYtelseskomponent): UtbetalingDomain.YtelseKomponent {
        return UtbetalingDomain.YtelseKomponent(
            ytelseskomponenttype = ytelseskomponent.ytelseskomponenttype.trim(),
            satsbelop = ytelseskomponent.satsbeloep,
            satstype = ytelseskomponent.satstype?.trim(),
            satsantall = ytelseskomponent.satsantall,
            ytelseskomponentbelop = ytelseskomponent.ytelseskomponentbeloep,
        )
    }

    private fun hentTrekkListe(trekk: WSTrekk): UtbetalingDomain.Trekk {
        return UtbetalingDomain.Trekk(
            trekktype = trekk.trekktype.trim(),
            trekkbelop = trekk.trekkbeloep,
            kreditor = trekk.kreditor?.trim(),
        )
    }

    private fun hentSkattListe(skatt: WSSkatt): UtbetalingDomain.Skatt {
        return UtbetalingDomain.Skatt(
            skattebelop = skatt.skattebeloep,
        )
    }

    private fun hentYtelsesperiode(periode: WSPeriode): UtbetalingDomain.YtelsePeriode {
        return UtbetalingDomain.YtelsePeriode(
            start = periode.fom.toString(DATOFORMAT),
            slutt = periode.tom.toString(DATOFORMAT),
        )
    }

    private fun hentArbeidsgiver(it: WSAktoer): UtbetalingDomain.Arbeidgiver {
        return UtbetalingDomain.Arbeidgiver(
            orgnr = it.aktoerId,
            navn = it.navn,
        )
    }
}
