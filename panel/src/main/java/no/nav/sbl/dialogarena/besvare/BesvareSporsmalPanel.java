package no.nav.sbl.dialogarena.besvare;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSvar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class BesvareSporsmalPanel extends GenericPanel<Void> {

    @Inject
    private SporsmalOgSvarPortType webservice;
    private final WebMarkupContainer liste = new WebMarkupContainer("liste");

    public BesvareSporsmalPanel(String id) {
        super(id);
        final SporsmalOgSvarListe sporsmalMedSvar = new SporsmalOgSvarListe("sporsmal-med-svar", new AlleSporsmalOgSvar());
        liste.setOutputMarkupId(true);
        liste.add(sporsmalMedSvar);
        add(liste);
        WSSporsmalOgSvar ss = sporsmalMedSvar.getModelObject().get(0);
        add(new SvarForm("svar-form", new CompoundPropertyModel<>(new Svar(ss.getBehandlingsId(), ss.getTema()))));
        add(new AttributeAppender("class", "visittkort"));
    }


    private class AlleSporsmalOgSvar extends LoadableDetachableModel<List<WSSporsmalOgSvar>> {
        @Override
        protected List<WSSporsmalOgSvar> load() {
            return webservice.hentSporsmalOgSvarListe("28088834986");
        }
    }

    private static class SporsmalOgSvarListe extends PropertyListView<WSSporsmalOgSvar> {

        public SporsmalOgSvarListe(String id, IModel<? extends List<WSSporsmalOgSvar>> sporsmalOgSvar) {
            super(id, sporsmalOgSvar);
        }

        @Override
        protected void populateItem(ListItem<WSSporsmalOgSvar> item) {
            item.add(new Label("sporsmal"));
            item.add(new Label("svar"));
        }

    }

    private final class SvarForm extends Form<Svar> {
        private final TextArea<Object> textarea;

        public SvarForm(String id, IModel<Svar> model) {
            super(id, model);
            this.textarea = new TextArea<>("svar");
            add(textarea);
            add(new AjaxSubmitLink("send") {
                @Override
                protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
                    Svar svar = getModelObject();
                    webservice.besvarSporsmal(new WSSvar().withFritekst(svar.fritekst).withTema(svar.tema).withBehandlingsId(svar.behandlingsid));
                    target.add(liste);
                }
            });
        }
    }

    private static final class Svar implements Serializable {
        String fritekst, behandlingsid, tema;

        private Svar(String behandlingsid, String tema) {
            this.behandlingsid = behandlingsid;
            this.tema = tema;
        }
    }
}
