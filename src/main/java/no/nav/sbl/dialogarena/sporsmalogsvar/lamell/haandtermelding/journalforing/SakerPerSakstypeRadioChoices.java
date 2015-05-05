package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.SakerForTema;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import java.util.List;

import static no.nav.modig.modia.aria.AriaHelpers.toggleButtonConnector;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class SakerPerSakstypeRadioChoices extends Panel {

    public SakerPerSakstypeRadioChoices(String id, PropertyModel<List<SakerForTema>> model, final String sakstypePropertyKey, final IModel<Boolean> open) {
        super(id);
        setOutputMarkupId(true);

        AjaxLink header = new AjaxLink("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                open.setObject(!open.getObject());
                target.add(SakerPerSakstypeRadioChoices.this);
            }
        };
        header.add(new Label("sakstype", new ResourceModel(sakstypePropertyKey)));

        WebMarkupContainer pil = new WebMarkupContainer("pil");
        pil.add(hasCssClassIf("opp", open),
                hasCssClassIf("ned", not(open))
        );
        header.add(pil);

        WebMarkupContainer sakswrapper = new WebMarkupContainer("sakswrapper");
        sakswrapper.add(new PropertyListView<SakerForTema>("saksgruppeliste", model) {
            @Override
            protected void populateItem(ListItem<SakerForTema> item) {
                item.add(new Label("temaNavn"));
                item.add(new PropertyListView<Sak>("saksliste") {
                    @Override
                    protected void populateItem(ListItem<Sak> item) {
                        item.add(new Radio<>("sak", item.getModel()));
                        item.add(new Label("saksIdVisning"));
                        item.add(new Label("opprettetDatoFormatert"));
                        item.add(new Label("fagsystemNavn"));
                    }
                });
            }
        }.add(visibleIf(open)));

        toggleButtonConnector(header, sakswrapper, open);
        add(header, sakswrapper);
    }
}
