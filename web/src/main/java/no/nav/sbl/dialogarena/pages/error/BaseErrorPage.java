package no.nav.sbl.dialogarena.pages.error;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.http.WebResponse;


public abstract class BaseErrorPage extends WebPage {

    public BaseErrorPage() {
        FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        add(feedbackPanel);
    }

    protected abstract int getErrorCode();

    @Override
    protected void configureResponse(final WebResponse response) {
        super.configureResponse(response);
        response.setStatus(getErrorCode());
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }
}
