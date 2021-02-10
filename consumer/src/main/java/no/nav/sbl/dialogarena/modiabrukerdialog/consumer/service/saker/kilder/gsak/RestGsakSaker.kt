package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.gsak

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent.IdentGruppe.AKTORID
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.OpprettSakDto
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import org.joda.time.DateTime
import java.time.Clock
import java.time.OffsetDateTime

class RestGsakSaker(
    private val sakApiGateway: SakApiGateway,
    private val pdlOppslagService: PdlOppslagService,
    private val clock: Clock = Clock.systemDefaultZone()
) : GsakSaker {
    override val kildeNavn: String = "SAK"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        val response = sakApiGateway.hentSaker(identMapping(fnr, AKTORID))
        val gsakSaker = response.map(TIL_SAK)
        saker.addAll(gsakSaker)
    }

    override fun opprettSak(fnr: String, sak: Sak): String {
        val ident = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val opprettetSak = sakApiGateway.opprettSak(
            OpprettSakDto(
                aktoerId = identMapping(fnr, AKTORID),
                tema = sak.temaKode,
                fagsakNr = sak.fagsystemSaksId,
                applikasjon = sak.fagsystemKode,
                opprettetAv = ident
            )
        )

        return opprettetSak.id
            ?: throw IllegalStateException("Opprettelse av Sak returnerte object som manglet id")
    }

    private fun identMapping(ident: String, type: HentIdent.IdentGruppe): String {
        val identliste = pdlOppslagService.hentIdent(ident)
        return identliste
            ?.identer
            ?.find { it -> it.gruppe == type }
            ?.ident
            ?: throw IllegalStateException("PDL Oppslag feilet ved mapping av $ident til $type")
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

        private fun convertJavaDateTimeToJoda(dateTime: OffsetDateTime): DateTime {
            return DateTime(dateTime.toInstant().toEpochMilli())
        }
    }
}
