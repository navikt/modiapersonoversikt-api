package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.metrics.MetricsFactory;
import no.nav.modig.lang.collections.predicate.GreaterThanPredicate;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.DialogSession;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.TraadAlleredeBesvart;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;
import static org.apache.wicket.event.Broadcast.DEPTH;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference MELDINGER_JS = new JavaScriptResourceReference(Innboks.class, "meldinger.js");
    public static final JavaScriptResourceReference BESVAR_INDIKATOR_JS = new JavaScriptResourceReference(Innboks.class, "besvarIndikator.js");
    public static final String INNBOKS_OPPDATERT_EVENT = "sos.innboks.oppdatert";
    public static final String TRAADER_SLAATT_SAMMEN = "slaaSammenEvent";

    private InnboksVM innboksVM;
    private final ReactComponentPanel slaaSammenTraaderPanel;

    @Inject
    OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    HenvendelseUtsendingService henvendelseUtsendingService;

    public Innboks(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupId(true);
        setDefaultModel(new CompoundPropertyModel<Object>(innboksVM));
        this.innboksVM = innboksVM;
        innboksVM.oppdaterMeldinger();
        innboksVM.settForsteSomValgtHvisIkkeSatt();

        if (innboksVM.getSessionHenvendelseId().isPresent()) {
            Optional<MeldingVM> meldingITraad = innboksVM.getNyesteMeldingITraad(innboksVM.getSessionHenvendelseId().get());
            meldingITraad.ifPresent(innboksVM::setValgtMelding);
        }

        PropertyModel<Boolean> harTraader = new PropertyModel<>(innboksVM, "harTraader");

        WebMarkupContainer meldingsliste = new WebMarkupContainer("meldingsliste");
        meldingsliste.add(visibleIf(both(harTraader).and(not(innboksVM.harFeilmelding()))));

        final TraaddetaljerPanel traaddetaljerPanel = new TraaddetaljerPanel("detaljpanel", innboksVM);
        traaddetaljerPanel.setOutputMarkupId(true);
        traaddetaljerPanel.add(visibleIf(both(harTraader).and(not(innboksVM.harFeilmelding()))));


        final AlleMeldingerPanel alleMeldingerPanel = new AlleMeldingerPanel("meldinger", innboksVM);
        final ReactComponentPanel meldingerSok = new ReactComponentPanel("meldingerSokContainer", "MeldingerSok", getMeldingerSokProps());


        final WebMarkupContainer innboksButtonContainer = new WebMarkupContainer("innboksButtonContainer");
        innboksButtonContainer.setOutputMarkupId(true);
        AjaxLink meldingerSokToggleButton = new SokKnapp("meldingerSokToggle") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                innboksVM.oppdaterMeldinger();
                target.add(alleMeldingerPanel, traaddetaljerPanel);
                meldingerSok.call("vis", getMeldingerSokProps());
                target.add(innboksButtonContainer);
                MetricsFactory.createEvent("hendelse.meldinger-lamell-apne-sok-knapp.klikk").report();
            }
        };
        meldingerSok.addCallback("oppdater", Void.class, (target, data) -> {
            innboksVM.oppdaterMeldinger();
            target.add(alleMeldingerPanel, traaddetaljerPanel);
        });
        innboksButtonContainer.add(meldingerSokToggleButton);

        slaaSammenTraaderPanel = new ReactComponentPanel("slaaSammenTraaderContainer", "SlaaSammenTraader", getSlaaSammenTraaderProps());
        AjaxLink slaaSammenTraaderToggleButton = new SokKnapp("slaaSammenTraaderToggle") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                slaaSammenTraaderPanel.setVisibilityAllowed(true);
                innboksVM.oppdaterMeldinger();
                target.add(alleMeldingerPanel, traaddetaljerPanel);
                slaaSammenTraaderPanel.call("vis", getSlaaSammenTraaderProps());
                target.add(innboksButtonContainer);
                MetricsFactory.createEvent("hendelse.meldinger-lamell-besvar-flere-knapp.klikk").report();
            }
        };
        slaaSammenTraaderToggleButton.add(visibleIf(when(sizeOf(innboksVM.getTildelteOppgaverUtenDelsvar()),
                new GreaterThanPredicate<>(1))));
        slaaSammenTraaderPanel.addCallback("slaaSammen", List.class, (target, data) -> {
            @SuppressWarnings("unchecked")
            List<String> traadIder = (List<String>) data;
            slaaSammenTraader(traadIder);
            innboksVM.oppdaterMeldinger();
            target.add(this);
            send(getPage(), DEPTH, TRAADER_SLAATT_SAMMEN);
            slaaSammenTraaderPanel.call("skjul");
            slaaSammenTraaderPanel.setVisibilityAllowed(false);
        });
        innboksButtonContainer.add(slaaSammenTraaderToggleButton);

        WebMarkupContainer feilmeldingPanel = visFeilMelding(innboksVM);

        meldingsliste.add(meldingerSok, slaaSammenTraaderPanel, innboksButtonContainer, alleMeldingerPanel);
        add(meldingsliste, traaddetaljerPanel, feilmeldingPanel);
    }

    private void slaaSammenTraader(List<String> traadIder) {
        List<Oppgave> oppgaver = hentTildelteOppgaverFraTraadIder(traadIder);

        for (Oppgave oppgave : oppgaver) {
            if (oppgaveBehandlingService.oppgaveErFerdigstilt(oppgave.oppgaveId)) {
                haandterOppgaveAlleredeFerdigstilt(oppgave.oppgaveId);
                return;
            }
        }

        List<String> meldingsIder = hentMeldingsIderFraTraadIder(traadIder);

        String nyTraadId;
        try {
            nyTraadId = henvendelseUtsendingService.slaaSammenTraader(meldingsIder);
        } catch (TraadAlleredeBesvart e) {
            haandterTraadAlleredeBesvart(e.traadId);
            return;
        }

        String nySvarHenvendelse = henvendelseUtsendingService.opprettHenvendelse(SVAR_SKRIFTLIG.toString(), oppgaver.get(0).fnr, nyTraadId);

        ferdigstillAlleUnntattEnOppgave(oppgaver, nyTraadId);
        Oppgave oppdatertOppgave = finnGjenvaerendeOppgave(oppgaver, nyTraadId)
                .withSvarHenvendelseId(nySvarHenvendelse);

        innboksVM.setSessionHenvendelseId(nyTraadId);
        innboksVM.setSessionOppgaveId(oppdatertOppgave.oppgaveId);
        DialogSession.read(this)
                .withOppgaveSomBesvares(oppdatertOppgave)
                .withOppgaverBlePlukket(true);
        innboksVM.oppdaterMeldinger();
        innboksVM.setValgtMelding(meldingsIder.stream().max(String::compareToIgnoreCase).get());

        send(getPage(), DEPTH, new NamedEventPayload(Events.SporsmalOgSvar.SVAR_PAA_MELDING, nyTraadId));
    }

    private Oppgave finnGjenvaerendeOppgave(List<Oppgave> oppgaver, String nyTraadId) {
        return oppgaver.stream()
                .filter(oppgave -> nyTraadId.equals(oppgave.henvendelseId))
                .findAny()
                .get();
    }

    private void ferdigstillAlleUnntattEnOppgave(List<Oppgave> oppgaver, String unntatt) {
        oppgaver.stream()
                .filter(oppgave -> !unntatt.equals(oppgave.henvendelseId))
                .forEach(this::ferdigstillOppgave);
    }

    private List<String> hentMeldingsIderFraTraadIder(List<String> traadIder) {
        return traadIder.stream()
                .flatMap(traadId -> innboksVM.getTraader()
                        .get(traadId)
                        .getMeldinger()
                        .stream()
                        .map(meldingVM -> meldingVM.melding.id))
                .collect(toList());
    }

    private void haandterOppgaveAlleredeFerdigstilt(String oppgaveId) {
        // TODO vis feilmelding
        DialogSession.read(this)
                .getPlukkedeOppgaver()
                .removeIf(oppgave -> oppgaveId.equals(oppgave.oppgaveId));
        innboksVM.tildelteOppgaver
                .removeIf(oppgave -> oppgaveId.equals(oppgave.oppgaveId));
    }

    private void haandterTraadAlleredeBesvart(String traadId) {
        // TODO vis feilmelding
        innboksVM.tildelteOppgaver
                .removeIf(oppgave -> traadId.equals(oppgave.henvendelseId));
        DialogSession.read(this)
                .getPlukkedeOppgaver()
                .removeIf(oppgave -> traadId.equals(oppgave.henvendelseId));
    }

    private void ferdigstillOppgave(Oppgave oppgave) {
        oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                oppgave.oppgaveId,
                Temagruppe.valueOf(innboksVM.getTraader()
                        .get(oppgave.henvendelseId)
                        .getEldsteMelding().melding.temagruppe),
                saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()
        );
        innboksVM.tildelteOppgaver.remove(oppgave);
        DialogSession.read(this).getPlukkedeOppgaver().remove(oppgave);
    }

    private List<Oppgave> hentTildelteOppgaverFraTraadIder(List<String> traadIder) {
        List<Oppgave> oppgaver = innboksVM.tildelteOppgaver.stream()
                .filter(oppgave -> traadIder.contains(oppgave.henvendelseId))
                .collect(toList());

        if (oppgaver.size() != traadIder.size()) {
            throw new IllegalArgumentException("Ikke-tildelte oppgaver forsøkt slått sammen.");
        }

        return oppgaver;
    }

    private WebMarkupContainer visFeilMelding(InnboksVM innboksVM) {
        WebMarkupContainer feilmeldingPanel = new WebMarkupContainer("feilmeldingpanel");
        feilmeldingPanel.add(new Label("feilmelding", new StringResourceModel("${feilmeldingKey}", getDefaultModel(), "")));
        feilmeldingPanel.add(visibleIf(innboksVM.harFeilmelding()));

        if (innboksVM.harFeilmelding().getObject().equals(true)) {
            String beskrivelse = "Teknisk feil modiabrukerdialog, oppgave lagt tilbake.";
            LeggTilbakeOppgaveIGsakRequest request = new LeggTilbakeOppgaveIGsakRequest()
                    .withBeskrivelse(beskrivelse)
                    .withOppgaveId(innboksVM.getSessionOppgaveId().orElse(null))
                    .withTemagruppe(null)
                    .withSaksbehandlersValgteEnhet(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
            oppgaveBehandlingService.leggTilbakeOppgaveIGsak(request);
            innboksVM.setSessionHenvendelseId(null);
            innboksVM.setSessionOppgaveId(null);
        }
        return feilmeldingPanel;
    }

    private Map<String, Object> getSlaaSammenTraaderProps() {
        Map<String, Object> props = getMeldingerSokProps();
        props.put("traadIder", innboksVM.getTildelteOppgaverUtenDelsvar());
        return props;
    }

    private Map<String, Object> getMeldingerSokProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("fnr", innboksVM.getFnr());
        props.put("traadMarkupIds", traadRefs(innboksVM));
        return props;
    }

    private Map<String, String> traadRefs(InnboksVM innboksVM) {
        return innboksVM.getNyesteMeldingerITraader().stream()
                .collect(toMap(
                        MeldingVM::getTraadId,
                        meldingVM -> AlleMeldingerPanel.TRAAD_ID_PREFIX + meldingVM.getTraadId()
                ));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript(
                "$(document).on('click', '.innboksSokToggle button',function(){$(this).addClass('loading');});"));
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        if (target != null) {
            target.appendJavaScript("Meldinger.init();");
            if (innboksVM.focusValgtTraadOnOpen) {
                target.appendJavaScript("Meldinger.focusOnSelectedElement()");
            }
        }
    }

    @Override
    public void onClosing(AjaxRequestTarget target, boolean isMinimizing) {
        innboksVM.setSessionHenvendelseId(null);
        if (!isMinimizing) {
            innboksVM.setValgtMelding((MeldingVM) null);
            innboksVM.focusValgtTraadOnOpen = false;
        }
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        String itemId = feedItemPayload.getItemId();
        if (!itemId.equals(innboksVM.getValgtTraad().getNyesteMelding().getId())) {
            innboksVM.setValgtMelding(itemId);
            send(getPage(), DEPTH, MELDING_VALGT);
            target.add(this);
        }
        innboksVM.focusValgtTraadOnOpen = true;
    }

    @RunOnEvents(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER)
    public void oppdatertInnboks(AjaxRequestTarget target) {
        innboksVM.oppdaterMeldinger();
        send(getPage(), DEPTH, INNBOKS_OPPDATERT_EVENT);
        target.add(this);
    }

    @RunOnEvents(Events.SporsmalOgSvar.SVAR_PAA_MELDING)
    public void setBesvarModus(AjaxRequestTarget target, String traadId) {
        innboksVM.traadBesvares = traadId;
        target.add(this);
    }

    @RunOnEvents({Events.SporsmalOgSvar.SVAR_AVBRUTT, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT, Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER, TRAAD_MERKET})
    public void leggTilTarget(AjaxRequestTarget target) {
        target.add(this);
    }

}
