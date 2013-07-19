package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import java.util.List;
import no.nav.modig.lang.collections.IterUtils;
import no.nav.modig.wicket.conditional.ConditionalUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

public class Innboks extends Panel {

    private Melding valgtMelding;
    private final WebMarkupContainer detaljer;
    private final Meldingsdetaljer meldingsdetaljer;
    private ListItem<Melding> valgtItem;

    public Innboks(String id, Melding valgtMelding, List<Melding> meldinger) {
        super(id);
        this.valgtMelding = valgtMelding;
        WebMarkupContainer liste = new WebMarkupContainer("meldingsliste");
        liste.setOutputMarkupId(true);
        liste.add(new MeldingerListe("meldinger", meldinger));

        detaljer = new WebMarkupContainer("meldingsdetaljer");
        detaljer.setOutputMarkupId(true);
        meldingsdetaljer = new Meldingsdetaljer("meldinger", meldinger);
        detaljer.add(meldingsdetaljer);

        meldingsdetaljer.valgteMelding(valgtMelding);

        add(liste, detaljer);
    }

    private void valgteItem(ListItem<Melding> meldingsTtem) {
        this.valgtItem = meldingsTtem;
        meldingsdetaljer.valgteMelding(meldingsTtem.getModelObject());
    }

    private class MeldingerListe extends PropertyListView<Melding> {

        public MeldingerListe(String id, List<? extends Melding> sporsmalOgSvar) {
            super(id, sporsmalOgSvar);
        }

        @Override
        protected void populateItem(final ListItem<Melding> item) {
            item.add(new Label("type"));
            item.add(new Label("fritekst"));
            item.add(ConditionalUtils.hasCssClassIf("valgt", new AbstractReadOnlyModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return item == valgtItem || item.getModelObject() == valgtMelding;
                }
            }));

            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    ListItem<Melding> forrige = valgtItem;
                    Innboks.this.valgteItem(item);
                    target.add(item, detaljer);
                    if (forrige != null) {
                        target.add(forrige);
                    }
                }
            });
        }
    }

    private class Meldingsdetaljer extends PropertyListView<Melding> {

        List<? extends Melding> alle;

        public Meldingsdetaljer(String id, List<? extends Melding> sporsmalOgSvar) {
            super(id, sporsmalOgSvar);
            alle = sporsmalOgSvar;
        }

        public void valgteMelding(Melding melding) {
            if (melding != null) {
                this.setList(IterUtils.on(alle).filter(harTraadId(melding.traadId)).collect());
            }
        }

        @Override
        protected void populateItem(final ListItem<Melding> item) {
            item.add(new Label("type"));
            item.add(new Label("fritekst"));
        }
    }

    private Predicate<Melding> harTraadId(final String traadId) {
        return new Predicate<Melding>() {
            @Override
            public boolean evaluate(Melding melding) {
                return traadId.equals(melding.traadId);
            }
        };
    }

}
