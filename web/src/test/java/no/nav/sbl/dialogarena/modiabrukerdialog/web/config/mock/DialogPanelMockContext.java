package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils.WicketInjectablePropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import(value = {ConsumerServicesMockContext.class, JacksonMockContext.class})
public class DialogPanelMockContext {

    @Bean
    public CmsContentRetriever cmsContentRetriever() {
        return mock(CmsContentRetriever.class);
    }

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return mock(PersonKjerneinfoServiceBi.class);
    }

    @Bean
    public WicketInjectablePropertyResolver wicketInjectablePropertyResolver() {
        WicketInjectablePropertyResolver mock = mock(WicketInjectablePropertyResolver.class);
        when(mock.getProperty(anyString())).thenReturn("Property");
        return mock;
    }

    @Bean
    public PropertyResolver propertyResolver() {
        return mock(PropertyResolver.class);
    }

    @Bean
    public UnleashService unleashService() { return mock(UnleashService.class); }
}
