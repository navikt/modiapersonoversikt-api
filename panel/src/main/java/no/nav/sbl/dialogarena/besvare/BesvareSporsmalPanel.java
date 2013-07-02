package no.nav.sbl.dialogarena.besvare;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.HenvendelseSporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentAlleSporsmalOgSvarRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.SporsmalOgSvar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;

import java.util.List;


public class BesvareSporsmalPanel extends GenericPanel<Void> {

    @Inject
    private HenvendelseSporsmalOgSvarPortType webservice;

    public BesvareSporsmalPanel(String id) {
        super(id);
        add(new SporsmalOgSvarListe("alle-sporsmaal", new AlleSporsmalOgSvar()));
    }


    class AlleSporsmalOgSvar extends LoadableDetachableModel<List<SporsmalOgSvar>> {
        @Override
        protected List<SporsmalOgSvar> load() {
            return webservice.hentAlleSporsmalOgSvar(new HentAlleSporsmalOgSvarRequest().withAktorId("balle")).getSporsmalOgSvar();
        }
    }

    static class SporsmalOgSvarListe extends PropertyListView<SporsmalOgSvar> {

        public SporsmalOgSvarListe(String id, IModel<? extends List<SporsmalOgSvar>> sporsmalOgSvar) {
            super(id, sporsmalOgSvar);
        }

        @Override
        protected void populateItem(ListItem<SporsmalOgSvar> item) {
            item.add(new Label("sporsmal"));
        }

    }

}
