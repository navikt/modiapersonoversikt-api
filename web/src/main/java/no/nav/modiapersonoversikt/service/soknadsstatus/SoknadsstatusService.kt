package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.apis.SoknadsstatusControllerApi
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Behandling
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Hendelse
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient

interface SoknadsstatusService {
    fun hentHendelser(ident: String): List<Hendelse>
    fun hentBehandlinger(ident: String): List<Behandling>
    fun ping()
}

class SoknadsstatusServiceImpl(private val soknadsstatusApi: SoknadsstatusControllerApi) : SoknadsstatusService {
    override fun hentHendelser(ident: String): List<Hendelse> {
        TODO("Not yet implemented")
    }

    override fun hentBehandlinger(ident: String): List<Behandling> {
        TODO("Not yet implemented")
    }

    override fun ping() {
        TODO("Not yet implemented")
    }

}

