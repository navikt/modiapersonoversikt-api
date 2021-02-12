package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.gsak

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.FodselnummerAktorService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1
import no.nav.tjeneste.virksomhet.sak.v1.SakV1
import org.slf4j.LoggerFactory

interface GsakSaker : SakerKilde {
    fun opprettSak(fnr: String, sak: Sak): String

    companion object {
        const val VEDTAKSLOSNINGEN = "FS36"
        const val SAKSTYPE_GENERELL = "GEN"
        const val SAKSTYPE_MED_FAGSAK = "MFS"

        val log = LoggerFactory.getLogger(GsakSaker::class.java)
        fun createProxy(
                sakApiGateway: SakApiGateway,
                fodselnummerAktorService: FodselnummerAktorService
        ): GsakSaker {
            return RestGsakSaker(sakApiGateway, fodselnummerAktorService)
        }
    }
}

