package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.pagelet.spi.utils.SPIResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;

public final class MergeSpringConfigFromComponents {

    private static final Logger LOG = LoggerFactory.getLogger(MergeSpringConfigFromComponents.class);

    public static void merge(BeanDefinitionRegistry registry) {
        Class<?>[] configClasses = SPIResources.getSpringConfiguration();
        for (Class<?> configuration : configClasses) {
            LOG.info("Registering " + configuration);
        }
        new AnnotatedBeanDefinitionReader(registry).register(configClasses);
    }

    private MergeSpringConfigFromComponents() { }

}
