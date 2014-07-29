package no.nav.sbl.dialogarena.sak;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

@ContextConfiguration(classes = WicketTesterConfig.class)
public abstract class AbstractWicketTest {

    @Inject
    protected FluentWicketTester<WebApplication> wicketTester;

    @Before
    public void before() {
        setup();
    }

    protected abstract void setup();

}
