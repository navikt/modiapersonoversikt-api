package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;

import static java.lang.System.currentTimeMillis;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

public class ModiaSystemExceptionPageTest {

    private ApplicationContextMock applicationContext;
    private FluentWicketTester<WicketApplication> wicketTester;

    @Before
    public void before() {
        applicationContext = createApplicationContext();
        wicketTester = createWicketTester();
    }

    @Test
    public void shouldRenderErrorPage() {
        wicketTester.goTo(ModiaSystemExceptionPage.class)
                .should().containComponent(withId("errorpanel").and(ofType(ModiaDefaultErrorPanel.class)))
                .should().containComponent(withId("goToLink"));
    }

    @Test
    public void shouldGoToHomepageFromErrorPageLink() {
        wicketTester.goTo(ModiaSystemExceptionPage.class)
                .click().link(withId("goToLink")).should().beOn(HentPersonPage.class);
    }

    private ApplicationContextMock createApplicationContext() {
        return new ApplicationContextMock() {

            @Override
            public String getApplicationName() {
                return "modiabrukerdialog";
            }

            @Override
            public long getStartupDate() {
                return currentTimeMillis();
            }

            @Override
            public Environment getEnvironment() {
                return null;
            }
        };
    }

    private FluentWicketTester<WicketApplication> createWicketTester() {
        return new FluentWicketTester<WicketApplication>(new WicketApplication() {
            @Override
            protected void setSpringComponentInjector() {
                getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
            }
        });
    }
}
