package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.gsak

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.FodselnummerAktorService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.UnleashProxySwitcher
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
            sakV1: SakV1,
            behandleSakWS: BehandleSakV1,
            sakApiGateway: SakApiGateway,
            fodselnummerAktorService: FodselnummerAktorService,
            unleashService: UnleashService
        ): GsakSaker {
            val restClient = RestGsakSaker(sakApiGateway, fodselnummerAktorService)
            val soapClient = SoapGsakSaker(sakV1, behandleSakWS)

            return UnleashProxySwitcher.createSwitcher(
                featureToggle = Feature.USE_REST_SAK_IMPL,
                unleashService = unleashService,
                ifEnabled = restClient,
                ifDisabled = soapClient
            )
        }
    }
}

