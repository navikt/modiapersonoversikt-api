package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.LamellHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.PriorityOrdered;

@Configuration
@Import(ComponentsContext.class)
public class ApplicationContext implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    @Bean
    public WicketApplication modiaApplication() {
        return new WicketApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public LamellHandler lamellHandler() {
        return new LamellHandler();
    }




    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        // Fungerer ikke for øyeblikket p.g.a. at komponenter tydeligvis angir Spring-config-klasser
        // som ikke fungerer i sine respektive ResourceReferences-implementasjoner. F.eks. dras det inn både
        // *Config og *TestConfig klasser, virker rart.
        // Vi angir configklasser for øyeblikket eksplisitt med @Import(ComponentsContext.class) i toppen av denne klassen,
        // men tanken er at dette skal gjøres med linjen under. Ta dette med de enkelte leverandørene av
        // komponenter, og få de til å angi config-klasser som fungerer.
        //
        // Mulig videre utvikling av SPI kan være å angi config for 2 ulike modi: integrasjon og mock/stub.

        // MergeSpringConfigFromComponents.merge(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public int getOrder() {
        return 0;
    }


}
