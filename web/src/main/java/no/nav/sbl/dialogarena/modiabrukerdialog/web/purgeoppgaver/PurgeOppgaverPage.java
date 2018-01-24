package no.nav.sbl.dialogarena.modiabrukerdialog.web.purgeoppgaver;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

import java.util.List;

import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class PurgeOppgaverPage extends BasePage {

    @Inject
    private PlukkOppgaveService plukkOppgaveService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public PurgeOppgaverPage(PageParameters pageParameters) {
        super(pageParameters);
        final IModel<Boolean> ferdig = Model.of(false);

        final Component ferdigLabel = new Label("ferdig", "Ferdig!!")
                .setOutputMarkupPlaceholderTag(true)
                .add(visibleIf(ferdig));


        Form form = new Form("form");
        form.add(new IndicatingAjaxButtonWithImageUrl("purge", "../img/ajaxloader/svart/loader_svart_48.gif") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                for (Temagruppe temagruppe : Temagruppe.PLUKKBARE) {
                    purgeTemagruppe(temagruppe);
                }
                ferdig.setObject(true);
                target.add(ferdigLabel);
            }
        });
        form.add(ferdigLabel);

        add(new Label("navIdent", getSubjectHandler().getUid()));
        add(form);
    }

    private void purgeTemagruppe(Temagruppe temagruppe) {
        String saksbehandlersValgteEnhet = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();
        List<Oppgave> oppgaver = plukkOppgaveService.plukkOppgaver(temagruppe, saksbehandlersValgteEnhet);
        while (!oppgaver.isEmpty()) {
            for (Oppgave oppgave : oppgaver) {
                oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak(oppgave.oppgaveId, temagruppe, saksbehandlersValgteEnhet);
            }
            oppgaver = plukkOppgaveService.plukkOppgaver(temagruppe, saksbehandlersValgteEnhet);
        }
    }
}
