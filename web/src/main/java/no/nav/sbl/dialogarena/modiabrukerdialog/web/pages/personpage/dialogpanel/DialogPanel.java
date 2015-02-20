package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Personnavn;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Bruker;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.velgdialogpanel.VelgDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.OppgavetilordningFeilet;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.SVAR_AVBRUTT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.FORTSETTDIALOGMODUS;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.OPPGAVEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SAMTALEREFERAT_TELEFON;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel.LEGG_TILBAKE_FERDIG;

public class DialogPanel extends Panel {

    public static final String NY_DIALOG_LENKE_VALGT = "dialogpanel.ny.dialog.lenke.valgt";
    public static final String NY_DIALOG_AVBRUTT = "dialogpanel.ny.dialog.avbrutt";
    private static final String AKTIVT_PANEL_ID = "aktivtPanel";

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

    private Component aktivtPanel;
    private OppgavetilordningFeilet oppgavetilordningFeiletModal;
    private GrunnInfo grunnInfo;
    private Optional<String> oppgaveIdFraParametere = none();
    private Optional<String> henvendelsesIdFraParametere = none();
    private Boolean fortsettDialogModusFraParametere = false;

    public DialogPanel(String id, String fnr) {
        super(id);
        grunnInfo = new GrunnInfo(
                hentBrukerInfo(fnr),
                hentSaksbehandlerInfo());
        settOppVerdierFraParameterePaaSession();
        aktivtPanel = new NyDialogPanel(AKTIVT_PANEL_ID, grunnInfo);
        oppgavetilordningFeiletModal = new OppgavetilordningFeilet("oppgavetilordningModal");

        add(aktivtPanel, oppgavetilordningFeiletModal);

        settOppRiktigMeldingPanel();
    }

    private void settOppVerdierFraParameterePaaSession() {
        String henvendelsesId = (String) getSession().getAttribute(HENVENDELSEID);
        if (henvendelsesId != null && !henvendelsesId.isEmpty()) {
            henvendelsesIdFraParametere = optional(henvendelsesId);
        }
        String oppgaveId = (String) getSession().getAttribute(OPPGAVEID);
        if (oppgaveId != null && !oppgaveId.isEmpty()) {
            oppgaveIdFraParametere = optional(oppgaveId);
        }
        String fortsettDialogModus = (String) getSession().getAttribute(FORTSETTDIALOGMODUS);
        if (fortsettDialogModus != null && fortsettDialogModus.equals(TRUE.toString())) {
            fortsettDialogModusFraParametere = true;
        }
    }

    private Bruker hentBrukerInfo(String fnr) {
        try {
            Personnavn personnavn = personKjerneinfoServiceBi.hentKjerneinformasjon(new HentKjerneinformasjonRequest(fnr))
                    .getPerson().getPersonfakta().getPersonnavn();

            return new Bruker(fnr, personnavn.getFornavn(), personnavn.getEtternavn());
        } catch (Exception e) {
            return new Bruker(fnr, "", "");
        }
    }

    private Saksbehandler hentSaksbehandlerInfo() {
        Optional<Attributes> attributes = ldapService.hentSaksbehandler(getSubjectHandler().getUid());

        if (!attributes.isSome()) {
            return new Saksbehandler(getSubjectHandler().getUid(), saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet(), "", "");
        }

        try {
            return new Saksbehandler(
                    getSubjectHandler().getUid(),
                    saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet(),
                    optional((String) attributes.get().get("givenname").get()).getOrElse(""),
                    optional((String) attributes.get().get("sn").get()).getOrElse(""));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private void settOppRiktigMeldingPanel() {
        if (henvendelsesIdFraParametere.isSome() && oppgaveIdFraParametere.isSome()) {
            if (fortsettDialogModusFraParametere) {
                List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.bruker.fnr, henvendelsesIdFraParametere.get());
                if (!traad.isEmpty() && !erEnkeltstaaendeSamtalereferat(traad)) {
                    erstattDialogPanelMedFortsettDialogPanel(traad, oppgaveIdFraParametere);
                    clearLokaleParameterVerdier();
                }
            } else {
                aktivtPanel = aktivtPanel.replaceWith(new VelgDialogPanel(AKTIVT_PANEL_ID));
            }
        }
    }

    @RunOnEvents(SVAR_PAA_MELDING)
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
                    String sporsmalOppgaveId = traad.get(0).oppgaveId;
                    oppgaveBehandlingService.tilordneOppgaveIGsak(sporsmalOppgaveId);
                    oppgaveId = optional(sporsmalOppgaveId);
                } catch (OppgaveBehandlingService.FikkIkkeTilordnet fikkIkkeTilordnet) {
                    oppgavetilordningFeiletModal.vis(target);
                }
            }
        }
        erstattDialogPanelMedFortsettDialogPanel(traad, oppgaveId);
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
        fortsettDialogModusFraParametere = false;
    }

    @RunOnEvents({NY_DIALOG_LENKE_VALGT})
    public void visNyDialogPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new NyDialogPanel(AKTIVT_PANEL_ID, grunnInfo));
        target.add(aktivtPanel);
    }

    @RunOnEvents({LEGG_TILBAKE_FERDIG, SVAR_AVBRUTT, NY_DIALOG_AVBRUTT})
    public void visVelgDialogPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new VelgDialogPanel(AKTIVT_PANEL_ID));
        target.add(aktivtPanel);
    }
}
