package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired

class SFJournalforing @Autowired constructor(
    private val sakerService: SakerService,
    private val sfHenvendelseService: SfHenvendelseService
) : JournalforingApi {
    override fun hentSaker(fnr: String): SakerService.Resultat {
        return sakerService.hentSaker(fnr)
    }

    override fun knyttTilSak(fnr: String, traadId: String, sak: JournalforingSak, enhet: String) {
        sfHenvendelseService.journalforHenvendelse(
            enhet = enhet,
            kjedeId = traadId,
            saksTema = sak.temaKode,
            fagsakSystem = sak.fagsystemKode,
            saksId = sak.fagsystemSaksId
        )
    }
}
