package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.sbl.dialogarena.sak.config.SaksoversiktServiceConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv wire inn sine komponenters kontekster for å ha full kontroll over springoppsettet.
 */
@Configuration
@Import({
        UtbetalingLamellContext.class,
        SporsmalOgSvarContext.class,
        SaksoversiktServiceConfig.class})
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
