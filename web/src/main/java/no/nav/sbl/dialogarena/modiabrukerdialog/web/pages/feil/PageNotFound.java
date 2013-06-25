package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.panels.DefaultErrorPanel;
import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;

public class PageNotFound extends AbstractErrorPage {
    public PageNotFound() {
        StringResourceModel errorCode = new StringResourceModel("errorpanel.errorcode", this, null, HttpServletResponse.SC_NOT_FOUND);
        
        ErrorPanelVM errorPanelVM = new ErrorPanelVM(ErrorPanelVM.Icon.UTROPSTEGN, asList(errorCode.getObject()));
        DefaultErrorPanel exceptionPanel = new DefaultErrorPanel("errorPanel", new Model<>(errorPanelVM)); 
        add(exceptionPanel);
    }
    
    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
