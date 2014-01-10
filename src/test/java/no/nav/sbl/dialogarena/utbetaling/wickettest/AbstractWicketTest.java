package no.nav.sbl.dialogarena.utbetaling.wickettest;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.utbetaling.UtbetalingApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Before;

public abstract class AbstractWicketTest {

    protected ApplicationContextMock applicationContext;
    protected FluentWicketTester<UtbetalingApplication> wicketTester;

    @Before
    public void before() {
        applicationContext = new ApplicationContextMock();
        UtbetalingApplication wicketApplication = new UtbetalingApplication() {
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
