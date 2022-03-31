package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.service.saker.Sak
import no.nav.modiapersonoversikt.service.saker.SakerService
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
        sfHenvendelseService.journalforHenvendelse(
            enhet = enhet,
            kjedeId = traadId,
            saksTema = sak.temaKode,
            fagsakSystem = sak.fagsystemKode,
            saksId = if (sak.temaKode == "BID") null else sak.fagsystemSaksId
        )
    }
}
