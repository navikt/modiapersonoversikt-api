package no.nav.modiapersonoversikt.consumer.arena.arenaSakVedtakService

import no.nav.arena.services.lib.sakvedtak.SaksInfoListe
import no.nav.arena.services.sakvedtakservice.Bruker
import no.nav.arena.services.sakvedtakservice.HentSaksInfoListeRequestV2
import no.nav.arena.services.sakvedtakservice.SakVedtakPortType
import no.nav.common.cxf.CXFClient
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.config.AppConstants
import no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import no.nav.modiapersonoversikt.utils.Utils
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor
import org.apache.wss4j.common.ext.WSPasswordCallback
import org.apache.wss4j.dom.WSConstants
import org.apache.wss4j.dom.handler.WSHandlerConstants
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.security.auth.callback.Callback
import javax.security.auth.callback.CallbackHandler
import javax.xml.datatype.DatatypeFactory
import javax.xml.namespace.QName
import javax.xml.ws.Holder

@Configuration
open class ArenaSakVedtakServiceConfig {
    private val log = LoggerFactory.getLogger(ArenaSakVedtakServiceConfig::class.java)
    val address: String = EnvironmentUtils.getRequiredProperty("ARENA_SAK_VEDTAK_URL")

    @Bean
    open fun sakVedtakService(): SakVedtakPortType {
        val prod: SakVedtakPortType = createSakVedtakPortType()
        return MetricsFactory.createTimerProxyForWebService(
            "SakVedtakPortType",
            prod,
            SakVedtakPortType::class.java
        )
    }

    @Bean
    open fun sakVedtakPortTypePing(sakVedtakPortType: SakVedtakPortType): Pingable {
        val dagensDato = LocalDate.now()
        val xmlDato = DatatypeFactory.newInstance().newXMLGregorianCalendar(dagensDato.toString())
        val hentSaksInfoListeRequestV2 = HentSaksInfoListeRequestV2()
            .withBruker(Bruker().withBrukertypeKode("PERSON").withBrukerId("10108000398"))
            .withFomDato(xmlDato)
            .withTomDato(xmlDato)
        val saksInfoListe = SaksInfoListe()
        val sak: Holder<SaksInfoListe> = Holder(saksInfoListe)

        val selftest = SelfTestCheck(
            String.format("SakVedtakService via %s", address),
            false
        ) {
            try {
                sakVedtakPortType.hentSaksInfoListeV2(
                    Holder(hentSaksInfoListeRequestV2.bruker),
                    hentSaksInfoListeRequestV2.saksId,
                    hentSaksInfoListeRequestV2.fomDato,
                    hentSaksInfoListeRequestV2.tomDato,
                    hentSaksInfoListeRequestV2.tema,
                    hentSaksInfoListeRequestV2.isLukket,
                    sak
                )
                HealthCheckResult.healthy()
            } catch (e: Exception) {
                log.error("Ukjent ved under kall på sakVedtakPortTypePing: ${e.message} ${e.cause}", e)
                HealthCheckResult.unhealthy(e)
            }
        }

        return Pingable { selftest }
    }

    private fun createSakVedtakPortType(): SakVedtakPortType {
        return Utils.withProperty(
            "disable.ssl.cn.check",
            "true"
        ) {
            CXFClient(SakVedtakPortType::class.java)
                .address(address)
                .wsdl("classpath:wsdl/arenasakvedtakservice.wsdl")
                .serviceName(QName("http://arena.nav.no/services/sakvedtakservice", "ArenaSakVedtakService"))
                .endpointName(QName("http://arena.nav.no/services/sakvedtakservice", "ArenaSakVedtakServicePort"))
                .withOutInterceptor(WSS4JOutInterceptor(getSecurityProps()))
                .build()
        }
    }

    private fun getSecurityProps(): Map<String, Any> {
        val user: String = EnvironmentUtils.getRequiredProperty(
            "service_user.username",
            AppConstants.SYSTEMUSER_USERNAME_PROPERTY
        )
        val password: String = EnvironmentUtils.getRequiredProperty(
            "service_user.password",
            AppConstants.SYSTEMUSER_PASSWORD_PROPERTY
        )
        val props: MutableMap<String, Any> = HashMap()
        props[WSHandlerConstants.ACTION] = WSHandlerConstants.USERNAME_TOKEN
        props[WSHandlerConstants.USER] = user
        props[WSHandlerConstants.PASSWORD_TYPE] = WSConstants.PW_TEXT
        props[WSHandlerConstants.PW_CALLBACK_REF] = CallbackHandler { callbacks: Array<Callback> ->
            val passwordCallback = callbacks[0] as WSPasswordCallback
            passwordCallback.password = password
        }
        return props
    }
}
