package no.nav.kodeverk.consumer.fim.kodeverk.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class EnvironmentPropertiesTestConfig {
    @Bean
    public BeanFactoryPostProcessor init() {
        PropertyPlaceholderConfigurer propertyConfigurer = new PropertyPlaceholderConfigurer();
        propertyConfigurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        propertyConfigurer.setLocation(new ClassPathResource("environment.properties"));
        return propertyConfigurer;
    }
}
