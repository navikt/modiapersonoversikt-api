package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.pdfconverter;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.domene.brevogarkiv.sanntidpdfkonverterer.v1.SanntidPdfKonvertererV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.PdfKonvertererMock.createSanntidPdfKonverterMock;

@Configuration
public class PdfKonverterConfig {

    public static final String PDF_CONV_MOCK_KEY = "sanntid.pdf.konv.withmock";

    @Bean
    public SanntidPdfKonvertererV1 sanntidPdfKonvertererV1() {
        SanntidPdfKonvertererV1 prod = createSanntidPdfKonverterer(new UserSAMLOutInterceptor());
        SanntidPdfKonvertererV1 mock = createSanntidPdfKonverterMock();
        return createSwitcher(prod, mock, PDF_CONV_MOCK_KEY, SanntidPdfKonvertererV1.class);
    }

    @Bean
    public Pingable pingPdfKonverterer() {
        SanntidPdfKonvertererV1 ws = createSanntidPdfKonverterer(new SystemSAMLOutInterceptor());
        return new PingableWebService("PdfKonverterer", ws);
    }


    private static SanntidPdfKonvertererV1 createSanntidPdfKonverterer(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(SanntidPdfKonvertererV1.class)
                .address(getProperty("sanntid.pdf.konv.v1.url"))
                .withOutInterceptor(interceptor)
                .build();
    }
}
