package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingServiceImpl;
import org.springframework.context.annotation.Bean;

import javax.inject.Named;

public class ServiceTestContext {

    @Bean
    @Named("henvendelseBehandlingServiceProd")
    public HenvendelseBehandlingService henvendelseBehandlingServiceProd() {
        return new HenvendelseBehandlingServiceImpl();
    }

}
