package no.nav.modiapersonoversikt.service.persondata

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.representasjon.RepresentasjonApi
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.Tilgangsmaskinen
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgang
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.slf4j.LoggerFactory
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig as Kodeverk

class RepresentasjonsoppdragService(
    private val representasjonApi: RepresentasjonApi,
    private val pdl: PdlOppslagService,
    private val tilgangsmaskinen: Tilgangsmaskinen,
    private val kodeverk: EnhetligKodeverk.Service,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val personMapper = TredjepartspersonMapper(kodeverk)

    fun hentRepresentasjonsOppdrag(fnr: Fnr): Representasjonsoppdrag {
        val feil = mutableSetOf<RepresentasjonsoppdragFeil>()
        val fullmakter =
            hent(RepresentasjonsoppdragFeil.FULLMAKTER, feil) {
                representasjonApi.hentFullmakterForFullmektig(fnr).also { oppdrag ->
                    require(oppdrag.all { it.fullmektig == fnr.get() }) { "Uventet fullmektig i repr-respons" }
                }
            }.orEmpty()
        val vergemal =
            hent(RepresentasjonsoppdragFeil.VERGEMAL, feil) {
                representasjonApi.hentVergemalForVerge(fnr).also { oppdrag ->
                    require(oppdrag.all { it.verge == fnr.get() }) { "Uventet verge i repr-respons" }
                }
            }.orEmpty()
        val identer =
            (fullmakter.map { it.fullmaktsgiver } + vergemal.map { it.vergehaver })
                .filter { it.isNotBlank() }
                .distinct()
        val tilganger =
            if (identer.isEmpty()) {
                emptyMap()
            } else {
                tilgangsmaskinen.sjekkTilganger(AuthContextUtils.requireNavIdent(), identer)
            }
        if (identer.any { tilganger[it] == null || tilganger[it]?.tilgang == TredjepersonTilgang.UAVKLART }) {
            feil.add(RepresentasjonsoppdragFeil.TILGANGSMASKINEN)
        }
        val godkjente = identer.filter { tilganger[it]?.tilgang == TredjepersonTilgang.GODKJENT }
        val personer =
            godkjente
                .chunked(PDL_BOLKSTORRELSE)
                .flatMap { bolk ->
                    val resultat =
                        hent(RepresentasjonsoppdragFeil.PERSONOPPLYSNINGER, feil) {
                            pdl.hentTredjepartspersondata(bolk)
                        }.orEmpty()
                    val funn = resultat.groupBy { it.ident }
                    bolk.mapNotNull { ident ->
                        val person = funn[ident]?.singleOrNull()?.person
                        if (person == null) {
                            feil.add(RepresentasjonsoppdragFeil.PERSONOPPLYSNINGER)
                            null
                        } else {
                            ident to personMapper.lagTredjepartsperson(ident, person, null)
                        }
                    }
                }.toMap()

        fun motpart(ident: String): RepresentertPerson {
            val person = personer[ident]
            val tilgang = tilganger[ident]
            val avvist = tilgang?.tilgang == TredjepersonTilgang.AVVIST
            return RepresentertPerson(
                ident = ident.takeIf { person != null },
                navn = person?.navn?.firstOrNull(),
                skjulingsaarsak =
                    when {
                        person != null -> null
                        avvist -> Persondata.Skjulingsaarsak.MANGLER_TILGANG
                        else -> Persondata.Skjulingsaarsak.UAVKLART
                    },
                tilgangsavslag = tilgang?.avslag.takeIf { avvist },
            )
        }

        return Representasjonsoppdrag(
            fullmaktsgivere =
                fullmakter.map {
                    Fullmaktsoppdrag(
                        id = it.fullmaktId,
                        person = motpart(it.fullmaktsgiver),
                        gyldigFraOgMed = it.gyldigFraOgMed,
                        gyldigTilOgMed = it.gyldigTilOgMed,
                        leserettigheter = temaer(it.leserettigheter),
                        skriverettigheter = temaer(it.skriverettigheter),
                    )
                },
            vergehavere =
                vergemal.map {
                    Vergeoppdrag(
                        id = it.id,
                        person = motpart(it.vergehaver),
                        gyldigFraOgMed = it.gyldigFraOgMed,
                        aktivt = it.aktivt,
                        vergemaalstype = it.vergemaalstype,
                        leserettigheter = temaer(it.leserettigheter),
                        skriverettigheter = temaer(it.skriverettigheter),
                    )
                },
            feilendeSystemer = feil,
        )
    }

    private fun temaer(koder: Set<String>): List<Persondata.KodeBeskrivelse<String>> =
        koder.sorted().map { kodeverk.hentKodeBeskrivelse(Kodeverk.TEMA, it) }

    private fun <T> hent(
        system: RepresentasjonsoppdragFeil,
        feil: MutableSet<RepresentasjonsoppdragFeil>,
        block: () -> T,
    ): T? =
        try {
            block()
        } catch (e: Exception) {
            feil.add(system)
            logger.warn("Representasjonsoppdrag: oppslag feilet for {} ({})", system, e.javaClass.simpleName)
            null
        }

    private companion object {
        const val PDL_BOLKSTORRELSE = 100
    }
}
