package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.brukerprofil.BrukerprofilPanel;
import no.nav.kjerneinfo.kontrakter.KontrakterPanel;
import no.nav.metrics.MetricsFactory;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.LamellPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.lamell.*;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.DialogSession;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.nysykepenger.NyttSykmeldingsperiodePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt.OversiktLerret;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.lamell.SaksoversiktLerret;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLerret;
import no.nav.sbl.dialogarena.varsel.lamell.VarselLerret;
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import no.nav.sykmeldingsperioder.foreldrepenger.ForeldrepengerPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.nyforeldrepenger.NyttForeldrepengerPanel;
import no.nav.sykmeldingsperioder.pleiepenger.PleiepengerPanel;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private String startLamell = null;

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;
    @Inject
    private GsakService gsakService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private UnleashService unleashService;

    private InnboksVM innboksVM;


    @Inject
    @Named("pep")
    private EnforcementPoint pep;

    public LamellContainer(String id, Session session, GrunnInfo grunnInfo, boolean nySaksoversikt, boolean nyOppfolging) {
        super(id, createLamellFactories(grunnInfo.bruker, nySaksoversikt, nyOppfolging));
        this.fnrFromRequest = grunnInfo.bruker.fnr;

        addNewFactory(createUtbetalingLamell(grunnInfo.bruker));
        this.dialogSession = DialogSession.read(session);
        addNewFactory(createMeldingerLamell(grunnInfo.bruker, dialogSession));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (startLamell != null) {
            goToLamell(startLamell);
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
        return getLameller().stream().anyMatch(Lamell::isModified);
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
            boolean nySykepengerToggle = unleashService.isEnabled(Feature.NY_SYKEPENGER);
            if (nySykepengerToggle){
                panel = new NyttSykmeldingsperiodePanel(PANEL, Model.of(fnrFromRequest), Model.of(itemId));
            } else {
                panel = new SykmeldingsperiodePanel(PANEL, Model.of(fnrFromRequest), Model.of(itemId));
            }
        } else if (FORELDREPENGER_TYPE.equalsIgnoreCase(type)) {
            boolean nyForeldrepenger = unleashService.isEnabled(Feature.NY_FORELDREPENGER);
            if (nyForeldrepenger) {
                panel = new NyttForeldrepengerPanel(PANEL, Model.of(fnrFromRequest), Model.of(itemId));
            } else {
                panel = new ForeldrepengerPanel(PANEL, Model.of(fnrFromRequest), Model.of(itemId));
            }
        } else if (PLEIEPENGER_TYPE.equalsIgnoreCase(type)) {
            boolean nyttPleiepengerPanelToggle = unleashService.isEnabled(Feature.NY_PLEIEPENGER);
            panel = new PleiepengerPanel(PANEL, Model.of(fnrFromRequest), itemId, nyttPleiepengerPanelToggle);
            return new DefaultLamellFactory(type, itemId, "", true, (LerretFactory) (id, name) -> new GenericLerret(id, panel)) {
                @Override
                public IModel<String> getHeading() {
                    return new StringResourceModel(LamellPanel.RESOURCE_PREFIX_LAMELL + "." + type.toLowerCase() + ".heading", null, new Object[]{((PleiepengerPanel) panel).idDato});
                }
            };
        } else {
            ApplicationException exc = new ApplicationException("Ukjent type lerret: " + type);
            logger.warn("ukjent lerret: {}", type, exc);
            throw exc;
        }

        return newLamellFactory(type, itemId, "", true, (LerretFactory) (id, name) -> new GenericLerret(id, panel));
    }

    private void gotoAndSendToLamell(String lamellId, Object payload) {
        if (hasFactory(lamellId)) {
            MetricsFactory.createEvent("hendelse.lamell.aapnet")
                    .addTagToReport("lamell", lamellId.replaceAll("[$.\\d]", ""))
                    .report();
            goToLamell(lamellId);
            sendToLamell(lamellId, payload);
        } else {
            ApplicationException exc = new ApplicationException("Ukjent lamellId <" + lamellId + "> klikket");
            logger.warn("ukjent lamellId: {}", lamellId, exc);
            throw exc;
        }
    }

    public void setStartLamell(String startLamell) {
        this.startLamell = startLamell;
    }

    private static boolean canHaveMoreThanOneLamell(String type) {
        return SYKEPENGER_TYPE.equalsIgnoreCase(type) || FORELDREPENGER_TYPE.equalsIgnoreCase(type) || PLEIEPENGER_TYPE.equalsIgnoreCase(type);
    }

    private static List<LamellFactory> createLamellFactories(final GrunnInfo.Bruker bruker, boolean nySaksoversikt, final boolean nyOppfolging) {
        List<LamellFactory> lamellFactories = new ArrayList<>();
        lamellFactories.add(createOversiktLamell(bruker));
        lamellFactories.add(createKontrakterLamell(bruker, nyOppfolging));
        lamellFactories.add(createBrukerprofilLamell(bruker));
        lamellFactories.add(createSaksoversiktLamell(bruker, nySaksoversikt));
        lamellFactories.add(createVarslingsLamell(bruker));

        return lamellFactories;
    }

    private static LamellFactory createBrukerprofilLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_BRUKERPROFIL, "B", true, (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {
            final Component comp = new ReactComponentPanel("brukerprofilpanel", "NyBrukerprofil", new HashMap<String, Object>() {{
                put("fødselsnummer", bruker.fnr);
            }});

            final NyBrukerprofilLerret brukerprofillerret = new NyBrukerprofilLerret("content", comp);

            @Override
            public Lerret getLazyLoadComponent(String markupId) {
                return brukerprofillerret;
            }
        });
    }

    private static LamellFactory createKontrakterLamell(final GrunnInfo.Bruker bruker, final boolean nyOppfolging) {
        if (nyOppfolging) {
            return newLamellFactory(LAMELL_KONTRAKTER, "T", true, (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {
                final Component comp = new ReactComponentPanel("oppfolgingpanel", "NyOppfolging", new HashMap<String, Object>() {{
                    put("fødselsnummer", bruker.fnr);
                }});

                final NyOppfolgingLerret oppfolgingLerret = new NyOppfolgingLerret("content", comp);

                @Override
                public Lerret getLazyLoadComponent(String markupId) {
                    return oppfolgingLerret;
                }
            });
        } else {
            return newLamellFactory(LAMELL_KONTRAKTER, "T", (LerretFactory) (id, name) -> new GenericLerret(id, new KontrakterPanel(PANEL, Model.of(bruker.fnr))));
        }
    }

    private static LamellFactory createOversiktLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_OVERSIKT, "O", false, (LerretFactory) (id, name) -> new OversiktLerret(id, bruker.fnr));
    }

    private static LamellFactory createUtbetalingLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_UTBETALINGER, "U", true, (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {
            final Component comp = new ReactComponentPanel("utbetalingpanel", "NyUtbetaling", new HashMap<String, Object>() {{
                put("fødselsnummer", bruker.fnr);
            }});

            final NyUtbetalingLerret utbetalinglerret = new NyUtbetalingLerret("content", comp);

            @Override
            public Lerret getLazyLoadComponent(String markupId) {
                return utbetalinglerret;
            }
        });
    }

    private static LamellFactory createSaksoversiktLamell(final GrunnInfo.Bruker bruker, final boolean nySaksoversikt) {
        String norgUrl = System.getProperty("server.norg2-frontend.url");
        if (nySaksoversikt) {
            return newLamellFactory(LAMELL_SAKSOVERSIKT, "S", true, (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {
                final Component comp = new ReactComponentPanel("saksoversiktpanel", "NySaksoversikt", new HashMap<String, Object>() {{
                    put("fødselsnummer", bruker.fnr);
                }});

                final NySaksoversiktLerret saksoversiktLerret = new NySaksoversiktLerret("content", comp);

                @Override
                public Lerret getLazyLoadComponent(String markupId) {
                    return saksoversiktLerret;
                }
            });
        } else {
            return newLamellFactory(LAMELL_SAKSOVERSIKT, "S", true, (LerretFactory) (id, name) -> new SaksoversiktLerret(id, bruker.fnr, bruker.geografiskTilknytning, bruker.diskresjonskode, norgUrl, bruker.navn));
        }
    }

    private static LamellFactory createVarslingsLamell(final GrunnInfo.Bruker bruker) {
        return newLamellFactory(LAMELL_VARSLING, "V", true, (LerretFactory) (id, name) -> new VarselLerret(id, bruker.fnr, true));
    }

    private LamellFactory createMeldingerLamell(final GrunnInfo.Bruker bruker, final DialogSession session) {
        innboksVM = initialiserInnboksVM(bruker, session);

        return newLamellFactory(LAMELL_MELDINGER, "M", (LerretFactory) (id, name) -> new AjaxLazyLoadLerret(id, name) {
            @Override
            public Lerret getLazyLoadComponent(String markupId) {
                return new Innboks(markupId, innboksVM);
            }
        });
    }

    private InnboksVM initialiserInnboksVM(GrunnInfo.Bruker bruker, DialogSession session) {
        innboksVM = new InnboksVM(bruker.fnr, henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);

        innboksVM.tildelteOppgaver.clear();
        innboksVM.tildelteOppgaver.addAll(session.getPlukkedeOppgaver());

        session.getOppgaveSomBesvares().ifPresent(oppgave -> innboksVM.traadBesvares = oppgave.henvendelseId);

        Oppgave oppgaveFraUrl = session.getOppgaveFraUrl();
        if (oppgaveFraUrl != null) {
            if (oppgaveFraUrl.oppgaveId != null && gsakService.oppgaveKanManuelltAvsluttes(oppgaveFraUrl.oppgaveId)) {
                innboksVM.setSessionOppgaveId(oppgaveFraUrl.oppgaveId);
            }
            innboksVM.setSessionHenvendelseId(oppgaveFraUrl.henvendelseId);
        }

        return innboksVM;
    }

    @RunOnEvents({Events.SporsmalOgSvar.SVAR_AVBRUTT, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT, Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER, Events.SporsmalOgSvar.FERDIGSTILT_UTEN_SVAR})
    public void unsetBesvartModus(AjaxRequestTarget target) {
        DialogSession.read(this)
                .withOppgaveSomBesvares(null)
                .withOppgaverBlePlukket(false)
                .getPlukkedeOppgaver()
                .removeIf(oppgave -> oppgave.henvendelseId.equals(innboksVM.traadBesvares));
        innboksVM.tildelteOppgaver.removeIf(oppgave -> oppgave.henvendelseId.equals(innboksVM.traadBesvares));
        innboksVM.traadBesvares = null;
        innboksVM.setSessionHenvendelseId(null);
        innboksVM.setSessionOppgaveId(null);
    }
}
