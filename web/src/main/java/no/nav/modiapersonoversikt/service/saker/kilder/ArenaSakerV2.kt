package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.arena.services.lib.sakvedtak.SaksInfoListe
import no.nav.arena.services.sakvedtakservice.Bruker
import no.nav.arena.services.sakvedtakservice.HentSaksInfoListeRequestV2
import no.nav.arena.services.sakvedtakservice.SakVedtakPortType
import no.nav.modiapersonoversikt.service.saker.Sak
import no.nav.modiapersonoversikt.service.saker.SakerKilde
import org.joda.time.DateTime
import javax.xml.ws.Holder

internal class ArenaSakerV2(val arenaSakVedtakService: SakVedtakPortType) : SakerKilde {
    override val kildeNavn: String
        get() = "ARENA"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        if (saker.none(Sak.IS_ARENA_OPPFOLGING::test)) {
            hentOppfolgingssakFraArena(fnr)
                ?.also { saker.add(it) }
        }
    }

    private fun hentOppfolgingssakFraArena(fnr: String): Sak? {
        val request = HentSaksInfoListeRequestV2()
            .withBruker(Bruker().withBrukertypeKode("PERSON").withBrukerId(fnr))
            .withTema("OPP")

        val saksInfoListe: Holder<SaksInfoListe> = Holder()

        arenaSakVedtakService.hentSaksInfoListeV2(
            Holder(request.bruker),
            request.saksId,
            request.fomDato,
            request.tomDato,
            request.tema,
            request.isLukket,
            saksInfoListe
        )

        return saksInfoListe.value.saksInfo.firstOrNull()?.let(TIL_SAK)
    }

    companion object {
        private val TIL_SAK = { arenaSak: no.nav.arena.services.lib.sakvedtak.SaksInfo ->
            Sak().apply {
                saksId = arenaSak.saksId
                fagsystemSaksId = arenaSak.saksId
                fagsystemKode = Sak.FAGSYSTEMKODE_ARENA
                sakstype = Sak.SAKSTYPE_MED_FAGSAK
                temaKode = arenaSak.tema
                opprettetDato = DateTime(arenaSak.sakOpprettet)
                finnesIGsak = false
            }
        }
    }
}