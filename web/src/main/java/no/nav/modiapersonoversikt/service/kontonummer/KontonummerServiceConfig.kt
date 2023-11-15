package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.modiapersonoversikt.consumer.kontoregister.generated.apis.KontoregisterV1Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KontonummerServiceConfig {

    @Autowired
    private lateinit var kontoregister: KontoregisterV1Api

    @Bean
    open fun kontonummerService() = KontonummerServiceImpl(KontonummerRegisterService(kontoregister))
}
