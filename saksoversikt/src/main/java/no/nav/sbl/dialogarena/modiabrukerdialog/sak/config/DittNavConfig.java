package no.nav.sbl.dialogarena.modiabrukerdialog.sak.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.PDFConverterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DittNavConfig {


    @Bean
    public PDFConverterService pdfConverterService() {
        return new PDFConverterService();
    }

}
