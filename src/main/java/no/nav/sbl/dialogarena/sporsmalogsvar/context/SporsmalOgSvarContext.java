package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.sbl.dialogarena.pdf.HandleBarKjoerer;
import no.nav.sbl.dialogarena.pdf.HtmlGenerator;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SporsmalOgSvarContext {

    @Bean
    public PdfUtils pdfUtils() {
        return new PdfUtils();
    }

    @Bean
    public HtmlGenerator htmlGenerator() {
        return new HandleBarKjoerer();
    }

    @Bean
    public MeldingService meldingService() {
        return new MeldingService();
    }


}
