package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.common.kodeverk.JsonKodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SaksoversiktServiceConfig {

    @Bean
    public SaksoversiktService saksoversiktService() {
        return new SaksoversiktService();
    }

    @Bean
    public BulletProofKodeverkService bulletProofKodeverkService() {
        return new BulletProofKodeverkService();
    }

    @Bean
    public Kodeverk kodeverk() {
        return new JsonKodeverk(getClass().getResourceAsStream("/kodeverk.json"));
    }

}
