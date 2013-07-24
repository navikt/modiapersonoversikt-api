package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import java.util.List;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class MeldingstraadPanel extends Panel {

    private final IModel<Melding> valgtMeldingModel;

    public MeldingstraadPanel(final String id, final IModel<Melding> valgtMeldingModel, final IModel<? extends List<? extends Melding>> model) {
        super(id, model);
        setOutputMarkupId(true);
        this.valgtMeldingModel = valgtMeldingModel;
        add(new Traad("melding", model));
    }

    @RunOnEvents(Innboks.VALGT_MELDING_EVENT)
    public void valgteMelding(AjaxRequestTarget target, Melding melding) {
        target.add(this);
    }

    private class Traad extends PropertyListView<Melding> {

        public Traad(String id, final IModel<? extends List<? extends Melding>> model) {
            super(id, model);
            setOutputMarkupId(true);
        }

        @Override
        protected void populateItem(final ListItem<Melding> item) {
            item.setOutputMarkupId(true);
            IModel<Boolean> valgt = new AbstractReadOnlyModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return item.getModelObject() == valgtMeldingModel.getObject();
                }
            };
            Label overskrift = new Label("overskrift");
            Label fritekst = new Label("fritekst");

            item.add(overskrift, fritekst);

            item.add(hasCssClassIf("valgt", valgt));
            item.add(hasCssClassIf("expanded", valgt));
        }
    }
}
