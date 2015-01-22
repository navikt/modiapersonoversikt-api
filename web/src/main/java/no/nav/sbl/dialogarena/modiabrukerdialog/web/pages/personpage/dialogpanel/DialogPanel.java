package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.OppgavetilordningFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.referatpanel.ReferatPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel.SvarPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.OPPGAVEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SAMTALEREFERAT_TELEFON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel.KVITTERING_VIST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel.LeggTilbakePanel.LEGG_TILBAKE_FERDIG;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel.SvarPanel.SVAR_AVBRUTT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DialogPanel extends Panel {

    private static final String AKTIVT_PANEL_ID = "aktivtPanel";

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;

    private Component aktivtPanel;
    private OppgavetilordningFeilet oppgavetilordningFeiletModal;
    private String fnr;

    public DialogPanel(String id, String fnr) {
        super(id);
        this.fnr = fnr;

        aktivtPanel = new ReferatPanel(AKTIVT_PANEL_ID, fnr);
        oppgavetilordningFeiletModal = new OppgavetilordningFeilet("oppgavetilordningModal");

        add(aktivtPanel, oppgavetilordningFeiletModal);

        settOppRiktigMeldingPanel();
    }

    private void settOppRiktigMeldingPanel() {
        String henvendelseId = (String) getSession().getAttribute(HENVENDELSEID);
        String oppgaveId = (String) getSession().getAttribute(OPPGAVEID);

        if (isNotBlank(henvendelseId) && isNotBlank(oppgaveId)) {
            List<Melding> traad = henvendelseUtsendingService.hentTraad(fnr, henvendelseId);
            if (!traad.isEmpty() && !erEnkeltstaaendeSamtalereferat(traad)) {
                erstattReferatPanelMedSvarPanel(traad, optional(oppgaveId));
            }
        }
    }

    private void erstattReferatPanelMedSvarPanel(List<Melding> traad, Optional<String> oppgaveId) {
        aktivtPanel = aktivtPanel.replaceWith(new SvarPanel(AKTIVT_PANEL_ID, fnr, traad, oppgaveId));
    }

    private boolean erEnkeltstaaendeSamtalereferat(List<Melding> traad) {
        List<Meldingstype> samtalereferat = asList(SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON);
        return traad.size() == 1 && samtalereferat.contains(traad.get(0).meldingstype);
    }

    @RunOnEvents(SVAR_PAA_MELDING)
    public void visSvarPanelBasertPaaTraadId(AjaxRequestTarget target, String traadId) {
        List<Melding> traad = henvendelseUtsendingService.hentTraad(fnr, traadId);
        Optional<String> oppgaveId = none();
        if (traad.size() <= 1) {
            try {
                String sporsmalOppgaveId = traad.get(0).oppgaveId;
                oppgaveBehandlingService.tilordneOppgaveIGsak(sporsmalOppgaveId);
                oppgaveId = optional(sporsmalOppgaveId);
            } catch (OppgaveBehandlingService.FikkIkkeTilordnet fikkIkkeTilordnet) {
                oppgavetilordningFeiletModal.vis(target);
            }
        }
        erstattReferatPanelMedSvarPanel(traad, oppgaveId);
        target.add(aktivtPanel);
    }

    @RunOnEvents({KVITTERING_VIST, LEGG_TILBAKE_FERDIG, SVAR_AVBRUTT})
    public void visReferatPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new ReferatPanel(AKTIVT_PANEL_ID, fnr));
        target.add(aktivtPanel);
    }
}
