package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.panels.DefaultErrorPanel;
import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.WebResponse;

import static java.util.Arrays.asList;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM.Icon.UTROPSTEGN;

public class PageNotFound extends AbstractErrorPage {

    public PageNotFound() {
        add(createErrorPanel());
    }

    private DefaultErrorPanel createErrorPanel() {
        return new DefaultErrorPanel("errorPanel", createErrorPanelModel());
    }

    private Model<ErrorPanelVM> createErrorPanelModel() {
        return new Model<>(new ErrorPanelVM(UTROPSTEGN, asList(getStringResourceModel())));
    }

    private String getStringResourceModel() {
        return new StringResourceModel("errorpanel.errorcode", this, null, SC_NOT_FOUND).getObject();
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(SC_NOT_FOUND);
    }
}
