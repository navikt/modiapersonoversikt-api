package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.apache.commons.io.Charsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.InputStreamReader;

@Configuration
public class CmsEndpointConfig {

    @Bean
    @Primary
    public ContentRetriever cmsContentRetriever() {
        return new ContentRetriever()
                .load("content.saksoversikt", "content.modiabrukerdialog");
    }

    @Bean(name = "propertyResolver")
    public ContentRetriever propertyResolver() {
        InputStreamReader content = new InputStreamReader(Melding.class.getResourceAsStream("Melding.properties"), Charsets.UTF_8);
        return new ContentRetriever()
                .load(content);
    }
}
