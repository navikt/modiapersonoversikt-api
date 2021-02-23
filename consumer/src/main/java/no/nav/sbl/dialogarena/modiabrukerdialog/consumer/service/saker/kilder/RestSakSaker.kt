package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.FodselnummerAktorService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.OpprettSakDto
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import org.joda.time.DateTime
import java.time.OffsetDateTime

class RestSakSaker(
    private val sakApiGateway: SakApiGateway,
    private val fodselnummerAktorService: FodselnummerAktorService
): SakerKilde {
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

     fun opprettSak(fnr: String, sak: Sak): String {
        val ident = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val opprettetSak = sakApiGateway.opprettSak(
            OpprettSakDto(
                aktoerId = requireNotNull(fodselnummerAktorService.hentAktorIdForFnr(fnr)) {
                    "Kan ikke opprette sak når mapping til aktorId feilet"
                },
                tema = sak.temaKode,
                fagsakNr = sak.fagsystemSaksId,
                applikasjon = sak.fagsystemKode,
                opprettetAv = ident
            )
        )

        return opprettetSak.id
            ?: throw IllegalStateException("Opprettelse av Sak returnerte object som manglet id")
    }

    companion object {
        const val VEDTAKSLOSNINGEN = "FS36"
        const val SAKSTYPE_GENERELL = "GEN"
        const val SAKSTYPE_MED_FAGSAK = "MFS"

        val TIL_SAK = { sakDto: SakDto ->
            Sak().apply {
                opprettetDato = sakDto.opprettetTidspunkt?.let { convertJavaDateTimeToJoda(it) }
                saksId = sakDto.id.toString()
                fagsystemSaksId = getFagsystemSakId(sakDto)
                temaKode = sakDto.tema
                fagsystemKode = sakDto.applikasjon ?: FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
                finnesIGsak = true
                sakstype = getSakstype(sakDto)
            }
        }

        private fun getSakstype(sakDto: SakDto): String {
            return when (sakDto.applikasjon) {
                VEDTAKSLOSNINGEN -> SAKSTYPE_MED_FAGSAK
                else -> {
                    if (sakDto.fagsakNr != null)
                        SAKSTYPE_MED_FAGSAK
                    else
                        SAKSTYPE_GENERELL
                }
            }
        }

        private fun getFagsystemSakId(sakDto: SakDto): String? {
            return if (VEDTAKSLOSNINGEN == sakDto.applikasjon && sakDto.fagsakNr == null) sakDto.id.toString() else sakDto.fagsakNr
        }

        private fun convertJavaDateTimeToJoda(dateTime: OffsetDateTime): DateTime {
            return DateTime(dateTime.toInstant().toEpochMilli())
        }
    }
}
