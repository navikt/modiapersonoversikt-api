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
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt.OversiktLerret;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogSession;
import no.nav.sbl.dialogarena.sak.lamell.SaksoversiktLerret;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLerret;
import no.nav.sbl.dialogarena.varsel.lamell.VarselLerret;
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import no.nav.sykmeldingsperioder.foreldrepenger.ForeldrepengerPanel;
import no.nav.sykmeldingsperioder.pleiepenger.PleiepengerPanel;
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
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.*;

public class LamellContainer extends TokenLamellPanel implements Serializable {

    private final DialogSession dialogSession;
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

    public LamellContainer(String id, Session session, GrunnInfo grunnInfo) {
        super(id, createLamellFactories(grunnInfo.bruker));
        this.fnrFromRequest = grunnInfo.bruker.fnr;

        addNewFactory(createUtbetalingLamell(grunnInfo.bruker));
        this.dialogSession = DialogSession.read(session);
        addNewFactory(createMeldingerLamell(grunnInfo.bruker, dialogSession));
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
        return on(getLameller()).exists(Lamell::isModified);
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

    private static List<LamellFactory> createLamellFactories(final GrunnInfo.Bruker bruker) {
        List<LamellFactory> lamellFactories = new ArrayList<>();
        lamellFactories.add(createOversiktLamell(bruker));
        lamellFactories.add(createKontrakterLamell(bruker));
        lamellFactories.add(createBrukerprofilLamell(bruker));
        lamellFactories.add(createSaksoversiktLamell(bruker));
        lamellFactories.add(createVarslingsLamell(bruker));

        return lamellFactories;
    }

    private static LamellFactory createBrukerprofilLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_BRUKERPROFIL, "B", (LerretFactory) (id, name) -> new BrukerprofilPanel(id, Model.of(bruker.fnr)));
    }

    private static LamellFactory createKontrakterLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_KONTRAKTER, "T", (LerretFactory) (id, name) -> new GenericLerret(id, new KontrakterPanel(PANEL, Model.of(bruker.fnr))));
    }

    private static LamellFactory createOversiktLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_OVERSIKT, "O", false, (LerretFactory) (id, name) -> new OversiktLerret(id, bruker.fnr));
    }

    private static LamellFactory createUtbetalingLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_UTBETALINGER, "U", true, (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {

            final UtbetalingLerret utbetalinglerret = new UtbetalingLerret("content", bruker.fnr);

            @Override
            public Lerret getLazyLoadComponent(String markupId) {
                return utbetalinglerret;
            }
        });
    }

    private static LamellFactory createSaksoversiktLamell(final GrunnInfo.Bruker bruker) {
        String norgUrl = System.getProperty("server.norg2-frontend.url");
        return newLamellFactory(LAMELL_SAKSOVERSIKT, "S", true, (LerretFactory) (id, name) -> new SaksoversiktLerret(id, bruker.fnr, bruker.geografiskTilknytning, bruker.diskresjonskode, norgUrl, bruker.navn));
    }

    private static LamellFactory createVarslingsLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_VARSLING, "V", true, (LerretFactory) (id, name) -> new VarselLerret(id, bruker.fnr));
    }

    private LamellFactory createMeldingerLamell(final GrunnInfo.Bruker bruker, final DialogSession session) {
        innboksVM = new InnboksVM(bruker.fnr, henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);

        innboksVM.tildelteOppgaver = session.getPlukkedeOppgaver();

        java.util.Optional<Oppgave> oppgaveSomBesvares = session.getOppgaveSomBesvares();

        oppgaveSomBesvares.ifPresent((oppgave) -> {
            innboksVM.setSessionHenvendelseId(oppgave.henvendelseId);
            assert oppgave.oppgaveId != null;
            if (gsakService.oppgaveKanManuelltAvsluttes(oppgave.oppgaveId)) {
                innboksVM.setSessionOppgaveId(oppgave.oppgaveId);
            }
            innboksVM.traadBesvares = oppgave.henvendelseId;
        });

        if (!oppgaveSomBesvares.isPresent() && session.getOppgaveFraUrl() != null) {
            innboksVM.setSessionHenvendelseId(session.getOppgaveFraUrl().henvendelseId);
            innboksVM.setSessionOppgaveId(session.getOppgaveFraUrl().oppgaveId);
        }

        return newLamellFactory(LAMELL_MELDINGER, "M", (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {
            @Override
            public Lerret getLazyLoadComponent(String markupId) {
                return new Innboks(markupId, innboksVM);
            }
        });
    }

    @RunOnEvents({Events.SporsmalOgSvar.SVAR_AVBRUTT, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT, Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER, Events.SporsmalOgSvar.FERDIGSTILT_UTEN_SVAR})
    public void unsetBesvartModus(AjaxRequestTarget target) {
        DialogSession.read(this)
                .withOppgaveSomBesvares(null)
                .getPlukkedeOppgaver()
                .removeIf(o -> o.henvendelseId.equals(innboksVM.traadBesvares));
        innboksVM.traadBesvares = null;
        innboksVM.setSessionHenvendelseId(null);
        innboksVM.setSessionOppgaveId(null);
    }
}
