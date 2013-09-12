package no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare;

import no.nav.sbl.dialogarena.sporsmalogsvar.service.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import java.util.List;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private BesvareService service;

    public BesvareSporsmalPanel(String id, String oppgaveId, String fnr) {
        super(id);
        setOutputMarkupId(true);

//        BesvareSporsmalDetaljer besvareSporsmalDetaljer = service.hentDetaljer(fnr, oppgaveId);
        BesvareSporsmalDetaljer besvareSporsmalDetaljer = new BesvareSporsmalDetaljer();
        besvareSporsmalDetaljer.sporsmal = new Sporsmal();
        besvareSporsmalDetaljer.svar = new Svar("1");
        besvareSporsmalDetaljer.tema = "tema";
        add(
                new Label("tema", besvareSporsmalDetaljer.tema),
                new SvarForm("svar", new CompoundPropertyModel<>(besvareSporsmalDetaljer.svar)),
                new SporsmalDetaljer("sporsmal", new CompoundPropertyModel<>(besvareSporsmalDetaljer.sporsmal)),
                new TidligereDialog("tidligere-dialog", besvareSporsmalDetaljer.tildligereDialog));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BesvareSporsmalPanel.class, "besvare.js")));
    }

    private final class SvarForm extends Form<Svar> {

        public SvarForm(String id, final CompoundPropertyModel<Svar> modell) {
            super(id, modell);
            add(
                    new TextArea<>("fritekst").setRequired(true),
                    new AjaxSubmitLink("send") {
                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            service.besvareSporsmal(modell.getObject());
                            target.add(BesvareSporsmalPanel.this);
                        }
                    }
            );
        }
    }

    private static class SporsmalDetaljer extends WebMarkupContainer {

        public SporsmalDetaljer(String id, CompoundPropertyModel<Sporsmal> modell) {
            super(id, modell);
            add(new Label("sendtDato"), new Label("fritekst"));
        }
    }

    private static class TidligereDialog extends PropertyListView<Henvendelse> {

        public TidligereDialog(String id, List<? extends Henvendelse> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(ListItem<Henvendelse> item) {
            item.add(new Label("sendtDato"), new Label("overskrift"), new Label("fritekst"));
        }
    }

}