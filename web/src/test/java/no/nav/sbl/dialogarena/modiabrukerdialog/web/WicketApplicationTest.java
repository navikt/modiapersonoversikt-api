package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import org.apache.wicket.Session;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.mock.MockWebResponse;
import org.apache.wicket.request.Url;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;


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
        
        Session session = application.newSession(request, resonse);
        
        Assert.assertThat("nb", equalTo(session.getLocale().getLanguage()));
    }
}
