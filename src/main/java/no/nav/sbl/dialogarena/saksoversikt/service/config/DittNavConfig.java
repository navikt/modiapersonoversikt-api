package no.nav.sbl.dialogarena.saksoversikt.service.config;

import no.nav.sbl.dialogarena.saksoversikt.service.service.PDFConverterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DittNavConfig {


    @Bean
    public PDFConverterService pdfConverterService() {
        return new PDFConverterService();
    }

}
