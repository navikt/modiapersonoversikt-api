package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.modig.modia.constants.ModiaConstants;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public class HentPersonPage extends BasePage {


    public HentPersonPage(PageParameters pageParameters) {

        HentPersonPanel hentPersonPanel = new HentPersonPanel("searchPanel");
        StringValue errorText = pageParameters.get("error");
        if (!errorText.isEmpty()) {
            hentPersonPanel.setErrorText(errorText.toString());
        }
        add(
                new ContextImage("modia-logo", "img/modiaLogo.svg"),
                hentPersonPanel,
                new PersonsokPanel("personsokPanel").setVisible(true)
        );

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(OnLoadHeaderItem.forScript("jQuery('#foedselsnummerInput').focus()"));
    }

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

    @RunOnEvents(InternalEvents.PERSONSOK_FNR_CLICKED)
    public void personsokresultatClicked(AjaxRequestTarget target, String query) {
        send(getPage(), Broadcast.DEPTH, new NamedEventPayload(InternalEvents.FNR_CHANGED, query));
    }

    @RunOnEvents(InternalEvents.HENTPERSON_FODSELSNUMMER_IKKE_TILGANG)
    public void personsokIkkeTilgang(AjaxRequestTarget target, String query) {
        send(getPage(), Broadcast.BREADTH, new NamedEventPayload(InternalEvents.FODSELSNUMMER_IKKE_TILGANG, query));
    }

}
