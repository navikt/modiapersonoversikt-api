package no.nav.sbl.dialogarena.besvare;

import no.nav.modig.wicket.events.components.AjaxEventSubmitButton;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.HenvendelseSporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.BesvarSporsmalRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentAlleSporsmalOgSvarRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.SporsmalOgSvar;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.List;


public class BesvareSporsmalPanel extends GenericPanel<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(BesvareSporsmalPanel.class);

    @Inject
    private HenvendelseSporsmalOgSvarPortType webservice;

    public BesvareSporsmalPanel(String id) {
        super(id);
        add(new AttributeAppender("class", "visittkort"));
        add(new SporsmalOgSvarListe("alle-sporsmaal", new AlleSporsmalOgSvar()));
        add(new SvarForm("svar-form"));
    }


    class AlleSporsmalOgSvar extends LoadableDetachableModel<List<SporsmalOgSvar>> {
        @Override
        protected List<SporsmalOgSvar> load() {
            return webservice.hentAlleSporsmalOgSvar(new HentAlleSporsmalOgSvarRequest().withAktorId("12345678901")).getSporsmalOgSvar();
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

    private class SvarForm extends Form {
        private final TextArea<Object> textarea;

        public SvarForm(String id) {
            super(id);
            this.textarea = new TextArea<>("svartekst");
            add(textarea);
            add(new AjaxSubmitLink("submit") {
                @Override
                protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
                    webservice.besvarSporsmal(new BesvarSporsmalRequest().withSvar(textarea.getInput()));
                }
            });
        }
    }
}
