package no.nav.modiapersonoversikt.service.varsel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VarslerServiceConfig {
    @Bean
    public VarslerService varslerService() {
        return new VarslerServiceImpl();
    }

}
