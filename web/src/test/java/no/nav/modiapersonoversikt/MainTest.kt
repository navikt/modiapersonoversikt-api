package no.nav.modiapersonoversikt

import no.nav.common.nais.NaisYamlUtils
import no.nav.common.rest.client.RestClient
import no.nav.common.test.SystemProperties
import no.nav.common.test.ssl.SSLTestUtils
import no.nav.common.test.ssl.TrustAllSSLSocketFactory
import org.springframework.boot.builder.SpringApplicationBuilder
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

object MainTest {
    init {
        setupRestClient()
        System.setProperty("NAIS_APP_NAME", "modiapersonoversikt-api")
        System.setProperty("NAIS_CLUSTER_NAME", "dev-gcp")
        System.setProperty("UNLEASH_SERVER_API_URL", "https://unleash-api.dev-gcp.nais.io/api")
        System.setProperty("UNLEASH_SERVER_API_TOKEN", "test")
        SystemProperties.setFrom(".vault.properties")
        NaisYamlUtils.loadFromYaml(".nais/dev.yml")
        SSLTestUtils.disableCertificateChecks()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        SpringApplicationBuilder(Main::class.java)
            .initializers(localBeans)
            .lazyInitialization(true)
            .profiles("local")
            .run(*args)
    }

    private fun setupRestClient() {
        RestClient.setBaseClient(
            RestClient
                .baseClientBuilder()
                .sslSocketFactory(
                    TrustAllSSLSocketFactory(),
                    object : X509TrustManager {
                        override fun checkClientTrusted(
                            x509Certificates: Array<X509Certificate>,
                            s: String,
                        ) {
                        }

                        override fun checkServerTrusted(
                            x509Certificates: Array<X509Certificate>,
                            s: String,
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
                    },
                ).build(),
        )
    }
}
