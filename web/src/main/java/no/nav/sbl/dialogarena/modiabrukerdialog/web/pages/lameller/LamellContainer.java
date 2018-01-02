package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.brukerprofil.BrukerprofilPanel;
import no.nav.kjerneinfo.kontrakter.KontrakterPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.LamellPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.lamell.*;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt.OversiktLerret;
import no.nav.sbl.dialogarena.sak.lamell.SaksoversiktLerret;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.InnboksProps;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLerret;
import no.nav.sbl.dialogarena.varsel.lamell.VarselLerret;
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import no.nav.sykmeldingsperioder.foreldrepenger.ForeldrepengerPanel;
import no.nav.sykmeldingsperioder.pleiepenger.PleiepengerPanel;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.modia.lamell.DefaultLamellFactory.newLamellFactory;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.SessionParametere.SporsmalOgSvar.BESVARMODUS;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.*;

/**
 * Holder på lameller og tilhørende lerreter
 */
public class LamellContainer extends TokenLamellPanel implements Serializable {

    private Logger logger = LoggerFactory.getLogger(LamellContainer.class);

    public static final String LAMELL_KONTRAKTER = "kontrakter";
    public static final String LAMELL_UTBETALINGER = "utbetalinger";
    public static final String LAMELL_FORELDREPENGER = "foreldrepenger";
    public static final String LAMELL_SYKEPENGER = "sykepenger";
    public static final String LAMELL_PLEIEPENGER = "pleiepenger";
    public static final String LAMELL_OVERSIKT = "oversikt";
    public static final String LAMELL_BRUKERPROFIL = "brukerprofil";
    public static final String LAMELL_SAKSOVERSIKT = "saksoversikt";
    public static final String LAMELL_MELDINGER = "meldinger";
    public static final String LAMELL_VARSLING = "varsling";
    public static final String PANEL = "panel";

    private String fnrFromRequest;
    private Optional<String> startLamell = none();

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;
    @Inject
    private GsakService gsakService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private InnboksVM innboksVM;


    @Inject
    @Named("pep")
    private EnforcementPoint pep;

    public LamellContainer(String id, String fnrFromRequest, Session session, GrunnInfo grunnInfo) {
        super(id, createLamellFactories(fnrFromRequest, grunnInfo));
        this.fnrFromRequest = fnrFromRequest;

        addNewFactory(createUtbetalingLamell(fnrFromRequest));
        addNewFactory(createMeldingerLamell(fnrFromRequest, session));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (startLamell.isSome()) {
            goToLamell(startLamell.get());
        }
    }

    public void handleLamellLinkClicked(LamellPayload lamellPayload) {
        gotoAndSendToLamell(lamellPayload.lamellId.toLowerCase(), lamellPayload);
    }

    public void handleFeedItemEvent(IEvent<?> event, FeedItemPayload feedItemPayload) {
        String type = feedItemPayload.getType().toLowerCase();
        String lamellId = feedItemPayload.getWidgetId().toLowerCase();

        if (canHaveMoreThanOneLamell(type)) {
            lamellId = createLamellIfMissing(type, feedItemPayload.getItemId());
        }

        gotoAndSendToLamell(lamellId, event.getPayload());
    }

    public void handleWidgetHeaderEvent(IEvent<?> event, WidgetHeaderPayload widgetHeaderPayload) {
        gotoAndSendToLamell(widgetHeaderPayload.getType().toLowerCase(), event.getPayload());
    }

    public void handleWidgetItemEvent(String linkId) {
        if (LAMELL_KONTRAKTER.equalsIgnoreCase(linkId)) {
            goToLamell(LAMELL_KONTRAKTER);
        } else {
            ApplicationException exc = new ApplicationException("Feedlenke med ukjent type <" + linkId + "> klikket");
            logger.warn("ukjent widgetId: {}", linkId, exc);
            throw exc;
        }
    }

    public boolean hasUnsavedChanges() {
        return on(getLameller()).exists(IS_LAMELL_MODIFIED);
    }

    private String createLamellIfMissing(String type, String itemId) {
        String factoryId = type + itemId;
        if (!hasFactory(factoryId)) {
            addNewFactory(createFactory(type, itemId));
        }
        return factoryId;
    }

    private LamellFactory createFactory(String type, String itemId) {
        final Panel panel;
        if (SYKEPENGER_TYPE.equalsIgnoreCase(type)) {
            panel = new SykmeldingsperiodePanel(PANEL, Model.of(fnrFromRequest), Model.of(itemId));
        } else if (FORELDREPENGER_TYPE.equalsIgnoreCase(type)) {
            panel = new ForeldrepengerPanel(PANEL, Model.of(fnrFromRequest), Model.of(itemId));
        } else if (PLEIEPENGER_TYPE.equalsIgnoreCase(type)) {
            panel = new PleiepengerPanel(PANEL, Model.of(fnrFromRequest), itemId);
        } else {
            ApplicationException exc = new ApplicationException("Ukjent type lerret: " + type);
            logger.warn("ukjent lerret: {}", type, exc);
            throw exc;
        }

        return newLamellFactory(type, itemId, "", true, (LerretFactory) (id, name) -> new GenericLerret(id, panel));
    }

    private void gotoAndSendToLamell(String lamellId, Object payload) {
        if (hasFactory(lamellId)) {
            goToLamell(lamellId);
            sendToLamell(lamellId, payload);
        } else {
            ApplicationException exc = new ApplicationException("Ukjent lamellId <" + lamellId + "> klikket");
            logger.warn("ukjent lamellId: {}", lamellId, exc);
            throw exc;
        }
    }

    public void setStartLamell(String startLamell) {
        this.startLamell = optional(startLamell);
    }

    private static boolean canHaveMoreThanOneLamell(String type) {
        return SYKEPENGER_TYPE.equalsIgnoreCase(type) || FORELDREPENGER_TYPE.equalsIgnoreCase(type) || PLEIEPENGER_TYPE.equalsIgnoreCase(type);
    }

    private static List<LamellFactory> createLamellFactories(final String fnrFromRequest, final GrunnInfo grunnInfo) {
        List<LamellFactory> lamellFactories = new ArrayList<>();
        lamellFactories.add(createOversiktLamell(fnrFromRequest));
        lamellFactories.add(createKontrakterLamell(fnrFromRequest));
        lamellFactories.add(createBrukerprofilLamell(fnrFromRequest));
        lamellFactories.add(createSaksoversiktLamell(fnrFromRequest, grunnInfo));
        lamellFactories.add(createVarslingsLamell(fnrFromRequest));

        return lamellFactories;
    }

    private static LamellFactory createBrukerprofilLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_BRUKERPROFIL, "B", (LerretFactory) (id, name) -> new BrukerprofilPanel(id, Model.of(fnrFromRequest)));
    }

    private static LamellFactory createKontrakterLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_KONTRAKTER, "T", (LerretFactory) (id, name) -> new GenericLerret(id, new KontrakterPanel(PANEL, Model.of(fnrFromRequest))));
    }

    private static LamellFactory createOversiktLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_OVERSIKT, "O", false, (LerretFactory) (id, name) -> new OversiktLerret(id, fnrFromRequest));
    }

    private static LamellFactory createUtbetalingLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_UTBETALINGER, "U", true, (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {

            final UtbetalingLerret utbetalinglerret = new UtbetalingLerret("content", fnrFromRequest);

            @Override
            public Lerret getLazyLoadComponent(String markupId) {
                return utbetalinglerret;
            }
        });
    }

    private static LamellFactory createSaksoversiktLamell(final String fnrFromRequest, GrunnInfo grunnInfo) {
        String norgUrl = System.getProperty("server.norg2-frontend.url");
        return newLamellFactory(LAMELL_SAKSOVERSIKT, "S", true, (LerretFactory) (id, name) -> new SaksoversiktLerret(id, fnrFromRequest, grunnInfo.bruker.geografiskTilknytning, grunnInfo.bruker.diskresjonskode, norgUrl, grunnInfo.bruker.navn));
    }

    private static LamellFactory createVarslingsLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_VARSLING, "V", true, (LerretFactory) (id, name) -> new VarselLerret(id, fnrFromRequest));
    }

    private LamellFactory createMeldingerLamell(final String fnrFromRequest, final Session session) {
        innboksVM = new InnboksVM(fnrFromRequest, henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
        InnboksProps props = new InnboksProps(
                optional((String) session.getAttribute(HENVENDELSEID)),
                optional((String) session.getAttribute(OPPGAVEID)),
                optional((String) session.getAttribute(BESVARMODUS)),
                optional(Boolean.valueOf((String) session.getAttribute(BESVARES))));

        if (props.henvendelseId.isSome()) {
            innboksVM.setSessionHenvendelseId(props.henvendelseId.get());
        }
        if (props.oppgaveId.isSome() && gsakService.oppgaveKanManuelltAvsluttes(props.oppgaveId.get())) {
            innboksVM.setSessionOppgaveId(props.oppgaveId.get());
        }
        if (props.oppgaveId.isSome() && props.henvendelseId.isSome() && props.fortsettModus.getOrElse(false)) {
            innboksVM.traadBesvares = props.henvendelseId.get();
        } else if (props.besvarModus.isSome()) {
            innboksVM.traadBesvares = props.besvarModus.get();
        }
        return newLamellFactory(LAMELL_MELDINGER, "M", (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {
            @Override
            public Lerret getLazyLoadComponent(String markupId) {
                return new Innboks(markupId, innboksVM);
            }
        });
    }

    private static final Predicate<Lamell> IS_LAMELL_MODIFIED = Lamell::isModified;

    @RunOnEvents({Events.SporsmalOgSvar.SVAR_AVBRUTT, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT, Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER, Events.SporsmalOgSvar.FERDIGSTILT_UTEN_SVAR})
    public void unsetBesvartModus(AjaxRequestTarget target) {
        innboksVM.traadBesvares = null;
        innboksVM.setSessionHenvendelseId(null);
        innboksVM.setSessionOppgaveId(null);
    }
}
