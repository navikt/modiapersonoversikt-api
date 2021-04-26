package no.nav.sbl.dialogarena.varsel.config;

import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import no.nav.sbl.dialogarena.varsel.service.VarslerServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VarslingContext {
    @Bean
    public VarslerService varslerService() {
        return new VarslerServiceImpl();
    }

}
