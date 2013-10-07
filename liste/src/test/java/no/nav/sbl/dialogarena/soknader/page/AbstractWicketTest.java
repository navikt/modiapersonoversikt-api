package no.nav.sbl.dialogarena.soknader.page;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.soknader.SoknaderTestApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Before;
import org.mockito.Mockito;

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

    protected <T> T mock(Class<T> clazz) {
        T mock = Mockito.mock(clazz);
        applicationContext.putBean(mock);
        return mock;
    }

    protected <T> T mock(String beanName, Class<T> clazz) {
        T mock = Mockito.mock(clazz);
        applicationContext.putBean(beanName, mock);
        return mock;
    }

    protected <T> void bean(String beanName, T bean) {
        applicationContext.putBean(beanName, bean);
    }

    protected abstract void setup();

}
