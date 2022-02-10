package no.nav.modiapersonoversikt.service

import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.HenvendelseInfoApi
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseLesService
import no.nav.modiapersonoversikt.service.sfhenvendelse.fixKjedeId

class HenvendelseLesServiceImpl(
    private val sfHenvendelse: HenvendelseInfoApi,
) : HenvendelseLesService {
    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        val kjedeId = behandlingsIder.map { it.fixKjedeId() }.distinct()
        require(kjedeId.size == 1) {
            "Fant flere unike kjedeIder i samme spørring. Dette skal ikke være mulig mot SF"
        }
        val henvendelse = sfHenvendelse.henvendelseinfoHenvendelseKjedeIdGet(kjedeId.first(), getCallId())
        return henvendelse.fnr == fnr
    }
}
