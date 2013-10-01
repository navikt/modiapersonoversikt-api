package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles;

import no.nav.sbl.dialogarena.soknader.service.SoknaderWidgetServiceMock;
import no.nav.sbl.dialogarena.soknader.service.SoknaderWidgetService;
import no.nav.sbl.dialogarena.soknader.service.SoknaderWidgetServiceDefault;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SoknaderConfig {

    @Profile({"default", "soknaderDefault"})
    @Configuration
    public static class Default {

        @Bean
        public SoknaderWidgetService soknaderWidgetService() {
            return new SoknaderWidgetServiceDefault();
        }

    }

    @Profile({"test", "soknaderTest"})
    @Configuration
    public static class Test {

        @Bean
        public SoknaderWidgetService soknaderWidgetService() {
            return new SoknaderWidgetServiceMock();
        }
    }
}
