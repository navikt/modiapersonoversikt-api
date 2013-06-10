package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;

public class HentPersonPage extends BasePage {

    public HentPersonPage() {
        //        add(new HentPersonPanel("searchPanel"));
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
