package no.nav.sbl.dialogarena.sak;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import no.nav.sbl.dialogarena.sak.service.SakOgBehandlingFilter;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Locale;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class WicketTesterConfig {

    @Inject
    private ApplicationContext applicationContext;

    @Bean
    public FluentWicketTester<? extends WebApplication> wicketTester() {
        DummyApplication dummyApplication = new DummyApplication(applicationContext) {
            @Override
            protected void init() {
                super.init();
                getResourceSettings().getStringResourceLoaders().add(0, new IStringResourceLoader() {
                    @Override public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
                        return "Mock-tekst fra CMS";
                    }

                    @Override public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
                        return "Mock-tekst fra CMS";
                    }
                });
            }
        };
        return new FluentWicketTester<>(dummyApplication);
    }

    @Bean
    public SaksoversiktService saksoversiktService() {
        return mock(SaksoversiktService.class, RETURNS_MOCKS);
    }

    @Bean
    public AktoerPortType aktoerPortType() {
        return mock(AktoerPortType.class, RETURNS_MOCKS);
    }

    @Bean
    public SakOgBehandling_v1PortType sakOgBehandlingPortType() {
        return mock(SakOgBehandling_v1PortType.class, RETURNS_MOCKS);
    }

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        return mock(HenvendelseSoknaderPortType.class, RETURNS_MOCKS);
    }

    @Bean
    public BulletproofCmsService bulletproofCmsService() {
        return new BulletproofCmsService();
    }

    @Bean
    public SakOgBehandlingFilter sakOgBehandlingFilter() {
        SakOgBehandlingFilter mock = mock(SakOgBehandlingFilter.class, RETURNS_MOCKS);
        when(mock.filtrerSaker(anyListOf(WSSak.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0]; // Filtrerer ingenting og returnerer argumentet
            }
        });
        return mock;
    }

    public class DummyApplication extends WebApplication {

        public DummyApplication(ApplicationContext applicationContext) {
            getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
        }

        @Override
        public Class<? extends Page> getHomePage() {
            return new Page() {
            }.getClass();
        }
    }
}
