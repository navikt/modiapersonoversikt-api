package no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare;

import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareService;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareSporsmalDetaljer;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Henvendelse;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Sporsmal;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Svar;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.Henvendelsestype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.Henvendelsestype.SVAR;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private BesvareService service;

    private TidligereDialog tidligereDialog;
    private SporsmalDetaljer sporsmalDetaljer;

    public BesvareSporsmalPanel(String id, String oppgaveId, String fnr) {
        super(id);
        setOutputMarkupId(true);

        BesvareSporsmalDetaljer besvareSporsmalDetaljer;
        if (oppgaveId != null) {
            besvareSporsmalDetaljer = service.hentDetaljer(fnr, oppgaveId);
        } else {
            besvareSporsmalDetaljer = new BesvareSporsmalDetaljer();
            besvareSporsmalDetaljer.tema = "";
            besvareSporsmalDetaljer.svar = new Svar("1");
            besvareSporsmalDetaljer.sporsmal = new Sporsmal("", DateTime.now());
            besvareSporsmalDetaljer.tildligereDialog = emptyList();
        }
        tidligereDialog = new TidligereDialog("tidligere-dialog", new ArrayList<>(besvareSporsmalDetaljer.tildligereDialog));
        sporsmalDetaljer = new SporsmalDetaljer("sporsmal", new CompoundPropertyModel<>(besvareSporsmalDetaljer.sporsmal));
        add(
                new Label("tema", besvareSporsmalDetaljer.tema),
                new SvarForm("svar", new CompoundPropertyModel<>(besvareSporsmalDetaljer.svar)),
                sporsmalDetaljer,
                tidligereDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BesvareSporsmalPanel.class, "besvare.js")));
    }

    private final class SvarForm extends Form<Svar> {

        public SvarForm(String id, final CompoundPropertyModel<Svar> modell) {
            super(id, modell);
            setOutputMarkupId(true);

            final TextArea<String> fritekst = new TextArea<>("fritekst");
            fritekst.setRequired(true);
            add(
                    new FeedbackPanel("feedback"),
                    fritekst,
                    new AjaxSubmitLink("send") {

                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            service.besvareSporsmal(modell.getObject());

                            Sporsmal sporsmal = (Sporsmal) sporsmalDetaljer.getDefaultModelObject();
                            tidligereDialog.prependHenvendelse(new Henvendelse(SPORSMAL, sporsmal.getSendtDato(), sporsmal.getFritekst()));

                            Svar svar = modell.getObject();
                            tidligereDialog.prependHenvendelse(new Henvendelse(SVAR, DateTime.now(), svar.fritekst));

                            sporsmalDetaljer.setVisible(false);
                            SvarForm.this.setVisible(false);

                            target.add(BesvareSporsmalPanel.this);
                        }

                        @Override
                        protected void onError(AjaxRequestTarget target, Form<?> form) {
                            target.add(BesvareSporsmalPanel.this);
                        }
                    }
            );
        }
    }

    private static class SporsmalDetaljer extends WebMarkupContainer {

        public SporsmalDetaljer(String id, CompoundPropertyModel<Sporsmal> modell) {
            super(id, modell);
            add(new Label("sendtDatoAsString"), new Label("fritekst"));
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

        public void prependHenvendelse(Henvendelse henvendelse) {
            List<Henvendelse> henvendelser = getModelObject();
            henvendelser.add(0, henvendelse);
            setModelObject(henvendelser);
        }
    }

}