package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Personnavn;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Bruker;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.OppgavetilordningFeilet;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.SVAR_AVBRUTT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.OPPGAVEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Bruker.FALLBACK_FORNAVN;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel.KVITTERING_VIST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel.LEGG_TILBAKE_FERDIG;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DialogPanel extends Panel {

    private static final String AKTIVT_PANEL_ID = "aktivtPanel";

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    @Inject
    private AnsattService ansattService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private Component aktivtPanel;
    private OppgavetilordningFeilet oppgavetilordningFeiletModal;
    private GrunnInfo grunnInfo;

    public DialogPanel(String id, String fnr) {
        super(id);

        grunnInfo = new GrunnInfo(
                hentBrukerInfo(fnr).getOrElse(new Bruker(fnr, FALLBACK_FORNAVN, "")),
                hentSaksbehandlerInfo());
        aktivtPanel = new NyDialogPanel(AKTIVT_PANEL_ID, grunnInfo);
        oppgavetilordningFeiletModal = new OppgavetilordningFeilet("oppgavetilordningModal");

        add(aktivtPanel, oppgavetilordningFeiletModal);

        settOppRiktigMeldingPanel();
    }

    private Optional<Bruker> hentBrukerInfo(String fnr) {
        try {
            Personnavn personnavn = personKjerneinfoServiceBi.hentKjerneinformasjon(new HentKjerneinformasjonRequest(fnr))
                    .getPerson().getPersonfakta().getPersonnavn();

            return optional(new Bruker(fnr, personnavn.getFornavn(), personnavn.getEtternavn()));
        } catch (Exception e) {
            return none();
        }
    }

    private Saksbehandler hentSaksbehandlerInfo() {
        return new Saksbehandler(
                getSubjectHandler().getUid(),
                saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet(),
                ansattService.hentAnsattNavn(getSubjectHandler().getUid()));
    }

    private void settOppRiktigMeldingPanel() {
        String henvendelseId = (String) getSession().getAttribute(HENVENDELSEID);
        String oppgaveId = (String) getSession().getAttribute(OPPGAVEID);

        if (isNotBlank(henvendelseId) && isNotBlank(oppgaveId)) {
            List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.bruker.fnr, henvendelseId);
            if (!traad.isEmpty() && !erEnkeltstaaendeSamtalereferat(traad)) {
                erstattNyDialogPanelMedFortsettDialogPanel(traad, optional(oppgaveId));
            }
        }
    }

    private void erstattNyDialogPanelMedFortsettDialogPanel(List<Melding> traad, Optional<String> oppgaveId) {
        aktivtPanel = aktivtPanel.replaceWith(new FortsettDialogPanel(AKTIVT_PANEL_ID, grunnInfo, traad, oppgaveId));
    }

    private static boolean erEnkeltstaaendeSamtalereferat(List<Melding> traad) {
        List<Meldingstype> samtalereferat = asList(SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON);
        return traad.size() == 1 && samtalereferat.contains(traad.get(0).meldingstype);
    }

    private static boolean erEnkeltstaaendeSporsmalFraBruker(List<Melding> traad) {
        return traad.size() == 1 && traad.get(0).meldingstype == SPORSMAL_SKRIFTLIG;
    }

    @RunOnEvents(SVAR_PAA_MELDING)
    public void visFortsettDialogPanelBasertPaaTraadId(AjaxRequestTarget target, String traadId) {
        List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.bruker.fnr, traadId);
        Optional<String> oppgaveId = none();
        if (erEnkeltstaaendeSporsmalFraBruker(traad)) {
            try {
                String sporsmalOppgaveId = traad.get(0).oppgaveId;
                oppgaveBehandlingService.tilordneOppgaveIGsak(sporsmalOppgaveId);
                oppgaveId = optional(sporsmalOppgaveId);
            } catch (OppgaveBehandlingService.FikkIkkeTilordnet fikkIkkeTilordnet) {
                oppgavetilordningFeiletModal.vis(target);
            }
        }
        erstattNyDialogPanelMedFortsettDialogPanel(traad, oppgaveId);
        target.add(aktivtPanel);
    }

    @RunOnEvents({KVITTERING_VIST, LEGG_TILBAKE_FERDIG, SVAR_AVBRUTT})
    public void visNyDialogPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new NyDialogPanel(AKTIVT_PANEL_ID, grunnInfo));
        target.add(aktivtPanel);
    }
}
