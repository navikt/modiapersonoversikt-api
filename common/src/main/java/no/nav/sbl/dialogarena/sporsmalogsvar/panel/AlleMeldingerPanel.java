package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import java.util.List;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class AlleMeldingerPanel extends Panel {

    private final IModel<Melding> valgtMeldingModel;

    public AlleMeldingerPanel(final String id, final IModel<Melding> valgtMeldingModel, final IModel<? extends List<? extends Melding>> model) {
        super(id, model);
        add(new Meldingsliste("melding", model));
        this.valgtMeldingModel = valgtMeldingModel;
        if (this.valgtMeldingModel.getObject() == null && !model.getObject().isEmpty()) {
            this.valgtMeldingModel.setObject(model.getObject().get(0));
        }
    }

    private class Meldingsliste extends PropertyListView<Melding> {

        private final Model<ListItem<Melding>> valgtItemModel;

        public Meldingsliste(final String id, final IModel<? extends List<? extends Melding>> model) {
            super(id, model);
            valgtItemModel = new Model<>();
        }

        private final class OverskriftModel extends AbstractReadOnlyModel<String> {

            private IModel<Melding> meldingModel;

            private OverskriftModel(IModel<Melding> meldingModel) {
                this.meldingModel = meldingModel;
            }

            @Override
            public String getObject() {
                Melding melding = meldingModel.getObject();
                String tekst;
                if (isBlank(melding.overskrift)) {
                    tekst = melding.erSporsmal() ? "Spørsmål om " + melding.tema : "Svar fra NAV";
                } else {
                    tekst = melding.overskrift;
                }
                return melding.erSvar() ? "Nav: " + tekst : tekst;
            }

            @Override
            public void detach() {
                meldingModel.detach();
            }
        }

        @Override
        protected void populateItem(final ListItem<Melding> item) {
            item.add(new Label("overskrift", new OverskriftModel(item.getModel())));
            item.add(new Label("opprettet"));
            item.add(new Label("fritekst"));
            item.add(hasCssClassIf("valgt", new AbstractReadOnlyModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return valgtMeldingModel.getObject() == item.getModelObject();
                }
            }));


            if (item.getModelObject() == valgtMeldingModel.getObject()) {
                valgtItemModel.setObject(item);
            }

            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    valgtMeldingModel.setObject(item.getModelObject());
                    ListItem<Melding> forrige = valgtItemModel.getObject();
                    valgtItemModel.setObject(item);
                    if (forrige != null) {
                        target.add(item, forrige);
                    }
                    send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Innboks.VALGT_MELDING_EVENT, item.getModelObject()));
                }
            });
        }
    }
}
