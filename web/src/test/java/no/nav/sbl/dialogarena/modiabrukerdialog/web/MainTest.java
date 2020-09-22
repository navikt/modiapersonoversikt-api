package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.common.nais.NaisYamlUtils;
import no.nav.common.rest.client.RestClient;
import no.nav.common.test.SystemProperties;
import no.nav.common.test.ssl.SSLTestUtils;
import no.nav.common.test.ssl.TrustAllSSLSocketFactory;
import org.springframework.boot.SpringApplication;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MainTest {
    static {
        setupRestClient();
        System.setProperty("NAIS_APP_NAME", "modiapersonoversikt-api");
        SystemProperties.setFrom(".vault.properties");
        NaisYamlUtils.loadFromYaml(".nais/nais-q0.yml");
        SSLTestUtils.disableCertificateChecks();
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Main.class);
        application.setAdditionalProfiles("local");
        application.run(args);
    }

    private static void setupRestClient() {
        RestClient.setBaseClient(RestClient.baseClientBuilder()
                .sslSocketFactory(new TrustAllSSLSocketFactory(), new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                })
                .build());
    }
}
