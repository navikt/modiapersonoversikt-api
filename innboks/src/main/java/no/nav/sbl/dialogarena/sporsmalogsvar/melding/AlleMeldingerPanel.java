package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.HarMeldingsliste;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.InnboksModell;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.MeldingslisteDelegat;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class AlleMeldingerPanel extends Panel implements IHeaderContributor, HarMeldingsliste {

    public static final String INGEN_MELDINGER_ID = "ingen-meldinger";

    private Meldingsliste meldingsliste;

    public AlleMeldingerPanel(String id, final InnboksModell innboksModell, MeldingslisteDelegat delegat) {
        super(id);
        setOutputMarkupId(true);

        add(new Label(INGEN_MELDINGER_ID, new StringResourceModel("ingen-meldinger", this, null))
                .add(visibleIf(innboksModell.ingenMeldinger())));

        this.meldingsliste = new Meldingsliste("meldinger", delegat);
        add(meldingsliste);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void valgteMelding(AjaxRequestTarget target, Optional<MeldingVM> forrigeMelding, MeldingVM valgteMelding, boolean oppdaterScroll) {
        for (ListItem<MeldingVM> item : on(meldingsliste).map(castTo(ListItem.class))) {
            if (forrigeMelding.isSome() && item.getModelObject() == forrigeMelding.get()) {
                target.add(item);
            }

            if (item.getModelObject() == valgteMelding) {
                target.add(item);
                if (oppdaterScroll) {
                    target.appendJavaScript("$('#" + getMarkupId() + "').scrollTo(0, '#" + item.getMarkupId() + "');");
                }
            }
        }
    }

    public static class Meldingsliste extends PropertyListView<MeldingVM> {
        private final MeldingslisteDelegat delegat;


        public Meldingsliste(final String id, MeldingslisteDelegat delegat) {
            super(id);
            setOutputMarkupId(true);
            this.delegat = delegat;
        }

        @Override
        protected void populateItem(final ListItem<MeldingVM> item) {
            item.add(new MeldingsHeader("header", item.getModel()));
            item.add(new Label("fritekst"));
            item.add(hasCssClassIf("valgt", delegat.erMeldingValgt(item.getModelObject())));

            item.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    delegat.meldingValgt(target, item.getModelObject(), false);
                }
            });
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptReferenceHeaderItem.forReference(new JavaScriptResourceReference(AlleMeldingerPanel.class, "../javascripts/scrollto.js")));
    }
}
