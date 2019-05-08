package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.DialogSession;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.velgdialogpanel.VelgDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.OppgavetilordningFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.modig.modia.utils.ComponentFinder.in;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage.FNR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel.LEGG_TILBAKE_FERDIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.TRAADER_SLAATT_SAMMEN;

public class DialogPanel extends Panel {

    public static final String NESTE_DIALOG_LENKE_VALGT = "dialogpanel.neste.dialog.lenke.valgt";
    public static final String NY_DIALOG_LENKE_VALGT = "dialogpanel.ny.dialog.lenke.valgt";
    public static final String NY_DIALOG_AVBRUTT = "dialogpanel.ny.dialog.avbrutt";
    private static final String AKTIVT_PANEL_ID = "aktivtPanel";
    public static final String TILDELT_FLERE_ALERT = "TildeltFlereOppgaverAlert";
    public static final String TILDELT_FLERE_ALERT_WICKET_CONTAINER_ID = "reactTildeltFlereOppgaverAlertContainer";


    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private final OppgavetilordningFeilet oppgavetilordningFeiletModal;
    private final GrunnInfo grunnInfo;
    private final DialogSession session;
    private ReactComponentPanel tildeltFlereAlert;
    private Boolean harOppgaveFraGosys;
    private Boolean harPlukketOppgave;
    private Component aktivtPanel;
    private Oppgave oppgave;

    public DialogPanel(String id, GrunnInfo grunnInfo) {
        super(id);
        this.grunnInfo = grunnInfo;

        session = DialogSession.read(this);
        oppgave = session.getOppgaveSomBesvares().orElse(null);
        harPlukketOppgave = session.oppgaverBlePlukket() && oppgave != null;
        harOppgaveFraGosys = session.getOppgaveFraUrl() != null && !harPlukketOppgave;
        oppgavetilordningFeiletModal = new OppgavetilordningFeilet("oppgavetilordningModal");
        tildeltFlereAlert = new ReactComponentPanel(TILDELT_FLERE_ALERT_WICKET_CONTAINER_ID, TILDELT_FLERE_ALERT, tildeltFlereAlertProps());

        aktivtPanel = new NyDialogPanel(AKTIVT_PANEL_ID, grunnInfo);
        add(tildeltFlereAlert, aktivtPanel, oppgavetilordningFeiletModal);
        this.setOutputMarkupId(true);
        settOppForAaBesvareOppgave();
    }

    private void settOppForAaBesvareOppgave() {
        if (harOppgaveFraGosys) {
            aktivtPanel = aktivtPanel.replaceWith(new VelgDialogPanel(AKTIVT_PANEL_ID));
        } else if (harPlukketOppgave) {
            List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.bruker.fnr, oppgave.henvendelseId,
                    saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
            if (!traad.isEmpty() && !erEnkeltstaaendeSamtalereferat(traad)) {
                erstattDialogPanelMedFortsettDialogPanel(traad, oppgave);
                clearLokaleParameterVerdier();
            }
        }
    }

    private Map<String,Object> tildeltFlereAlertProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("antallTildelteOppgaver", session.getPlukkedeOppgaver().size());
        return props;
    }

    @RunOnEvents({LEGG_TILBAKE_UTFORT, FERDIGSTILT_UTEN_SVAR, MELDING_SENDT_TIL_BRUKER, SVAR_PAA_MELDING, TRAADER_SLAATT_SAMMEN})
    public void oppdaterTildeltFlereAlert(AjaxRequestTarget target) {
        tildeltFlereAlert.updateState(tildeltFlereAlertProps());
        target.add(tildeltFlereAlert);
    }

    @RunOnEvents(SVAR_PAA_MELDING)
    public void visFortsettDialogPanelBasertPaaTraadId(AjaxRequestTarget target, String traadId) {
        oppdaterTildeltFlereAlert(target);
        List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.bruker.fnr, traadId,
                saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
        String oppgaveId = null;

        oppgave = session.getPlukkedeOppgaver().stream()
                .filter(oppgave1 -> traadId.equals(oppgave1.henvendelseId))
                .findFirst()
                .orElse(oppgave);

        boolean harTrykketNyMeldingPaaAlleredePlukketOppgave = oppgave != null && traadId.equals(oppgave.henvendelseId);

        if (traadTilhorerOppgaveFraGosys(traadId)) {
            oppgave = session.getOppgaveFraUrl();
            session.withOppgaveSomBesvares(oppgave);
            oppgaveId = oppgave.oppgaveId;
        }

        if (harTrykketNyMeldingPaaAlleredePlukketOppgave && !erEnkeltstaaendeSamtalereferat(traad)) {
            oppgaveId = oppgave.oppgaveId;
            clearLokaleParameterVerdier();
        } else {
            if (erEnkeltstaaendeSporsmalFraBruker(traad)) {
                try {
                    Melding sporsmal = traad.get(0);
                    oppgaveBehandlingService.tilordneOppgaveIGsak(sporsmal.oppgaveId, Temagruppe.valueOf(sporsmal.temagruppe),
                            saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
                    oppgaveId = sporsmal.oppgaveId;
                } catch (OppgaveBehandlingService.FikkIkkeTilordnet fikkIkkeTilordnet) {
                    oppgavetilordningFeiletModal.vis(target);
                }
            }
        }
        String svarHenvendelseId = opprettSvar(traadId);
        Oppgave oppgaveSomBesvares = new Oppgave(oppgaveId, grunnInfo.bruker.fnr, traadId)
                .withSvarHenvendelseId(svarHenvendelseId);
        erstattDialogPanelMedFortsettDialogPanel(traad, oppgaveSomBesvares);
        if (oppgaveId != null) {
            session.withOppgaveSomBesvares(oppgaveSomBesvares);
        } else {
            session.withOppgaveSomBesvares(null);
        }
        target.add(aktivtPanel);
    }

    private boolean traadTilhorerOppgaveFraGosys(String traadId) {
        return session.getOppgaveFraUrl() != null && traadId.equals(session.getOppgaveFraUrl().henvendelseId);
    }

    private String opprettSvar(String traadId) {
        String type = SVAR_SKRIFTLIG.toString();
        String fnr = grunnInfo.bruker.fnr;
        return henvendelseUtsendingService.opprettHenvendelse(type, fnr, traadId);
    }

    private static boolean erEnkeltstaaendeSamtalereferat(List<Melding> traad) {
        List<Meldingstype> samtalereferat = asList(SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON);
        return traad.size() == 1 && samtalereferat.contains(traad.get(0).meldingstype);
    }

    private static boolean erEnkeltstaaendeSporsmalFraBruker(List<Melding> traad) {
        List<Melding> andreMeldingstyper = traad.stream()
                .filter(melding -> !(melding.erDelvisSvar()))
                .filter(melding -> !(melding.meldingstype == SPORSMAL_SKRIFTLIG || melding.meldingstype == SPORSMAL_SKRIFTLIG_DIREKTE))
                .collect(toList());
        return andreMeldingstyper.isEmpty();
    }

    private void erstattDialogPanelMedFortsettDialogPanel(List<Melding> traad, Oppgave oppgave) {
        aktivtPanel = aktivtPanel.replaceWith(new FortsettDialogPanel(AKTIVT_PANEL_ID, grunnInfo, traad, oppgave));
    }

    private void erstattDialogPanelMedKvitteringspanel(AjaxRequestTarget target) {
        KvitteringsPanel kvitteringsPanel = new KvitteringsPanel(AKTIVT_PANEL_ID);
        kvitteringsPanel.visKvittering(target, getString("dialogpanel.ferdigstiltUtenSvar.kvittering.bekreftelse"), aktivtPanel);
        aktivtPanel = aktivtPanel.replaceWith(kvitteringsPanel);
        aktivtPanel.add(hasCssClassIf("kvittering", Model.of(true)));

        List<AjaxLink> links = (List<AjaxLink>) in((MarkupContainer) aktivtPanel)
                .findComponents(AjaxLink.class);
        links.forEach(ajaxLink -> ajaxLink.setOutputMarkupId(true));
        target.add(kvitteringsPanel);
        target.focusComponent(links.get(0));
    }

    private void clearLokaleParameterVerdier() {
        oppgave = null;
        harPlukketOppgave = false;
    }

    @RunOnEvents({NY_DIALOG_LENKE_VALGT})
    public void visNyDialogPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new NyDialogPanel(AKTIVT_PANEL_ID, grunnInfo));
        target.add(aktivtPanel);
        TextArea textarea = in((MarkupContainer) aktivtPanel).findComponent(TextArea.class);
        target.focusComponent(textarea);
    }

    @RunOnEvents({NESTE_DIALOG_LENKE_VALGT})
    public void plukkNesteTildelteOppgave(AjaxRequestTarget target) {
        session.withOppgaveSomBesvares(session.getPlukkedeOppgaver().get(0))
                .withOppgaverBlePlukket(true);
        setResponsePage(PersonPage.class, new PageParameters().set(FNR, session.getOppgaveSomBesvares().get().fnr));
    }

    @RunOnEvents({FERDIGSTILT_UTEN_SVAR})
    public void visKvitteringVedFerdigstiltUtenSvar(AjaxRequestTarget target) {
        erstattDialogPanelMedKvitteringspanel(target);
    }

    @RunOnEvents({LEGG_TILBAKE_FERDIG, SVAR_AVBRUTT, NY_DIALOG_AVBRUTT})
    public void visVelgDialogPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new VelgDialogPanel(AKTIVT_PANEL_ID));
        target.add(aktivtPanel);
    }

}
