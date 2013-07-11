package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.listeners.ModigExceptionListener;
import no.nav.modig.wicket.errorhandling.panels.DefaultErrorPanel;
import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;

public class ModiaSystemExceptionPage extends AbstractErrorPage {

    private final ModiaDefaultErrorPanel errorpanel;

    public ModiaSystemExceptionPage() {

        final String exceptionUniqueId = Session.get().getMetaData(ModigExceptionListener.EXCEPTION_UID_KEY);

        StringResourceModel errorCode = new StringResourceModel("system.error.code.message", this, null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ErrorPanelVM errorPanelVM = new ErrorPanelVM(ErrorPanelVM.Icon.UTROPSTEGN,
                asList(errorCode.getString(), exceptionUniqueId));

        errorpanel = new ModiaDefaultErrorPanel("errorpanel", new Model<>(errorPanelVM));

        add(
                errorpanel
        );
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
