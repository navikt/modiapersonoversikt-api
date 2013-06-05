package no.nav.sbl.dialogarena.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.pages.HomePage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HentPersonPage extends WebPage {

    public HentPersonPage() {

        add(new HentPersonPanel("searchPanel"));
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @RunOnEvents(no.nav.kjerneinfo.eventpayload.HentPerson.FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
        throw new RestartResponseException(
                HomePage.class,
                new PageParameters().set("fnr", query)
        );
    }
}
