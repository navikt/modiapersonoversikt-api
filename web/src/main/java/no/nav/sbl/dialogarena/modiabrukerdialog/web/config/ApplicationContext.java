package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.PriorityOrdered;

@Configuration
@Import({ComponentsContext.class, CacheConfig.class})
public class ApplicationContext implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public WicketApplication modiaApplication() {
        return new WicketApplication();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        // Fungerer ikke for �yeblikket p.g.a. at komponenter tydeligvis angir Spring-config-klasser
        // som ikke fungerer i sine respektive ResourceReferences-implementasjoner. F.eks. dras det inn b�de
        // *Config og *TestConfig klasser, virker rart.
        // Vi angir configklasser for �yeblikket eksplisitt med @Import(ComponentsContext.class) i toppen av denne klassen,
        // men tanken er at dette skal gj�res med linjen under. Ta dette med de enkelte leverand�rene av
        // komponenter, og f� de til � angi config-klasser som fungerer.
        //
        // Mulig videre utvikling av SPI kan v�re � angi config for 2 ulike modi: integrasjon og mock/stub.

        // MergeSpringConfigFromComponents.merge(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    @Override
    public int getOrder() {
        return 0;
    }


}
