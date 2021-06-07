package no.nav.modiapersonoversikt.config.endpoint.cms;

import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
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
