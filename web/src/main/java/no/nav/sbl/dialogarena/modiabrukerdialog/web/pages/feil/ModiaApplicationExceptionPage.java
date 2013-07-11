package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.errorhandling.listeners.ModigExceptionListener.EXCEPTION_MESSAGE;
import static no.nav.modig.wicket.errorhandling.listeners.ModigExceptionListener.EXCEPTION_UID_KEY;
import static no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM.Icon.UTROPSTEGN;

public class ModiaApplicationExceptionPage extends AbstractErrorPage {

    private static final String DEFAULT_ERROR_MESSAGE = "En feil har oppstått ";

    public ModiaApplicationExceptionPage() {
        add(
                createDefaultErrorPanel(createErrorPanelVM(createExceptionString(Session.get().getMetaData(EXCEPTION_MESSAGE)), createErrorCodeString(), Session.get().getMetaData(EXCEPTION_UID_KEY)))
        );
    }

    private ModiaDefaultErrorPanel createDefaultErrorPanel(ErrorPanelVM errorPanelVM) {
        return new ModiaDefaultErrorPanel("errorpanel", new Model<>(errorPanelVM));
    }

    private ErrorPanelVM createErrorPanelVM(String exceptionString, String errorCode, String exceptionUniqueId) {
        return new ErrorPanelVM(UTROPSTEGN, exceptionString, "", asList(errorCode, exceptionUniqueId));
    }

    private String createErrorCodeString() {
        return new StringResourceModel("errorpanel.errorcode", this, null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR).toString();
    }

    private String createExceptionString(String internasjonaliseringsnokkel) {
        return internasjonaliseringsnokkel == null ? "" : new StringResourceModel(internasjonaliseringsnokkel, null, DEFAULT_ERROR_MESSAGE).getObject();
    }
}
