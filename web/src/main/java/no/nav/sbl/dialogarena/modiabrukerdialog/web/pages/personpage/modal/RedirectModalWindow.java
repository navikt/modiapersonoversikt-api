package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class RedirectModalWindow extends ModigModalWindow {

    private Class<? extends Page> targetPage = HentPersonPage.class;
    private PageParameters params = new PageParameters();

    public RedirectModalWindow(String id) {
        super(id);
    }

    public void setTarget(Class<? extends Page> targetPage, PageParameters params) {
        this.targetPage = targetPage;
        this.params = params;
    }

    public void redirect() {
        throw new RestartResponseException(targetPage, params);
    }

    public static String getJavascriptSaveButtonFocus() {
        return "$('#lagreButton').get(0).focus();";
    }

}

