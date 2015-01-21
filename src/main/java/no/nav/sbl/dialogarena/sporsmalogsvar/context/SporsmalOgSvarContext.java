package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SporsmalOgSvarContext {

    @Bean
    public GsakService gsakService() {
        return new GsakService();
    }

    @Bean
    public HenvendelseBehandlingService henvendelseBehandlingService() {
        return new HenvendelseBehandlingService();
    }
}
