package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HentPersonPage extends BasePage {

    public HentPersonPage() {
        add(new HentPersonPanel("searchPanel"));
        //add(new Label("searchPanel","Her skal søk komme"));
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

        @RunOnEvents(HentPersonPanel.FODSELSNUMMER_FUNNET)
        public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
            throw new RestartResponseException(
                    Intern.class,
                    new PageParameters().set("fnr", query)
            );
        }
}
