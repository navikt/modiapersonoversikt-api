package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.referatpanel.ReferatPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel.SvarPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.OppgavetilordningFeilet;
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
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DialogPanel extends Panel {

    private static final String AKTIVT_PANEL_ID = "aktivtPanel";

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;

    private Component aktivtPanel;
    private OppgavetilordningFeilet oppgavetilordningFeiletModal;
    private GrunnInfo grunnInfo;

    public DialogPanel(String id, String fnr) {
        super(id);
        settOppGrunnInfo(fnr);

        aktivtPanel = new ReferatPanel(AKTIVT_PANEL_ID, grunnInfo);
        oppgavetilordningFeiletModal = new OppgavetilordningFeilet("oppgavetilordningModal");

        add(aktivtPanel, oppgavetilordningFeiletModal);

        settOppRiktigMeldingPanel();
    }

    private void settOppGrunnInfo(String fnr) {
        String fornavn = getFornavn(fnr);
        grunnInfo = new GrunnInfo(fnr, fornavn);
    }

    private String getFornavn(String fnr) {
        String fodselsnummer;
        if (fnr == null) {
            fodselsnummer = "";
        } else {
            fodselsnummer = fnr.replaceAll("[^\\d]", "");
        }
        HentKjerneinformasjonRequest request = new HentKjerneinformasjonRequest(fodselsnummer);
        HentKjerneinformasjonResponse response = personKjerneinfoServiceBi.hentKjerneinformasjon(request);
        if(response != null && response.getPerson() != null && response.getPerson().getPersonfakta() != null
                && response.getPerson().getPersonfakta().getPersonnavn() != null){
            return response.getPerson().getPersonfakta().getPersonnavn().getFornavn();
        } else {
            return null;
        }
    }

    private void settOppRiktigMeldingPanel() {
        String henvendelseId = (String) getSession().getAttribute(HENVENDELSEID);
        String oppgaveId = (String) getSession().getAttribute(OPPGAVEID);

        if (isNotBlank(henvendelseId) && isNotBlank(oppgaveId)) {
            List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.fnr, henvendelseId);
            if (!traad.isEmpty() && !erEnkeltstaaendeSamtalereferat(traad)) {
                erstattReferatPanelMedSvarPanel(traad, optional(oppgaveId));
            }
        }
    }

    private void erstattReferatPanelMedSvarPanel(List<Melding> traad, Optional<String> oppgaveId) {
        aktivtPanel = aktivtPanel.replaceWith(new SvarPanel(AKTIVT_PANEL_ID, grunnInfo, traad, oppgaveId));
    }

    private boolean erEnkeltstaaendeSamtalereferat(List<Melding> traad) {
        List<Meldingstype> samtalereferat = asList(SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON);
        return traad.size() == 1 && samtalereferat.contains(traad.get(0).meldingstype);
    }

    @RunOnEvents(SVAR_PAA_MELDING)
    public void visSvarPanelBasertPaaTraadId(AjaxRequestTarget target, String traadId) {
        List<Melding> traad = henvendelseUtsendingService.hentTraad(grunnInfo.fnr, traadId);
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

    @RunOnEvents({KVITTERING_VIST, LEGG_TILBAKE_FERDIG, Events.SporsmalOgSvar.SVAR_AVBRUTT})
    public void visReferatPanel(AjaxRequestTarget target) {
        aktivtPanel = aktivtPanel.replaceWith(new ReferatPanel(AKTIVT_PANEL_ID, grunnInfo));
        target.add(aktivtPanel);
    }

    public GrunnInfo getGrunnInfo() {
        return grunnInfo;
    }
}
