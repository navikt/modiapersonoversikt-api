package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.ArenaService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.JoarkJournalforingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SporsmalOgSvarContext {

    @Bean
    public GsakService gsakService() {
        return new GsakService();
    }

    @Bean
    public ArenaService arenaService() {
        return new ArenaService();
    }

    @Bean
    public HenvendelseBehandlingService henvendelseBehandlingService() {
        return new HenvendelseBehandlingService();
    }

    @Bean
    public JoarkJournalforingService joarkService() {
        return new JoarkJournalforingService();
    }

}
