package no.nav.dialogarena.modiabrukerdialog.example.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductionApplicationContext {

    @Bean
    public String getName() {
        return "production";
    }

}
