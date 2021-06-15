package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Configuration
public class CmsEndpointConfig {
    @Bean
    public ContentRetriever propertyResolver() {
        InputStreamReader content = new InputStreamReader(Melding.class.getResourceAsStream("Melding.properties"), StandardCharsets.UTF_8);
        return new ContentRetriever()
                .load("content.saksoversikt")
                .load("content.modiabrukerdialog")
                .load(content);
    }
}