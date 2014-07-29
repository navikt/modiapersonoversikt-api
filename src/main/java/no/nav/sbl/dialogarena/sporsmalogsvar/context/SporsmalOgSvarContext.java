package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.ValgtEnhetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SporsmalOgSvarContext {

    @Bean
    public MeldingService meldingService() {
        return new MeldingService();
    }

    @Bean
    public ValgtEnhetService valgtEnhetService() {
        return new ValgtEnhetService();
    }
}
