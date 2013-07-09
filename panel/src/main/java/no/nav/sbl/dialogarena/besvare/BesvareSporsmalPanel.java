package no.nav.sbl.dialogarena.besvare;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.HenvendelseSporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.BesvarSporsmalRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentAlleSporsmalOgSvarRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.SporsmalOgSvar;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

public class BesvareSporsmalPanel extends GenericPanel<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(BesvareSporsmalPanel.class);

    @Inject
    private HenvendelseSporsmalOgSvarPortType webservice;
    private final SporsmalOgSvarListe sporsmalMedSvar;
    private final WebMarkupContainer liste = new WebMarkupContainer("liste");

    public BesvareSporsmalPanel(String id) {
        super(id);
        sporsmalMedSvar = new SporsmalOgSvarListe("sporsmal-med-svar", new AlleSporsmalOgSvar());
        liste.setOutputMarkupId(true);
        liste.add(sporsmalMedSvar);
        add(liste);
        SporsmalOgSvar ss = this.sporsmalMedSvar.getModelObject().get(0);
        add(new SvarForm("svar-form", new CompoundPropertyModel<>(new Svar(ss.getBehandlingsId(), ss.getTema()))));
        add(new AttributeAppender("class", "visittkort"));
    }


    private class AlleSporsmalOgSvar extends LoadableDetachableModel<List<SporsmalOgSvar>> {
        @Override
        protected List<SporsmalOgSvar> load() {
            return webservice.hentAlleSporsmalOgSvar(new HentAlleSporsmalOgSvarRequest().withAktorId("12345678901")).getSporsmalOgSvar();
        }
    }

    private class SporsmalOgSvarListe extends PropertyListView<SporsmalOgSvar> {

        public SporsmalOgSvarListe(String id, IModel<? extends List<SporsmalOgSvar>> sporsmalOgSvar) {
            super(id, sporsmalOgSvar);
        }

        @Override
        protected void populateItem(ListItem<SporsmalOgSvar> item) {
            item.add(new Label("sporsmal"));
            item.add(new Label("svar"));
        }

    }

    private class SvarForm extends Form<Svar> {
        private final TextArea<Object> textarea;

        public SvarForm(String id, IModel<Svar> model) {
            super(id, model);
            this.textarea = new TextArea<>("svar");
            add(textarea);
            add(new AjaxSubmitLink("send") {
                @Override
                protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
                    Svar svar = getModelObject();
                    webservice.besvarSporsmal(new BesvarSporsmalRequest().withSvar(svar.svar).withBehandlingsId(svar.behandlingsid).withTema(svar.tema));
                    target.add(liste);
                }
            });
        }
    }

    private static class Svar implements Serializable {
        String svar, behandlingsid, tema;

        private Svar(String behandlingsid, String tema) {
            this.behandlingsid = behandlingsid;
            this.tema = tema;
        }
    }
}
