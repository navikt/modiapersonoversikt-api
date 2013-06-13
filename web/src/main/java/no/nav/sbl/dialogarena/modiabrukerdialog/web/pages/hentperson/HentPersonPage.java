package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import org.apache.wicket.markup.html.basic.Label;

public class HentPersonPage extends BasePage {

    public HentPersonPage() {
        //        add(new HentPersonPanel("searchPanel"));
        add(new Label("searchPanel","Her skal søk komme"));
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    //    @RunOnEvents(no.nav.kjerneinfo.eventpayload.HentPerson.FODSELSNUMMER_FUNNET)
    //    public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
    //        throw new RestartResponseException(
    //                Intern.class,
    //                new PageParameters().set("fnr", query)
    //        );
    //    }
}
