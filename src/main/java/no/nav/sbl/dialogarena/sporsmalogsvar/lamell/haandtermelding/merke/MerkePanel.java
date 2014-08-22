package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

public class MerkePanel extends AnimertPanel {

    @Inject
    private HenvendelseBehandlingService henvendelse;
    @Inject
    private GsakService gsak;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        Form form = new Form("form");
        final CheckBox checkBox = new CheckBox("kontorsperret", Model.of(false));
        form.add(checkBox);
        form.add(new AjaxSubmitLink("merk") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                henvendelse.merkSomKontorsperret(innboksVM.getValgtTraad());
                if (checkBox.getModelObject()) {
                    gsak.opprettGsakOppgave(new NyOppgave());
                    checkBox.setModelObject(false);
                }
                lukkPanel(target);
            }
        });

        AjaxLink<Void> avbrytLink = new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        };

        add(form, avbrytLink);
    }
}
