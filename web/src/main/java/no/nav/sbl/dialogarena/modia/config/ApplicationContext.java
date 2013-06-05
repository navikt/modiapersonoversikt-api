package no.nav.sbl.dialogarena.modia.config;

import no.nav.modig.pagelet.spi.utils.SPIResources;
import no.nav.sbl.dialogarena.WicketApplication;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.PriorityOrdered;


@Configuration
//@Import({KjerneinfoConfig.class})
public class ApplicationContext implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanDefinitionRegistry);
        Class<?>[] springReferences = SPIResources.getSpringConfiguration();

        for (int i = 0; i < springReferences.length; i++) {
            reader.register(springReferences);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) {
    }

    /**
     * Returns priority of post processing. Must be higher than Must run before {@link org.springframework.context.annotation.ConfigurationClassParser}
     *
     * @return priority
     */
    @Override
    public int getOrder() {
        return 0;
    }

    @Bean
    public WicketApplication modiaApplication() {
        return new WicketApplication();
    }
}
