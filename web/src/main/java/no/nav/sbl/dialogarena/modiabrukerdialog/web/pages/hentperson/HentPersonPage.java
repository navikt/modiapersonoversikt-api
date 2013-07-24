package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.modig.modia.constants.ModiaConstants;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HentPersonPage extends BasePage {

	public HentPersonPage() {

        add(
				new HentPersonPanel("searchPanel"),
				new PersonsokPanel("personsokPanel").setVisible(true)
        );
    }

	@Override
	public boolean isVersioned() {
		return false;
	}

	@RunOnEvents(InternalEvents.FODSELSNUMMER_FUNNET)
	public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
        throw new RestartResponseException(Intern.class, new PageParameters().set("fnr", query));
	}

    @RunOnEvents(InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, String query) {
		getSession().setAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET, true);
	    refreshKjerneinfo(target, query);
    }

}
