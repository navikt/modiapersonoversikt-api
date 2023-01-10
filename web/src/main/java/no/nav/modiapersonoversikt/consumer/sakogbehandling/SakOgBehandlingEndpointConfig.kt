package no.nav.modiapersonoversikt.consumer.sakogbehandling

import no.nav.common.cxf.CXFClient
import no.nav.common.cxf.StsConfig
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.xml.namespace.QName

@Configuration
open class SakOgBehandlingEndpointConfig {
    @Autowired
    private val stsConfig: StsConfig? = null

    @Bean
    open fun sakOgBehandlingPortType(): SakOgBehandlingV1 {
        val porttype = createSakogbehandlingPortType()
            .configureStsForSubject(stsConfig)
            .build()

        return MetricsFactory.createTimerProxyForWebService("SakOgBehandling", porttype, SakOgBehandlingV1::class.java)
    }

    @Bean
    open fun pingSakOgBehandling(): Pingable {
        val porttype = createSakogbehandlingPortType()
            .configureStsForSystemUser(stsConfig)
            .build()

        return PingableWebService("Sak og behandling", porttype)
    }

    private fun createSakogbehandlingPortType(): CXFClient<SakOgBehandlingV1> {
        return CXFClient(SakOgBehandlingV1::class.java)
            .timeout(15000, 15000)
            .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/Binding.wsdl")
            .serviceName(QName("http://nav.no/tjeneste/virksomhet/sakOgBehandling/v1/Binding", "SakOgBehandling_v1"))
            .endpointName(
                QName(
                    "http://nav.no/tjeneste/virksomhet/sakOgBehandling/v1/Binding",
                    "SakOgBehandling_v1Port"
                )
            )
            .address(EnvironmentUtils.getRequiredProperty("SAKOGBEHANDLING_ENDPOINTURL"))
    }
}
