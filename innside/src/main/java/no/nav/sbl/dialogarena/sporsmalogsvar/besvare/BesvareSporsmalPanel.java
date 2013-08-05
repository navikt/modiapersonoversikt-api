package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import java.util.List;
import javax.inject.Inject;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Svar;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.tema.Temastruktur;
import no.nav.sbl.dialogarena.sporsmalogsvar.tema.TemastrukturVelger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import static java.util.Arrays.asList;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private MeldingService service;

    public static final String SPORSMAL_OPPDATERT = "hendelser.sporsmal_oppdatert";
    private final FeedbackPanel feedbackPanel;
    private List<Temastruktur> temastrukturListe = asList(
            new Temastruktur("Barnetrygd",  "EØS", "Informasjon", "Klage/anke", "Ordinær", "Tilbakebetaling/tilbakekreving EØS", "Tilbakebetaling/tilbakekreving",
                    "Utbetaling", "Utvidet", "Vedtak", "Tilbakekreving - Klage", "Tilbakekreving - Anke", "Tilbakekreving - Omgjøring"),
            new Temastruktur("Generell", "Veteransak"),
            new Temastruktur("Kontantstøtte", "Veteransak", "Adopsjon", "EU", "Informasjon", "Klage/anke", "Ordinær", "Tilbakebetaling/tilbakekreving EØS",
                    "Tilbakebetaling/tilbakekreving", "Utbetaling", "Vedtak", "Tilbakekreving - Klage", "Tilbakekreving - Anke", "Tilbakekreving - Omgjøring"),
            new Temastruktur("Pensjon", "AFP", "AFP Privat", "Alderspensjon", "Barnepensjon", "Familiepleier", "Gammel yrkesskade", "Gjenlevende ektefelle",
                    "Krigspensjon", "Omsorgspoeng", "Uførepensjon", "Veteransak"),
            new Temastruktur("Ukjent"));

    public BesvareSporsmalPanel(String id, BesvareModell model) {
        super(id, model);
        setOutputMarkupId(true);
        feedbackPanel = new FeedbackPanel("feedback");
        add(
                feedbackPanel,
                new SporsmalPanel("sporsmal", model),
                new SvarForm("svar", model));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forReference(
                new CssResourceReference(BesvareSporsmalPanel.class, "../stylesheets/besvare.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BesvareSporsmalPanel.class, "textarea.js")));
        response.render(OnDomReadyHeaderItem.forScript("textarea();"));
    }

    private final class SvarForm extends Form<SporsmalOgSvarVM> {

        public SvarForm(String id, final BesvareModell model) {
            super(id, model);
            add(
                    new TextField<>("svar.overskrift"),
                    new TextArea<>("svar.fritekst"),
                    new CheckBox("svar.sensitiv", Model.of(Boolean.FALSE)),
                    new TextField<>("svar.tema"),
                    new TemastrukturVelger("tema-velger", temastrukturListe),
                    new AjaxSubmitLink("send") {
                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            MeldingVM svar = model.getSvar();
                            service.besvar(new Svar().withId(svar.getId()).withOverskrift(svar.getOverskrift()).withFritekst(svar.getFritekst()).withTema(svar.getTema()));
                            model.nullstill();
                            info("Svaret er sendt.");
                            send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Innboks.MELDINGER_OPPDATERT));
                            send(getPage(), Broadcast.BREADTH, new NamedEventPayload(SPORSMAL_OPPDATERT));
                            target.add(BesvareSporsmalPanel.this);
                        }
                    },
                    new AjaxLink<Void>("avbryt") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            model.nullstill();
                            send(getPage(), Broadcast.BREADTH, SPORSMAL_OPPDATERT);
                        }
                    });
        }
    }

}
