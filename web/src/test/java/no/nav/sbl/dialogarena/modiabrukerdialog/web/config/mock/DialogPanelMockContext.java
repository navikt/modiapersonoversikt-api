package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.CmsContentRetriever;
import org.springframework.context.annotation.*;

import static org.mockito.Mockito.mock;

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
}
