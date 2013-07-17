package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.mock.MockWebResponse;
import org.apache.wicket.request.Url;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class WicketApplicationTest {
    
    private final WicketApplication application = new WicketApplication() {
        @Override
        protected void setSpringComponentInjector() {
            // ignore
        }
    };
    
    @Test
    public void newSessionShouldHaveNbLocale() throws Exception {
        MockWebRequest request = new MockWebRequest(new Url());
        MockWebResponse resonse  = new MockWebResponse();

        assertThat("nb", equalTo(application.newSession(request, resonse).getLocale().getLanguage()));
    }
}
