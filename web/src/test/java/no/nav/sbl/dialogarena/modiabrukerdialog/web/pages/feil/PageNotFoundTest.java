package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Test;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

public class PageNotFoundTest {

    private FluentWicketTester<? extends WebApplication> wicketTester;
    
    @Test
    public void shouldRenderNotFoundPage() {
        wicket().goTo(PageNotFound.class).should().containComponent(withId("errorPanel"));
        
    }
    protected FluentWicketTester<? extends WebApplication> wicket() {
        if (wicketTester == null) {
            wicketTester = new FluentWicketTester<WebApplication>(new WebApplication() {
                @Override
                public Class<? extends Page> getHomePage() {
                    return null;
                }
            });
        }
        return wicketTester;
    }
}
