package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.gsak

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.FodselnummerAktorService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import org.joda.time.DateTime
import java.time.Clock
import java.time.ZonedDateTime

class RestGsakSaker(
    private val sakApiGateway: SakApiGateway,
    private val fodselnummerAktorService: FodselnummerAktorService,
    private val clock: Clock = Clock.systemDefaultZone()
) : GsakSaker {
    override val kildeNavn: String = "SAK"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val response = sakApiGateway.hentSaker(
            requireNotNull(fodselnummerAktorService.hentAktorIdForFnr(fnr)) {
                "Kan ikke hente ut saker når mapping til aktorId feilet"
            }
        )
        val gsakSaker = response.map(TIL_SAK)
        saker.addAll(gsakSaker)
    }

    override fun opprettSak(fnr: String, sak: Sak): String {
        val ident = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val opprettetSak = sakApiGateway.opprettSak(
            SakDto(
                id = sak.saksId,
                tema = sak.temaKode,
                applikasjon = sak.fagsystemKode,
                aktoerId = fodselnummerAktorService.hentAktorIdForFnr(fnr),
                orgnr = null,
                fagsakNr = sak.fagsystemSaksId,
                opprettetAv = ident,
                opprettetTidspunkt = ZonedDateTime.now(clock)
            )
        )

        return opprettetSak.id
            ?: throw IllegalStateException("Opprettelse av Sak returnerte object som manglet id")
    }

    companion object {
        val TIL_SAK = { sakDto: SakDto ->
            Sak().apply {
                opprettetDato = sakDto.opprettetTidspunkt?.let { convertJavaDateTimeToJoda(it) }
                saksId = sakDto.id.toString()
                fagsystemSaksId = getFagsystemSakId(sakDto)
                temaKode = sakDto.tema
                fagsystemKode = sakDto.applikasjon
                finnesIGsak = true
                sakstype = getSakstype(sakDto)
            }
        }

        private fun getSakstype(sakDto: SakDto): String {
            return when (sakDto.applikasjon) {
                GsakSaker.VEDTAKSLOSNINGEN -> GsakSaker.SAKSTYPE_MED_FAGSAK
                else -> {
                    if (sakDto.fagsakNr != null)
                        GsakSaker.SAKSTYPE_MED_FAGSAK
                    else
                        GsakSaker.SAKSTYPE_GENERELL
                }
            }
        }

        private fun getFagsystemSakId(sakDto: SakDto): String? {
            return if (GsakSaker.VEDTAKSLOSNINGEN == sakDto.applikasjon) sakDto.id.toString() else sakDto.fagsakNr
        }

        private fun convertJavaDateTimeToJoda(dateTime: ZonedDateTime): DateTime {
            return DateTime(dateTime.toInstant().toEpochMilli())
        }
    }
}
