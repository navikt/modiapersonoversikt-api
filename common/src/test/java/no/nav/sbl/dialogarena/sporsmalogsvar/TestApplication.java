package no.nav.sbl.dialogarena.sporsmalogsvar;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.DummyHomePage;

public class TestApplication extends WebApplication {

    @Override
    protected void init() {
        getMarkupSettings().setStripWicketTags(true);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return DummyHomePage.class;
    }

}