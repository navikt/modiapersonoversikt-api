package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.listeners.ModigExceptionListener;
import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;

public class ModiaSystemExceptionPage extends AbstractErrorPage {


    public ModiaSystemExceptionPage() {
        final String exceptionUniqueId = Session.get().getMetaData(ModigExceptionListener.EXCEPTION_UID_KEY);
        StringResourceModel errorCode = getErrorCode();

        add(
                createDefaultErrorPanel(createErrorPanelVM(errorCode.toString(), exceptionUniqueId))
        );
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private ModiaDefaultErrorPanel createDefaultErrorPanel(ErrorPanelVM errorPanelVM) {
        return new ModiaDefaultErrorPanel("errorpanel", new Model<>(errorPanelVM));
    }

    private StringResourceModel getErrorCode() {
        return new StringResourceModel("system.error.code.message", this, null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private ErrorPanelVM createErrorPanelVM(String errorCode, String exceptionUniqueId) {
        return new ErrorPanelVM(ErrorPanelVM.Icon.UTROPSTEGN, asList(errorCode.toString(), exceptionUniqueId));
    }

}
