package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.InnboksModell;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class AlleMeldingerPanel extends Panel implements IHeaderContributor {

    private final InnboksModell innboksModell;

    private Meldingsliste meldingsliste;

    public AlleMeldingerPanel(String id, InnboksModell innboksModell) {
        super(id, innboksModell);
        setOutputMarkupId(true);
        this.innboksModell = innboksModell;
        this.meldingsliste = new Meldingsliste("meldinger");
        add(meldingsliste);
    }

    @RunOnEvents(Innboks.VALGT_MELDING_EVENT)
    public void valgteMelding(AjaxRequestTarget target, ValgtMeldingOppdatert valgtMeldingOppdatert) {
        for (int i = 0; i < meldingsliste.size(); i++) {
            ListItem<MeldingVM> item = (ListItem<MeldingVM>) meldingsliste.get(i);
            if (item.getModelObject() == valgtMeldingOppdatert.forrige) {
                target.add(item);
            }

            if (item.getModelObject() == valgtMeldingOppdatert.valgt) {
                target.add(item);
                if (valgtMeldingOppdatert.scroll) {
                    target.appendJavaScript("$('#" + getMarkupId() + "').scrollTo(0, '#" + item.getMarkupId() + "');");
                }
            }
        }
    }

    private class Meldingsliste extends PropertyListView<MeldingVM> {

        public Meldingsliste(final String id) {
            super(id);
            setOutputMarkupId(true);
        }

        @Override
        protected void populateItem(final ListItem<MeldingVM> item) {
            item.add(new MeldingsHeader("header"));
            item.add(new Label("fritekst"));
            IModel<Boolean> erValgtMelding = innboksModell.erValgtMelding(item.getModelObject());
            item.add(hasCssClassIf("valgt", erValgtMelding));

            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    MeldingVM forrige = innboksModell.getInnboksVM().getValgtMelding();
                    MeldingVM valgte = item.getModelObject();
                    innboksModell.getInnboksVM().setValgtMelding(valgte);
                    send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Innboks.VALGT_MELDING_EVENT, new ValgtMeldingOppdatert(forrige, valgte, false)));
                }
            });
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptReferenceHeaderItem.forReference(new JavaScriptResourceReference(AlleMeldingerPanel.class, "../javascripts/scrollto.js")));
    }
}
