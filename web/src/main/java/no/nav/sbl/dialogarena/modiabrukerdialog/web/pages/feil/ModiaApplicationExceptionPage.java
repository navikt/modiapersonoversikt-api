package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.listeners.ModigExceptionListener;
import no.nav.modig.wicket.errorhandling.panels.DefaultErrorPanel;
import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;

public class ModiaApplicationExceptionPage extends AbstractErrorPage {

    private static final String DEFAULT_ERROR_MESSAGE = "En feil har oppstått ";

    private final ModiaDefaultErrorPanel errorpanel;

    public ModiaApplicationExceptionPage() {

        final String exceptionUniqueId = Session.get().getMetaData(ModigExceptionListener.EXCEPTION_UID_KEY);
        final String internasjonaliseringsnokkel = Session.get().getMetaData(ModigExceptionListener.EXCEPTION_MESSAGE);
        IModel<String> exceptionModel = internasjonaliseringsnokkel == null ? new Model<>("") : new StringResourceModel(internasjonaliseringsnokkel, null, DEFAULT_ERROR_MESSAGE);

        StringResourceModel errorCode = new StringResourceModel("errorpanel.errorcode", this, null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        ErrorPanelVM errorPanelVM = new ErrorPanelVM(ErrorPanelVM.Icon.UTROPSTEGN, exceptionModel.getObject(), "",
                asList(errorCode.getString(), exceptionUniqueId));

        errorpanel = new ModiaDefaultErrorPanel("errorpanel", new Model<>(errorPanelVM));
        add(
                errorpanel
        );
    }
}
