package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.modig.modia.constants.ModiaConstants;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personinfo.PersonPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import static no.nav.modig.modia.events.InternalEvents.FNR_CHANGED;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_IKKE_TILGANG;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.event.Broadcast.DEPTH;
import static org.apache.wicket.markup.head.OnLoadHeaderItem.forScript;

public class HentPersonPage extends BasePage {

    public HentPersonPage(PageParameters pageParameters) {
        HentPersonPanel hentPersonPanel = new HentPersonPanel("searchPanel");
        setupErrorText(pageParameters, hentPersonPanel);
        add(
                new ContextImage("modia-logo", "img/modiaLogo.svg"),
                hentPersonPanel,
                new PersonsokPanel("personsokPanel").setVisible(true)
        );
    }

    private void setupErrorText(PageParameters pageParameters, HentPersonPanel hentPersonPanel) {
        StringValue errorText = pageParameters.get("error");
        if (!errorText.isEmpty()) {
            hentPersonPanel.setErrorText(errorText.toString());
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(forScript("jQuery('#foedselsnummerInput').focus()"));
    }

    public boolean isVersioned() {
        return false;
    }

    @RunOnEvents(InternalEvents.FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
        throw new RestartResponseException(PersonPage.class, new PageParameters().set("fnr", query));
    }

    @RunOnEvents(InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, String query) {
        getSession().setAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET, true);
        refreshKjerneinfo(target, query);
    }

    @RunOnEvents(InternalEvents.PERSONSOK_FNR_CLICKED)
    public void personsokresultatClicked(AjaxRequestTarget target, String query) {
        send(getPage(), DEPTH, new NamedEventPayload(FNR_CHANGED, query));
    }

    @RunOnEvents(InternalEvents.HENTPERSON_FODSELSNUMMER_IKKE_TILGANG)
    public void personsokIkkeTilgang(AjaxRequestTarget target, String query) {
        send(getPage(), BREADTH, new NamedEventPayload(FODSELSNUMMER_IKKE_TILGANG, query));
    }

}
