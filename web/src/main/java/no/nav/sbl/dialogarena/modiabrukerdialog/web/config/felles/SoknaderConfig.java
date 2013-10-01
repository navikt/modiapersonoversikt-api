package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles;

import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.sbl.dialogarena.soknader.service.SoknaderServiceDefault;
import no.nav.sbl.dialogarena.soknader.service.SoknaderServiceMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SoknaderConfig {

    @Profile({"default", "soknaderDefault"})
    @Configuration
    public static class Default {

        @Bean
        public SoknaderService soknaderWidgetService() {
            return new SoknaderServiceDefault();
        }

    }

    @Profile({"test", "soknaderTest"})
    @Configuration
    public static class Test {

        @Bean
        public SoknaderService soknaderWidgetService() {
            return new SoknaderServiceMock();
        }
    }
}
