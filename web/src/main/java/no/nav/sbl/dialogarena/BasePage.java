package no.nav.sbl.dialogarena;

import no.nav.sbl.dialogarena.pages.HomePage;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class BasePage extends WebPage {

    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(HomePage.class, "lokal.js");
    public static final CssResourceReference CSS_RESOURCE = new CssResourceReference(HomePage.class, "lokal.css");
    public static final CssResourceReference CSS_MODUS = new CssResourceReference(BasePage.class, "modus.css");

    private final WebMarkupContainer body;
    private FeedbackPanel feedback;

    public BasePage() {
        feedback = new FeedbackPanel("feedback");
        body = (WebMarkupContainer) new TransparentWebMarkupContainer("body").setOutputMarkupId(true);
        body.add(new DebugBar("debug"), feedback);
        add(body);
    }

    public WebMarkupContainer getBody() {
        return body;
    }

    public FeedbackPanel getFeedback() {
        return feedback;
    }
}
