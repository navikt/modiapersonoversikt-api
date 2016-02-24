package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.SessionParametere;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.EnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Bruker;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.velgdialogpanel.VelgDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.OppgavetilordningFeilet;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.modia.utils.ComponentFinder.in;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.SVAR_AVBRUTT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel.LEGG_TILBAKE_FERDIG;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DialogPanel extends Panel {

    public static final String NY_DIALOG_LENKE_VALGT = "dialogpanel.ny.dialog.lenke.valgt";
    public static final String NY_DIALOG_AVBRUTT = "dialogpanel.ny.dialog.avbrutt";
    private static final String AKTIVT_PANEL_ID = "aktivtPanel";
    private static final Predicate<String> ER_SATT = new Predicate<String>() {
        @Override
        public boolean evaluate(String s) {
            return !isBlank(s);
        }
    };

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private LDAPService ldapService;
    @Inject
    private EnhetService enhetService;

    private Component aktivtPanel;
    private OppgavetilordningFeilet oppgavetilordningFeiletModal;
    private GrunnInfo grunnInfo;
    private Optional<String> oppgaveIdFraParametere = none();
    private Optional<String> henvendelsesIdFraParametere = none();
    private Boolean besvaresFraParametere = false;

    public DialogPanel(String id, String fnr, GrunnInfo grunnInfo) {
        super(id);

        settOppVerdierFraParameterePaaSession();
        aktivtPanel = new NyDialogPanel(AKTIVT_PANEL_ID, grunnInfo);
        oppgavetilordningFeiletModal = new OppgavetilordningFeilet("oppgavetilordningModal");

        add(aktivtPanel, oppgavetilordningFeiletModal);

        settOppRiktigMeldingPanel();
    }

    private void settOppVerdierFraParameterePaaSession() {
        henvendelsesIdFraParametere = optional(ER_SATT, (String) getSession().getAttribute(HENVENDELSEID));
        oppgaveIdFraParametere = optional(ER_SATT, (String) getSession().getAttribute(OPPGAVEID));

        String besvares = (String) getSession().getAttribute(BESVARES);
        besvaresFraParametere = !isBlank(besvares) && Boolean.valueOf(besvares);
    }







    private void settOppRiktigMeldingPanel() {
        if (henvendelsesIdFraParametere.isSome() && oppgaveIdFraParametere.isSome()) {
            if (besvaresFraParametere) {
                List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.bruker.fnr, henvendelsesIdFraParametere.get());
                if (!traad.isEmpty() && !erEnkeltstaaendeSamtalereferat(traad)) {
                    try {
                        oppgaveBehandlingService.tilordneOppgaveIGsak(oppgaveIdFraParametere.get(), Temagruppe.valueOf(traad.get(0).temagruppe));
                        erstattDialogPanelMedFortsettDialogPanel(traad, oppgaveIdFraParametere);
                    } catch (OppgaveBehandlingService.FikkIkkeTilordnet fikkIkkeTilordnet) {
                        throw new RuntimeException(fikkIkkeTilordnet);
                    }
                    clearLokaleParameterVerdier();
                }
            } else {
                aktivtPanel = aktivtPanel.replaceWith(new VelgDialogPanel(AKTIVT_PANEL_ID));
            }
        }
    }

    @RunOnEvents(Events.SporsmalOgSvar.SVAR_PAA_MELDING)
    public void visFortsettDialogPanelBasertPaaTraadId(AjaxRequestTarget target, String traadId) {
        List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.bruker.fnr, traadId);
        Optional<String> oppgaveId = none();
        if (henvendelsesIdFraParametere.isSome() && oppgaveIdFraParametere.isSome()
                && traadId.equals(henvendelsesIdFraParametere.get())
                && !traad.isEmpty()
                && !erEnkeltstaaendeSamtalereferat(traad)) {
            oppgaveId = oppgaveIdFraParametere;
            clearLokaleParameterVerdier();
        } else {
            if (erEnkeltstaaendeSporsmalFraBruker(traad)) {
                try {
                    Melding sporsmal = traad.get(0);
                    String sporsmalOppgaveId = sporsmal.oppgaveId;
                    oppgaveBehandlingService.tilordneOppgaveIGsak(sporsmalOppgaveId, Temagruppe.valueOf(sporsmal.temagruppe));
                    oppgaveId = optional(sporsmalOppgaveId);
                } catch (OppgaveBehandlingService.FikkIkkeTilordnet fikkIkkeTilordnet) {
                    oppgavetilordningFeiletModal.vis(target);
                }
            }
        }
        erstattDialogPanelMedFortsettDialogPanel(traad, oppgaveId);
        getSession().setAttribute(SessionParametere.SporsmalOgSvar.BESVARMODUS, traadId);
        target.add(aktivtPanel);
    }

    private static boolean erEnkeltstaaendeSamtalereferat(List<Melding> traad) {
        List<Meldingstype> samtalereferat = asList(SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON);
        return traad.size() == 1 && samtalereferat.contains(traad.get(0).meldingstype);
    }

    private static boolean erEnkeltstaaendeSporsmalFraBruker(List<Melding> traad) {
        return traad.size() == 1 && traad.get(0).meldingstype == SPORSMAL_SKRIFTLIG;
    }

    private void erstattDialogPanelMedFortsettDialogPanel(List<Melding> traad, Optional<String> oppgaveId) {
        aktivtPanel = aktivtPanel.replaceWith(new FortsettDialogPanel(AKTIVT_PANEL_ID, grunnInfo, traad, oppgaveId));
    }

    private void clearLokaleParameterVerdier() {
        oppgaveIdFraParametere = none();
        henvendelsesIdFraParametere = none();
        besvaresFraParametere = false;
    }

    @RunOnEvents({NY_DIALOG_LENKE_VALGT})
    public void visNyDialogPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new NyDialogPanel(AKTIVT_PANEL_ID, grunnInfo));
        target.add(aktivtPanel);
        TextArea textarea = in((MarkupContainer) aktivtPanel).findComponent(TextArea.class);
        target.focusComponent(textarea);
    }

    @RunOnEvents({LEGG_TILBAKE_FERDIG, SVAR_AVBRUTT, NY_DIALOG_AVBRUTT})
    public void visVelgDialogPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new VelgDialogPanel(AKTIVT_PANEL_ID));
        target.add(aktivtPanel);
    }

    @RunOnEvents({Events.SporsmalOgSvar.SVAR_AVBRUTT, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT, Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER})
    public void unsetBesvartModus() {
        getSession().setAttribute(SessionParametere.SporsmalOgSvar.BESVARMODUS, null);
    }
}
