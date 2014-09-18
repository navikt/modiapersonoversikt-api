package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.sikkerhetstiltak.SikkerhetstiltakPersonPanel;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.FNR_CHANGED;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.HENTPERSON_CLEAR_MESSAGES;
import static no.nav.modig.modia.events.InternalEvents.HENTPERSON_FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.PERSONSOK_FNR_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.PERSONSOK_SEARCH_PERFORMED;
import static no.nav.modig.modia.events.InternalEvents.GOTO_HENT_PERSONPAGE;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.event.Broadcast.DEPTH;
import static org.apache.wicket.markup.head.OnLoadHeaderItem.forScript;

public class HentPersonPage extends BasePage {

	public static final String SIKKERHETSTILTAK = "sikkerhetstiltak";
	public static final String FNR = "fnr";
	public static final String ERROR = "error";

	public HentPersonPage(PageParameters pageParameters) {
        HentPersonPanel hentPersonPanel = new HentPersonPanel("searchPanel");
        setupErrorText(pageParameters, hentPersonPanel);
        add(
                new SaksbehandlerInnstillingerPanel("saksbehandlerInnstillingerPanel"),
                new SaksbehandlerInnstillingerTogglerPanel("saksbehandlerInnstillingerToggler"),
                hentPersonPanel,
                new PlukkOppgavePanel("plukkOppgave"),
                new PersonsokPanel("personsokPanel").setVisible(true)
        );

		setUpSikkerhetstiltakspanel(pageParameters);
    }

	private void setUpSikkerhetstiltakspanel(PageParameters pageParameters) {
		StringValue fnr = pageParameters.get(FNR);

		StringValue sikkerhetstiltakBeskrivelse = pageParameters.get(SIKKERHETSTILTAK);
		if (!sikkerhetstiltakBeskrivelse.isEmpty()) {
			add(new SikkerhetstiltakPersonPanel(SIKKERHETSTILTAK, sikkerhetstiltakBeskrivelse.toString()));
		} else {
			add(new SikkerhetstiltakPersonPanel(SIKKERHETSTILTAK, new String()));
		}
	}

	private void setupErrorText(PageParameters pageParameters, HentPersonPanel hentPersonPanel) {
        StringValue errorText = pageParameters.get(ERROR);
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

    @RunOnEvents(FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
        throw new RestartResponseException(PersonPage.class, new PageParameters().set("fnr", query));
    }

	@RunOnEvents(GOTO_HENT_PERSONPAGE)
	public void refreshKjerneinfoSikkerhetsInfo(AjaxRequestTarget target, String query) throws JSONException {
		String errorText = getErrorText(query);
		String sikkerhetstiltak = getSikkerhetsTiltakBeskrivelse(query);

		PageParameters pageParameters = new PageParameters();
		if (!StringUtils.isEmpty(sikkerhetstiltak)) {
			pageParameters.set(ERROR, errorText).set(SIKKERHETSTILTAK, sikkerhetstiltak);
		} else {
			pageParameters.set(ERROR, errorText);
		}

		throw new RestartResponseException(HentPersonPage.class, pageParameters);
	}

    @RunOnEvents(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, String query) {
        getSession().setAttribute(HENT_PERSON_BEGRUNNET, true);
        refreshKjerneinfo(target, query);
    }

    @RunOnEvents(PERSONSOK_FNR_CLICKED)
    public void personsokresultatClicked(AjaxRequestTarget target, String query) {
        send(getPage(), DEPTH, new NamedEventPayload(FNR_CHANGED, query));
    }

    @RunOnEvents(HENTPERSON_FODSELSNUMMER_IKKE_TILGANG)
    public void personsokIkkeTilgang(AjaxRequestTarget target, String query) {
        send(getPage(), BREADTH, new NamedEventPayload(FODSELSNUMMER_IKKE_TILGANG, query));
    }

    @RunOnEvents(PERSONSOK_SEARCH_PERFORMED)
    public void personsokUtfort(AjaxRequestTarget target, String query) {
        send(getPage(), DEPTH, new NamedEventPayload(HENTPERSON_CLEAR_MESSAGES, query));
    }

	protected String getSikkerhetsTiltakBeskrivelse(String query) throws JSONException {
		return getJsonField(query, HentPersonPanel.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
	}

	protected String getErrorText(String query) throws JSONException {
		return getJsonField(query, HentPersonPanel.JSON_ERROR_TEXT);
	}

	private String getJsonField(String query, String field) throws JSONException {
		JSONObject jsonObject =  new JSONObject(query);
		if (jsonObject.has(field)) {
			return new JSONObject(query).getString(field);
		} else {
			return null;
		}
	}
}
