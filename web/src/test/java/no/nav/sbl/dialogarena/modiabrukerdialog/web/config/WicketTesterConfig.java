package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PlukkOppgavePepMockContext;
import no.nav.sbl.modiabrukerdialog.pep.config.spring.PepTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import({
        WicketApplicationTestContext.class,
        KjerneinfoPepMockContext.class,
		PepTestConfig.class,
        PlukkOppgavePepMockContext.class
})
public class WicketTesterConfig {

    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester<WicketApplication> fluentWicketTester() {
        return new FluentWicketTester<>(application);
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() {
        CmsContentRetriever mock = mock(CmsContentRetriever.class);
        when(mock.hentTekst(anyString())).thenReturn("Tekst fra mock-cms");
        return mock;
    }

}
