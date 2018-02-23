package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.consumer.fim.person.to.RecoverableAuthorizationException;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.modig.modia.constants.ModiaConstants;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.begrunnelse.ReactBegrunnelseModal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback.HodeCallbackWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback.SokOppBrukerCallback;
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.URL_TIL_SESSION_PARAMETERE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.wicket.markup.head.OnDomReadyHeaderItem.forScript;

public class Hode extends WebMarkupContainer {
    private Logger logger = LoggerFactory.getLogger(Hode.class);
    public static final PackageResourceReference LESS = new PackageResourceReference(Hode.class, "hode.less");
    private static final ResourceReference JS = new JavaScriptResourceReference(Hode.class, "hode.js");
    private static ObjectMapper mapper;

    private static final transient Map<String, HodeCallbackWrapper> callbacks = new HashMap<>();
    private GrunnInfo grunnInfo;
    private ReactBegrunnelseModal modal;
    private PersonKjerneinfoServiceBi personService;

    public Hode(String id, ReactBegrunnelseModal modal, PersonKjerneinfoServiceBi personService) {
        this(id, modal, personService, new GrunnInfo(null, null), "");
    }

    public Hode(String id, ReactBegrunnelseModal modal, PersonKjerneinfoServiceBi personService, String byttTilFnr) {
        this(id, modal, personService, new GrunnInfo(new GrunnInfo.Bruker(byttTilFnr), null), byttTilFnr, "");
    }

    public Hode(String id, ReactBegrunnelseModal modal, PersonKjerneinfoServiceBi personService, GrunnInfo grunnInfo, String byttTilFnr) {
        this(id, modal, personService, grunnInfo, byttTilFnr, "");
    }

    public Hode(String id, ReactBegrunnelseModal modal, PersonKjerneinfoServiceBi personService, GrunnInfo grunnInfo, String byttTilFnr, String feilmelding) {
        super(id);
        this.grunnInfo = grunnInfo;
        this.modal = modal;
        this.personService = personService;

        setOutputMarkupId(true);
        add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                WebRequest request = ((WebRequest) RequestCycle.get().getRequest());
                Set<String> paramnames = request.getQueryParameters().getParameterNames();

                callbacks.forEach((key, value) -> {
                    if (paramnames.contains(key)) {
                        String data = request.getQueryParameters().getParameterValue(key).toString();
                        if (data == null || data.isEmpty() || value.type == Void.class) {
                            value.callback.onCallback(target, Hode.this,null);
                        } else {
                            value.callback.onCallback(target, Hode.this, deserialize(data, value.type));
                        }
                    }
                });
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                boolean autoSubmit = byttTilFnr != null && byttTilFnr.length() > 0;
                response.render(JavaScriptContentHeaderItem.forReference(JS));
                response.render(forScript(lagHodeInitScript(grunnInfo, getCallbackUrl(), getMarkupId(), autoSubmit, feilmelding)));
                super.renderHead(component, response);
            }
        });

        addCallback("sokperson", new HodeCallbackWrapper<>(String.class, new SokOppBrukerCallback(personService, this)));
    }

    public void addCallback(String event, HodeCallbackWrapper<?> callback) {
        callbacks.put(event, callback);
    }

    public String getUpdateScript(String feilmelding) {
        return String.format("updateHode('%s', '%s');", getMarkupId(), feilmelding);
    }

    private static String lagHodeInitScript(GrunnInfo grunnInfo, CharSequence callbackUrl, String markupId, boolean autoSubmit, String feilmelding) {
        String fnr = Optional
                .ofNullable(grunnInfo)
                .map((gr) -> gr.bruker)
                .map((gr) -> gr.fnr)
                .filter((fdato) -> fdato.length() > 0)
                .orElse("");

        return String.format("initHode('%s', '%s', '%s', %s, '%s')", callbackUrl, markupId, fnr, autoSubmit, feilmelding);
    }

    private static void ensureMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
    }

    private static String serialize(Object obj) {
        StringWriter sw = new StringWriter();
        try {
            ensureMapper();
            mapper.writeValue(sw, obj);
        } catch (IOException e) {
            return "";
        }
        return sw.toString();
    }


    private static <T> T deserialize(String string, Class<T> type) {
        try {
            ensureMapper();
            return mapper.readValue(string.getBytes(), type);
        } catch (IOException e) {
            return null;
        }
    }

    @RunOnEvents(InternalEvents.FNR_CHANGED)
    public void externalSubmit(AjaxRequestTarget target, String query) {
        try {
            handlePerson(target, this, query);
        } catch (AuthorizationException ex) {
            if (ex.getMessage().contains("sikkerhetsbegrensning.diskresjon")) {
                send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(InternalEvents.HENTPERSON_FODSELSNUMMER_IKKE_TILGANG, query));
            }
        }
    }

    public void handlePerson(AjaxRequestTarget target, Hode component, String fnr) {
        Person person;
        try {
            person = getPersonKjerneinfo(fnr, component);

            if (person != null && person.getFodselsnummer() != null) {
                component.send(component.getPage(), Broadcast.DEPTH, new NamedEventPayload(InternalEvents.FODSELSNUMMER_FUNNET, lagNyePageParametere(component.getPage().getPageParameters(), fnr)));
            }
        } catch (RecoverableAuthorizationException ex) {
            logger.info("RecoverableAuthorizationException ved kall på getPersonKjerneinfo", ex.getMessage());
            component.getSession().setAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET, "");
            modal.show(target, fnr);
        } catch (ApplicationException ex) {
            logger.error("ApplicationException ved kall på getPersonKjerneinfo", ex.getMessage());
            target.appendJavaScript(component.getUpdateScript(getFeilmelding(component, ex)));
        }
    }

    private String getFeilmelding(Hode component, ApplicationException ex) {
        try {
            return component.getString(ex.getId());
        } catch (MissingResourceException exception) {
            return ex.getMessage();
        }
    }

    private Person getPersonKjerneinfo(String fnr, Component component) {
        if (fnr == null) {
            throw new IllegalStateException("Kan ikke hente kjerneinfo uten FNR");
        }
        String fodselsnummer = fnr.replaceAll("[^\\d]", "");
        logger.info("Henter informasjon om person med fodselsnummer {}", fodselsnummer);
        HentKjerneinformasjonRequest request = new HentKjerneinformasjonRequest(fodselsnummer);
        String fnrBegrunnet = (String) component.getSession().getAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET);
        Boolean erBegrunnet = !isBlank(fnrBegrunnet) && fnrBegrunnet.equals(fnr);
        request.setBegrunnet((erBegrunnet == null) ? false : erBegrunnet);
        HentKjerneinformasjonResponse response = personService.hentKjerneinformasjon(request);
        return response != null ? response.getPerson() : null;
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

    private void sendGotoHentPersonPageEvent(Component component, String errorText, Sikkerhetstiltak sikkerhetstiltak, String fnr) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(SokOppBrukerCallback.JSON_ERROR_TEXT, errorText);
            payload.put(SokOppBrukerCallback.JSON_SOKT_FNR, fnr);
            if (sikkerhetstiltak != null) {
                payload.put(SokOppBrukerCallback.JSON_SIKKERHETTILTAKS_BESKRIVELSE, sikkerhetstiltak.getSikkerhetstiltaksbeskrivelse());
            }
        } catch (JSONException exp) {
            throw new ApplicationException("JSONException", exp, "Klarer ikke å opprette JSON-object riktig");
        }
        component.send(component.getPage(), Broadcast.DEPTH, new NamedEventPayload(InternalEvents.GOTO_HENT_PERSONPAGE, payload.toString()));
    }
}
