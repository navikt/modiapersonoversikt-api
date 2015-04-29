package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SporsmalOgSvarContext {

    @Bean
    public GsakService gsakService() {
        return new GsakServiceImpl();
    }

    @Bean
    public HenvendelseBehandlingService henvendelseBehandlingService() {
        return new HenvendelseBehandlingServiceImpl();
    }

    @Bean
    public MeldingerSok meldingIndekserer() {
        return new MeldingerSokImpl();
    }
}
