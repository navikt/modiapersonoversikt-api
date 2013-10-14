package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import org.apache.wicket.Session;
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
        assertThat("nb", equalTo(createSession().getLocale().getLanguage()));
    }

    private Session createSession() {
        return application.newSession(new MockWebRequest(new Url()), new MockWebResponse());
    }

}
