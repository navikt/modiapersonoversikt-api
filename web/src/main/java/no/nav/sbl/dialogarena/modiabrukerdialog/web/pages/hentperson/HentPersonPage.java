package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.LamellHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.ModiaModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSide;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.ModiaModalWindow.getJavascriptSaveButtonFocus;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;

import javax.inject.Inject;

public class HentPersonPage extends BasePage {
    @Inject
    private LamellHandler lamellHandler;

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
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("fnr", query);
        pageParameters.set("begrunnelse", true);
        throw new RestartResponseException(Intern.class,pageParameters);
    }


}
