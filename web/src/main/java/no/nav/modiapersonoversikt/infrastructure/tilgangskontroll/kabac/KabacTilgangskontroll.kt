package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac

import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.*
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService

class KabacTilgangskontroll(
    pdl: PdlOppslagService,
    skjermingApi: SkjermedePersonerApi,
    norg: NorgApi,
    ansattService: AnsattService,
    henvendelseService: SfHenvendelseService,
    ldap: LDAPService
) : Kabac by Kabac.Impl() {
    init {
        install(AuthContextPip)
        install(NavIdentPip)
        install(BrukersFnrPip(pdl))
        install(BrukersAktorIdPip(pdl))
        install(BrukersDiskresjonskodePip(pdl))
        install(BrukersSkjermingPip(skjermingApi))
        install(BrukersEnhetPip(norg))
        install(BrukersGeografiskeTilknyttningPip(pdl))
        install(BrukersRegionEnhetPip(norg))
        install(VeiledersEnheterPip(ansattService))
        install(VeiledersRegionEnheterPip(norg))
        install(VeiledersRollerPip(ldap))
        install(VeiledersTemaPip(ansattService))
        install(HenvendelseEierPip(henvendelseService))
        install(InternalTilgangPip())
    }
}
