package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService
import org.springframework.beans.factory.annotation.Autowired

class JoarkJournalforing @Autowired constructor(
    val sakerService: SakerService
) : JournalforingApi {
    override fun hentSaker(fnr: String): SakerService.Resultat {
        return sakerService.hentSaker(fnr)
    }

    override fun knyttTilSak(fnr: String, traadId: String, sak: Sak, enhet: String) {
        sakerService.knyttBehandlingskjedeTilSak(
            fnr = fnr,
            behandlingskjede = traadId,
            sak = sak,
            enhet = enhet
        )
    }
}
