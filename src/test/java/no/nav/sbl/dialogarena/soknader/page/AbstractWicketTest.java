package no.nav.sbl.dialogarena.soknader.page;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.soknader.SoknaderTestApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Before;

public abstract class AbstractWicketTest {

    protected ApplicationContextMock applicationContext;
    protected FluentWicketTester<SoknaderTestApplication> wicketTester;

    @Before
    public void before() {
        applicationContext = new ApplicationContextMock();
        SoknaderTestApplication wicketApplication = new SoknaderTestApplication() {
            @Override
            protected void setupSpringInjector() {
                getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
            }
        };
        wicketTester = new FluentWicketTester<>(wicketApplication);
        setup();
    }

    protected abstract void setup();

}
