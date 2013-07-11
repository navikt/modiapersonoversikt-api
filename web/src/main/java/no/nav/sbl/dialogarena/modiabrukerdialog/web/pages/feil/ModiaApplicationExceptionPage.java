package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.listeners.ModigExceptionListener;
import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;

public class ModiaApplicationExceptionPage extends AbstractErrorPage {

    private static final String DEFAULT_ERROR_MESSAGE = "En feil har oppstått ";

    public ModiaApplicationExceptionPage() {

        final String exceptionUniqueId = Session.get().getMetaData(ModigExceptionListener.EXCEPTION_UID_KEY);
        final String internasjonaliseringsnokkel = Session.get().getMetaData(ModigExceptionListener.EXCEPTION_MESSAGE);
        IModel<String> exceptionModel = createExceptionModel(internasjonaliseringsnokkel);
        StringResourceModel errorCode = createErrorCodeResourceModel();

        add(
                createDefaultErrorPanel(createErrorPanelVM(exceptionModel, errorCode.toString(), exceptionUniqueId))
        );
    }

    private ModiaDefaultErrorPanel createDefaultErrorPanel(ErrorPanelVM errorPanelVM) {
        return new ModiaDefaultErrorPanel("errorpanel", new Model<>(errorPanelVM));
    }

    private ErrorPanelVM createErrorPanelVM(IModel<String> exceptionModel, String errorCode, String exceptionUniqueId) {
        return new ErrorPanelVM(ErrorPanelVM.Icon.UTROPSTEGN, exceptionModel.getObject(), "", asList(errorCode, exceptionUniqueId));
    }

    private StringResourceModel createErrorCodeResourceModel() {
        return new StringResourceModel("errorpanel.errorcode", this, null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private IModel<String> createExceptionModel(String internasjonaliseringsnokkel) {
        return internasjonaliseringsnokkel == null ? new Model<>("") : new StringResourceModel(internasjonaliseringsnokkel, null, DEFAULT_ERROR_MESSAGE);
    }
}
