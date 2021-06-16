package no.nav.modiapersonoversikt.legacy.varsel.config;

import no.nav.modiapersonoversikt.legacy.varsel.service.VarslerService;
import no.nav.modiapersonoversikt.legacy.varsel.service.VarslerServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VarslingContext {
    @Bean
    public VarslerService varslerService() {
        return new VarslerServiceImpl();
    }

}
