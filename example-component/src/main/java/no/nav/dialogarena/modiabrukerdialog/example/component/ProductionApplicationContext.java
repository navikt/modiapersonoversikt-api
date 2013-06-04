package no.nav.dialogarena.modiabrukerdialog.example.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductionApplicationContext {

    @Value("service")
    private String url;

    @Bean
    public String getName() {
        return "production";
    }

    @Bean
    public DefaultService service() {
        return new DefaultService(url);
    }

}
