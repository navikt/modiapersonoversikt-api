package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.modig.lang.collections.IterUtils;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

public class Innboks extends Panel {

    @Inject
    private MeldingService service;

    private List<Melding> alleMeldinger;
    private IModel<Melding> melding;
    private IModel<List<Melding>> traad;
    private String aktorId;

    public Innboks(String id, Melding melding, String aktorId) {
        super(id);
        this.melding = new CompoundPropertyModel<>(melding);
        this.aktorId = aktorId;
        WebMarkupContainer liste = new WebMarkupContainer("liste");
        liste.setOutputMarkupId(true);
        this.traad = new CompoundPropertyModel<>(hentTraad(melding.traadId));
        liste.add(new MeldingerListe("meldinger", new AlleMeldingerModell()));
        add(liste, new Traad("traad"));
    }

    private class AlleMeldingerModell extends LoadableDetachableModel<List<Melding>> {
        @Override
        protected List<Melding> load() {
            alleMeldinger = service.hentAlleMeldinger(aktorId);
            return alleMeldinger;
        }
    }

    private class MeldingerListe extends PropertyListView<Melding> {

        public MeldingerListe(String id, IModel<? extends List<Melding>> sporsmalOgSvar) {
            super(id, sporsmalOgSvar);
        }

        @Override
        protected void populateItem(final ListItem<Melding> item) {
            item.add(new Label("type"));
            item.add(new Label("fritekst"));
            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    melding.setObject(item.getModelObject());
                    traad.setObject(hentTraad(item.getModelObject().traadId));
                    target.add(getPage());
                }
            });
        }

    }

    private class Traad extends WebMarkupContainer {

        public Traad(String id) {
            super(id);
        }

    }

    private List<Melding> hentTraad(String traadId) {
        return IterUtils.on(alleMeldinger).filter(harTraadId(traadId)).collect(byBehandlingsIdAsc);
    }

    private Predicate<Melding> harTraadId(final String traadId) {
        return new Predicate<Melding>() {
            @Override
            public boolean evaluate(Melding melding) {
                return traadId.equals(melding.traadId);
            }
        };
    }

    private Comparator<Melding> byBehandlingsIdAsc = new Comparator<Melding>() {
        public int compare(Melding o1, Melding o2) {
            return Long.valueOf(o1.id).compareTo(Long.valueOf(o2.id));
        }
    };

}
