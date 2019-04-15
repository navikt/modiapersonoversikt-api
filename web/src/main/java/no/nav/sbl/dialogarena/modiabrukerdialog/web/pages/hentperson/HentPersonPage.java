package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.sikkerhetstiltak.SikkerhetstiltakPersonPanel;
import no.nav.modig.modia.constants.ModiaConstants;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.Hode;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.begrunnelse.ReactBegrunnelseModal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback.SokOppBrukerCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tempnaisgosys.GosysNaisLenke;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.URL_TIL_SESSION_PARAMETERE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.begrunnelse.ReactBegrunnelseModal.CONFIRM;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.begrunnelse.ReactBegrunnelseModal.DISCARD;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.event.Broadcast.DEPTH;
import static org.apache.wicket.markup.head.OnLoadHeaderItem.forScript;

public class HentPersonPage extends BasePage {
    private static final Logger PERSON_ACCESS_LOGGER = LoggerFactory.getLogger("personaccess");
    public static final String SIKKERHETSTILTAK = "sikkerhetstiltak";
    public static final String ERROR = "error";
    public static final String FNR = "fnr";
    public static final String SOKT_FNR = "soektfnr";

    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    private final ReactBegrunnelseModal oppgiBegrunnelseModal;

    @Inject
    private UnleashService unleashService;

    public HentPersonPage(PageParameters pageParameters) {
        super(pageParameters);

        boolean naisGosysLenke = unleashService.isEnabled(Feature.NAIS_GOSYS_LENKE);

        oppgiBegrunnelseModal = new ReactBegrunnelseModal("oppgiBegrunnelseModal");
        String urlFnr = Optional.ofNullable(pageParameters.get(FNR).toString()).orElse(null);

        Hode hode = new Hode("hode", oppgiBegrunnelseModal, personKjerneinfoServiceBi, urlFnr);
        add(
                hode,
                new PlukkOppgavePanel("plukkOppgaver"),
                new PersonsokPanel("personsokPanel").setVisible(true),
                oppgiBegrunnelseModal
        );

        if (naisGosysLenke) {
            add(new GosysNaisLenke("gosysNaisLenke"));
        } else {
            add(new EmptyPanel("gosysNaisLenke"));
        }

        setUpSikkerhetstiltakspanel(pageParameters);
        configureModalWindow(oppgiBegrunnelseModal, pageParameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(forScript("jQuery('#js-deokorator-sokefelt').focus();"));
    }

    public static void configureModalWindow(ReactBegrunnelseModal oppgiBegrunnelseModal, final PageParameters pageParameters) {
        oppgiBegrunnelseModal.addCallback(DISCARD, Void.class, new ReactComponentCallback<Void>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Void data) {
                oppgiBegrunnelseModal.hide(target);
            }
        });
        oppgiBegrunnelseModal.addCallback(CONFIRM, Map.class, new ReactComponentCallback<Map>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Map data) {
                String begrunnelse = ((String) data.get("begrunnelse"));
                String fnr = ((String) data.get("fnr"));

                SubjectHandler subjectHandler = SubjectHandler.getSubjectHandler();
                PERSON_ACCESS_LOGGER.warn("Bruker {} henter person med FNR/DNR {}. Begrunnelse: {}", subjectHandler.getUid(), fnr, begrunnelse);
                oppgiBegrunnelseModal.hide(target);
                oppgiBegrunnelseModal.getSession().setAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET, fnr);
                oppgiBegrunnelseModal.send(oppgiBegrunnelseModal.getPage(), Broadcast.DEPTH, new NamedEventPayload(InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE, lagNyePageParametere(pageParameters, fnr)));
            }
        });
    }

    private static PageParameters lagNyePageParametere(PageParameters pageParameters, String fnr) {
        PageParameters params = new PageParameters()
                .set("fnr", fnr);

        for (String urlParam : URL_TIL_SESSION_PARAMETERE) {
            String pageParam = pageParameters.get(urlParam).toString();
            if (!isBlank(pageParam)) {
                params.set(urlParam, pageParam);
            }
        }

        return params;
    }

    private void setUpSikkerhetstiltakspanel(PageParameters pageParameters) {
        StringValue sikkerhetstiltakBeskrivelse = pageParameters.get(SIKKERHETSTILTAK);
        if (!sikkerhetstiltakBeskrivelse.isEmpty()) {
            add(new SikkerhetstiltakPersonPanel(SIKKERHETSTILTAK, sikkerhetstiltakBeskrivelse.toString()));
        } else {
            add(new SikkerhetstiltakPersonPanel(SIKKERHETSTILTAK, ""));
        }
    }

    public boolean isVersioned() {
        return false;
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, PageParameters pageParameters) {
        throw new RestartResponseException(PersonPage.class, pageParameters);
    }

    @RunOnEvents(GOTO_HENT_PERSONPAGE)
    public void refreshKjerneinfoSikkerhetsInfo(AjaxRequestTarget target, String query) throws JSONException {
        String errorText = getTextFromPayload(query, SokOppBrukerCallback.JSON_ERROR_TEXT);
        String soktFnr = getTextFromPayload(query, SokOppBrukerCallback.JSON_SOKT_FNR);
        String sikkerhetstiltak = getTextFromPayload(query, SokOppBrukerCallback.JSON_SIKKERHETTILTAKS_BESKRIVELSE);

        PageParameters pageParameters = new PageParameters();
        if (!StringUtils.isEmpty(sikkerhetstiltak)) {
            pageParameters.set(ERROR, errorText).set(SIKKERHETSTILTAK, sikkerhetstiltak)
                    .set(SOKT_FNR, soktFnr);
        } else {
            pageParameters.set(ERROR, errorText).set(SOKT_FNR, soktFnr);
        }

        throw new RestartResponseException(HentPersonPage.class, pageParameters);
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, PageParameters pageParameters) {
        getSession().setAttribute(HENT_PERSON_BEGRUNNET, pageParameters.get("fnr").toString());
        refreshKjerneinfo(target, pageParameters);
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

	protected String getTextFromPayload(String query, String jsonField) throws JSONException {
		return getJsonField(query, jsonField);
	}

    private String getJsonField(String query, String field) throws JSONException {
        JSONObject jsonObject = new JSONObject(query);
        if (jsonObject.has(field)) {
            return new JSONObject(query).getString(field);
        } else {
            return null;
        }
    }
}
