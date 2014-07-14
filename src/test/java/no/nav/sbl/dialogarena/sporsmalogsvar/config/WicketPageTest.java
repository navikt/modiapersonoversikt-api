package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

@ContextConfiguration(classes = WicketTesterConfig.class)
public abstract class WicketPageTest {

    @Inject
    protected FluentWicketTester<?> wicket;

}
