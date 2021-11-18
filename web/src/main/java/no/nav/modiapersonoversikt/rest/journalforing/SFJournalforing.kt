package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired

class SFJournalforing @Autowired constructor(
    val sakerService: SakerService,
    val sfHenvendelseService: SfHenvendelseService
) : JournalforingApi {
    override fun hentSaker(fnr: String): SakerService.Resultat {
        return sakerService.hentSaker(fnr)
    }

    override fun knyttTilSak(fnr: String, traadId: String, sak: Sak, enhet: String) {
        if (sak.fagsystemKode == Sak.BIDRAG_MARKOR || sak.temaKode == Sak.BIDRAG_MARKOR) {
            sfHenvendelseService.journalforHenvendelse(
                enhet = enhet,
                kjedeId = traadId,
                saksTema = "BID",
                fagsakSystem = Sak.FAGSYSTEMKODE_BIDRAG,
                saksId = null
            )
        } else {
            sfHenvendelseService.journalforHenvendelse(
                enhet = enhet,
                kjedeId = traadId,
                saksTema = sak.temaKode,
                fagsakSystem = sak.fagsystemKode,
                saksId = sak.fagsystemSaksId
            )
        }
    }
}
