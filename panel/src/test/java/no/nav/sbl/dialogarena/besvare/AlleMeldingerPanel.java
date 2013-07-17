package no.nav.sbl.dialogarena.besvare;

import java.util.List;
import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMelding;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class AlleMeldingerPanel extends Panel {

    @Inject
    private SporsmalOgSvarPortType webservice;

    public AlleMeldingerPanel(String id) {
        super(id);
        WebMarkupContainer liste = new WebMarkupContainer("liste");
        liste.setOutputMarkupId(true);
        liste.add(new MeldingerListe("meldinger", new AlleMeldinger()));
        add(liste);
    }

    private class AlleMeldinger extends LoadableDetachableModel<List<WSMelding>> {
        @Override
        protected List<WSMelding> load() {
            return webservice.hentSporsmalOgSvarListe("28088834986");
        }
    }

    private class MeldingerListe extends PropertyListView<WSMelding> {

        public MeldingerListe(String id, IModel<? extends List<WSMelding>> sporsmalOgSvar) {
            super(id, sporsmalOgSvar);
        }

        @Override
        protected void populateItem(final ListItem<WSMelding> item) {
            item.add(new Label("type"));
            item.add(new Label("fritekst"));
//            item.add(new AjaxEventBehavior("onclick") {
//                @Override
//                protected void onEvent(AjaxRequestTarget target) {
//                    WSMelding melding = item.getModelObject();
//                    modell.setObject();
//                    WSSporsmalOgSvar wsSporsmalOgSvar = item.getModelObject();
//                    modell.setObject(
//                            new BesvareSporsmalVM(wsSporsmalOgSvar.getBehandlingsId(), wsSporsmalOgSvar.getTema(), wsSporsmalOgSvar.getSporsmal(),
//                                    wsSporsmalOgSvar.getSvar(), wsSporsmalOgSvar.getOpprettet().toLocalDate(), wsSporsmalOgSvar.isSensitiv()));
//                    target.add(getPage());
//                }
//            });
        }

    }

//    private static class Melding {
//
//        enum MeldingsType {SPORSMAL, SVAR}
//
//        MeldingsType type;
//        String tema, overskrift, fritekst;
//        LocalDate opprettet;
//
//        Melding(MeldingsType type, String tema, String overskrift, LocalDate opprettet, String friteskt) {
//            this.type = type;
//            this.tema = tema;
//            this.overskrift = overskrift;
//            this.opprettet = opprettet;
//            this.fritekst = friteskt;
//        }
//    }

}
