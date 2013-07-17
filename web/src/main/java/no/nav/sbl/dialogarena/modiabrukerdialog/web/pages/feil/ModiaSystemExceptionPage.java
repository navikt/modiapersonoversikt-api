package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.WebResponse;

import static java.util.Arrays.asList;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static no.nav.modig.wicket.errorhandling.listeners.ModigExceptionListener.EXCEPTION_UID_KEY;
import static no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM.Icon.UTROPSTEGN;

public class ModiaSystemExceptionPage extends AbstractErrorPage {

    public ModiaSystemExceptionPage() {
        add(
                createDefaultErrorPanel(createErrorPanelVM(getErrorCode().getObject(), Session.get().getMetaData(EXCEPTION_UID_KEY)))
        );
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    private ModiaDefaultErrorPanel createDefaultErrorPanel(ErrorPanelVM errorPanelVM) {
        return new ModiaDefaultErrorPanel("errorpanel", new Model<>(errorPanelVM));
    }

    private StringResourceModel getErrorCode() {
        return new StringResourceModel("system.error.code.message", this, null, SC_INTERNAL_SERVER_ERROR);
    }

    private ErrorPanelVM createErrorPanelVM(String errorCode, String exceptionUniqueId) {
        return new ErrorPanelVM(UTROPSTEGN, asList(errorCode, exceptionUniqueId));
    }

}
