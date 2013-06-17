package no.nav.dialogarena.modiabrukerdialog.example.config;

import no.nav.dialogarena.modiabrukerdialog.example.service.DefaultExampleService;
import no.nav.dialogarena.modiabrukerdialog.example.service.ExampleService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"test", "default"})
@Configuration
public class ExampleContext {

    @Bean
    public ExampleService exampleService() {
        return new DefaultExampleService();
    }

}
