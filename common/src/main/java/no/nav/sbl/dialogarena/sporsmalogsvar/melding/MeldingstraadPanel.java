package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.HarMeldingsliste;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.MeldingslisteDelegat;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class MeldingstraadPanel extends Panel implements HarMeldingsliste {

    private final MeldingslisteDelegat delegat;

    public MeldingstraadPanel(String id, MeldingslisteDelegat delegat) {
        super(id);
        this.delegat = delegat;
        setOutputMarkupId(true);
        add(new Traad("traad"));
    }

    @Override
    public void valgteMelding(AjaxRequestTarget target, MeldingVM forrigeMelding, MeldingVM valgteMelding, boolean oppdaterScroll) {
        target.add(this);
    }

    private class Traad extends PropertyListView<MeldingVM> {

        public Traad(String id) {
            super(id);
            setOutputMarkupId(true);
        }

        @Override
        protected void populateItem(final ListItem<MeldingVM> item) {
            item.setOutputMarkupId(true);
            item.add(new MeldingsHeader("header"));
            item.add(new Label("fritekst"));

            item.add(hasCssClassIf("valgt", delegat.erMeldingValgt(item.getModelObject())));
            item.add(hasCssClassIf("ekspandert", delegat.erMeldingValgt(item.getModelObject())));

            item.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    delegat.meldingValgt(target, item.getModelObject(), true);
                }
            });
        }
    }
}
