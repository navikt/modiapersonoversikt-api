package no.nav.modiapersonoversikt.service.utbetaling

import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.apis.UtbetaldataV2Api
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.AktoerDTO
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.PeriodeDTO
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.UtbetalingDTO
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.UtbetalingsoppslagDTO
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.service.utbetaling.UtbetalingUtils.leggTilEkstraDagerPaaStartdato
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import java.time.LocalDate
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.AktoerDTO as RsAktoer
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.PeriodeDTO as RsPeriode
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.SkattDTO as RsSkatt
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.TrekkDTO as RsTrekk
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.UtbetalingDTO as RsUtbetaling
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.YtelseDTO as RsYtelse
import no.nav.modiapersonoversikt.api.domain.utbetaling.generated.models.YtelsekomponentDTO as RsYtelseKomponent

@CacheConfig(cacheNames = ["utbetalingCache"], keyGenerator = "userkeygenerator")
open class UtbetalingServiceImpl(
    private val utbetaldataV2Api: UtbetaldataV2Api,
    private val unleash: UnleashService,
) : UtbetalingService {
    @Cacheable
    override fun hentUtbetalinger(
        fnr: Fnr,
        startDato: LocalDate,
        sluttDato: LocalDate,
    ): List<UtbetalingDomain.Utbetaling> {
        val fomDato =
            if (unleash.isEnabled(Feature.UTVIDET_UTBETALINGS_SPORRING.propertyKey)) {
                startDato
            } else {
                leggTilEkstraDagerPaaStartdato(startDato)
            }

        val utbetalinger =
            utbetaldataV2Api.hentUtbetalingsinformasjonIntern(
                utbetalingsoppslagDTO =
                    UtbetalingsoppslagDTO(
                        ident = fnr.get(),
                        rolle = UtbetalingsoppslagDTO.Rolle.RETTIGHETSHAVER,
                        periode =
                            PeriodeDTO(
                                fom = fomDato,
                                tom = sluttDato,
                            ),
                        periodetype = UtbetalingsoppslagDTO.Periodetype.UTBETALINGSPERIODE,
                    ),
            )

        return utbetalinger
            ?.filter(finnUtbetalingerISokeperioden(startDato, sluttDato))
            ?.sortedBy { it.posteringsdato }
            ?.map(::mapTilDTO)
            .orEmpty()
    }

    private fun finnUtbetalingerISokeperioden(
        start: LocalDate,
        slutt: LocalDate,
    ) = { utbetaling: UtbetalingDTO ->
        val dato =
            listOfNotNull(
                utbetaling.posteringsdato,
                utbetaling.forfallsdato,
                utbetaling.utbetalingsdato,
            ).firstOrNull()

        if (dato == null) {
            false
        } else {
            dato in start..slutt
        }
    }

    private fun mapTilDTO(utbetaling: RsUtbetaling): UtbetalingDomain.Utbetaling =
        UtbetalingDomain.Utbetaling(
            posteringsdato = utbetaling.posteringsdato.toString(),
            utbetalingsdato = utbetaling.utbetalingsdato?.toString(),
            forfallsdato = utbetaling.forfallsdato?.toString(),
            utbetaltTil = utbetaling.utbetaltTil?.navn?.trim(),
            erUtbetaltTilPerson = utbetaling.utbetaltTil?.aktoertype == AktoerDTO.Aktoertype.PERSON,
            erUtbetaltTilOrganisasjon = utbetaling.utbetaltTil?.aktoertype == AktoerDTO.Aktoertype.ORGANISASJON,
            erUtbetaltTilSamhandler = utbetaling.utbetaltTil?.aktoertype == AktoerDTO.Aktoertype.SAMHANDLER,
            nettobelop = utbetaling.utbetalingNettobeloep,
            melding = utbetaling.utbetalingsmelding?.trim(),
            metode = utbetaling.utbetalingsmetode.trim(),
            status = utbetaling.utbetalingsstatus.trim(),
            konto = utbetaling.utbetaltTilKonto?.kontonummer?.trim(),
            ytelser = utbetaling.ytelseListe.map(::hentYtelserForUtbetaling),
        )

    private fun hentYtelserForUtbetaling(ytelse: RsYtelse): UtbetalingDomain.Ytelse =
        UtbetalingDomain.Ytelse(
            type = ytelse.ytelsestype?.trim(),
            ytelseskomponentListe = ytelse.ytelseskomponentListe?.map(::hentYtelsekomponentListe) ?: emptyList(),
            ytelseskomponentersum = ytelse.ytelseskomponentersum,
            trekkListe = ytelse.trekkListe?.map(::hentTrekkListe) ?: emptyList(),
            trekksum = ytelse.trekksum,
            skattListe = ytelse.skattListe?.map(::hentSkattListe) ?: emptyList(),
            skattsum = ytelse.skattsum,
            periode = hentYtelsesperiode(ytelse.ytelsesperiode),
            nettobelop = ytelse.ytelseNettobeloep,
            bilagsnummer = ytelse.bilagsnummer?.trim(),
            arbeidsgiver = ytelse.refundertForOrg?.let { orgnr -> hentArbeidsgiver(orgnr) },
        )

    private fun hentYtelsekomponentListe(ytelseskomponent: RsYtelseKomponent): UtbetalingDomain.YtelseKomponent =
        UtbetalingDomain.YtelseKomponent(
            ytelseskomponenttype = ytelseskomponent.ytelseskomponenttype.trim(),
            satsbelop = ytelseskomponent.satsbeloep,
            satstype = ytelseskomponent.satstype?.trim(),
            satsantall = ytelseskomponent.satsantall,
            ytelseskomponentbelop = ytelseskomponent.ytelseskomponentbeloep,
        )

    private fun hentTrekkListe(trekk: RsTrekk): UtbetalingDomain.Trekk =
        UtbetalingDomain.Trekk(
            trekktype = trekk.trekktype.trim(),
            trekkbelop = trekk.trekkbeloep,
            kreditor = trekk.kreditor?.trim(),
        )

    private fun hentSkattListe(skatt: RsSkatt): UtbetalingDomain.Skatt =
        UtbetalingDomain.Skatt(
            skattebelop = skatt.skattebeloep,
        )

    private fun hentYtelsesperiode(periode: RsPeriode?): UtbetalingDomain.YtelsePeriode? {
        if (periode == null) {
            return null
        }
        return UtbetalingDomain.YtelsePeriode(
            start = periode.fom.toString(),
            slutt = periode.tom.toString(),
        )
    }

    private fun hentArbeidsgiver(aktor: RsAktoer): UtbetalingDomain.Arbeidgiver =
        UtbetalingDomain.Arbeidgiver(
            orgnr = aktor.ident,
            navn = aktor.navn,
        )

    override fun ping() =
        SelfTestCheck("Rest Utbetaling", false) {
            HealthCheckResult.healthy()
        }
}
