package no.nav.sbl.dialogarena.modiabrukerdialog.sak.config;

import no.nav.modig.content.ContentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnonicConfig {

    @Bean(name = "saksoversikt-cms-integrasjon")
    public ContentRetriever contentRetriever() {
        return new ContentRetriever().load("content.saksoversikt");
    }
}
