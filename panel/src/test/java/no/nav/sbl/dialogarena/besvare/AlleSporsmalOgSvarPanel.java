package no.nav.sbl.dialogarena.besvare;

import java.util.List;
import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmalOgSvar;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class AlleSporsmalOgSvarPanel extends Panel {

    @Inject
    private SporsmalOgSvarPortType webservice;

    private IModel<BesvareSporsmalVM> modell;

    public AlleSporsmalOgSvarPanel(String id, IModel<BesvareSporsmalVM> modell) {
        super(id);
        this.modell = modell;
        WebMarkupContainer liste = new WebMarkupContainer("liste");
        liste.setOutputMarkupId(true);
        liste.add(new SporsmalOgSvarListe("sporsmal-med-svar", new AlleSporsmalOgSvar()));
        add(liste);
    }

    private class AlleSporsmalOgSvar extends LoadableDetachableModel<List<WSSporsmalOgSvar>> {
        @Override
        protected List<WSSporsmalOgSvar> load() {
            return webservice.hentSporsmalOgSvarListe("28088834986");
        }
    }

    private class SporsmalOgSvarListe extends PropertyListView<WSSporsmalOgSvar> {

        public SporsmalOgSvarListe(String id, IModel<? extends List<WSSporsmalOgSvar>> sporsmalOgSvar) {
            super(id, sporsmalOgSvar);
        }

        @Override
        protected void populateItem(final ListItem<WSSporsmalOgSvar> item) {
            item.add(new Label("sporsmal"));
            item.add(new Label("svar"));
            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    WSSporsmalOgSvar wsSporsmalOgSvar = item.getModelObject();
                    modell.setObject(
                            new BesvareSporsmalVM(wsSporsmalOgSvar.getBehandlingsId(), wsSporsmalOgSvar.getTema(), wsSporsmalOgSvar.getSporsmal(),
                                    wsSporsmalOgSvar.getSvar(), wsSporsmalOgSvar.getOpprettet().toLocalDate(), wsSporsmalOgSvar.isSensitiv()));
                    target.add(getPage());
                }
            });
        }

    }

}
