package no.nav.sbl.dialogarena.sak;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Before;
import org.springframework.context.ApplicationContext;

public abstract class AbstractWicketTest {

    protected ApplicationContextMock applicationContext;
    protected FluentWicketTester<DummyApplication> wicketTester;

    @Before
    public void before() {
        applicationContext = new ApplicationContextMock();
        wicketTester = new FluentWicketTester<>(new DummyApplication(applicationContext));
        setup();
    }

    protected abstract void setup();

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
