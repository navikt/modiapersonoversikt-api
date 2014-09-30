package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
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

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class SakerPerSakstypeRadioChoices extends Panel {

    public SakerPerSakstypeRadioChoices(String id, PropertyModel<List<TemaSaker>> model, final String sakstypePropertyKey, final IModel<Boolean> open) {
        super(id);
        setOutputMarkupId(true);

        add(new AjaxLink("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                open.setObject(!open.getObject());
                target.add(SakerPerSakstypeRadioChoices.this);
            }
        }.add(new Label("sakstype", new ResourceModel(sakstypePropertyKey))));

        add(new PropertyListView<TemaSaker>("saksgruppeliste", model) {
            @Override
            protected void populateItem(ListItem<TemaSaker> item) {
                item.add(new Label("temaNavn"));
                item.add(new PropertyListView<Sak>("saksliste") {
                    @Override
                    protected void populateItem(ListItem<Sak> item) {
                        item.add(new Radio<>("sak", item.getModel()));
                        item.add(new Label("saksId"));
                        item.add(new Label("opprettetDatoFormatert"));
                        item.add(new Label("fagsystemNavn"));
                    }
                });
            }
        }.add(visibleIf(open)));

        WebMarkupContainer pil = new WebMarkupContainer("pil");
        pil.add(
                hasCssClassIf("opp", open),
                hasCssClassIf("ned", not(open))
        );
        add(pil);
    }
}
