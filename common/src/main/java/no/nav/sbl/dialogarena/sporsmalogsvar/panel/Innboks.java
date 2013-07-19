package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import no.nav.modig.lang.collections.IterUtils;
import no.nav.modig.wicket.conditional.ConditionalUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import static java.util.Collections.emptyList;

public class Innboks extends Panel {

    @Inject
    MeldingService service;

    private Melding valgtMelding;
    private List<Melding> alleMeldinger;
    private final WebMarkupContainer detaljer;
    private final Meldingsdetaljer meldingsdetaljer;

    public Innboks(String id, String fodselsnr) {
        super(id);
        alleMeldinger = service.hentAlleMeldinger(fodselsnr);
        valgtMelding = alleMeldinger.isEmpty() ? null : alleMeldinger.get(0);
        WebMarkupContainer liste = new WebMarkupContainer("meldingsliste");
        liste.setOutputMarkupId(true);
        liste.add(new MeldingerListe("meldinger"));

        detaljer = new WebMarkupContainer("meldingsdetaljer");
        detaljer.setOutputMarkupId(true);
        meldingsdetaljer = new Meldingsdetaljer("meldinger");
        meldingsdetaljer.oppdaterView();
        detaljer.add(meldingsdetaljer);

        add(liste, detaljer);
    }

    private class MeldingerListe extends PropertyListView<Melding> {

        ListItem<Melding> valgtItem;

        public MeldingerListe(String id) {
            super(id, alleMeldinger);
        }

        @Override
        protected void populateItem(final ListItem<Melding> item) {
            item.add(new Label("type"));
            item.add(new Label("fritekst"));
            item.add(ConditionalUtils.hasCssClassIf("valgt", new AbstractReadOnlyModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return item.getModelObject() == valgtMelding;
                }
            }));
            if (item.getModelObject() == valgtMelding) {
                valgtItem = item;
            }

            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    ListItem<Melding> forrige = valgtItem;
                    valgteItem(item);
                    target.add(item, detaljer);
                    if (forrige != null) {
                        target.add(forrige);
                    }
                }
            });
        }

        private void valgteItem(ListItem<Melding> meldingsItem) {
            valgtItem = meldingsItem;
            Innboks.this.valgtMelding = meldingsItem.getModelObject();
            meldingsdetaljer.oppdaterView();
        }
    }

    private class Meldingsdetaljer extends PropertyListView<Melding> {

        public Meldingsdetaljer(String id) {
            super(id, alleMeldinger);
        }

        public void oppdaterView() {
            this.setList(valgtTraad());
        }

        public List<Melding> valgtTraad() {
            if (valgtMelding != null) {
                return IterUtils.on(alleMeldinger).filter(harTraadId(valgtMelding.traadId)).collect(nyesteNederst);
            } else {
                return emptyList();
            }
        }

        @Override
        protected void populateItem(final ListItem<Melding> item) {
            item.add(new Label("type"));
            item.add(new Label("fritekst"));
            item.add(ConditionalUtils.hasCssClassIf("valgt", new AbstractReadOnlyModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return item.getModelObject() == valgtMelding;
                }
            }));
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

    private Comparator<Melding> nyesteNederst = new Comparator<Melding>() {
        public int compare(Melding o1, Melding o2) {
            return Long.valueOf(o1.id, Character.MAX_RADIX).compareTo(Long.valueOf(o2.id, Character.MAX_RADIX));
        }
    };


}
