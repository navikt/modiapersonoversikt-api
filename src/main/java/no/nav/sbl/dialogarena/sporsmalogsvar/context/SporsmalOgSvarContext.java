package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SporsmalOgSvarContext {

    @Bean
    public GsakService gsakService() {
        return new GsakService();
    }

    @Bean
    public SakerService sakerService() {
        return new SakerServiceImpl();
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
