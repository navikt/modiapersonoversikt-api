package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;


public class ModiaModalWindow extends ModigModalWindow {

    private Class clazz = HentPersonPage.class;
    private PageParameters pageParameters = new PageParameters();

    public ModiaModalWindow(String id) {
        super(id);
    }

    public void setRedirectClass(Class clazz) {
        this.clazz = clazz;
    }

    public void setPageParameters(PageParameters pageParameters) {
        this.pageParameters = pageParameters;
    }

    public void redirect() {
        throw new RestartResponseException(clazz, pageParameters);
    }

    public static String getJavascriptSaveButtonFocus() {
        return "$('#lagreButton').get(0).focus();";
    }

}

