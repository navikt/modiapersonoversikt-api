package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
public class ServicesConfig {

    @Bean
    public HenvendelseUtsendingService henvendelseUtsendingService() {
        return new HenvendelseUtsendingService();
    }

    @Bean
    public OppgaveBehandlingService oppgaveBehandlingService() {
        return new OppgaveBehandlingService();
    }

    @Bean
    public AnsattService ansattService() {
        return new AnsattService();
    }

}
