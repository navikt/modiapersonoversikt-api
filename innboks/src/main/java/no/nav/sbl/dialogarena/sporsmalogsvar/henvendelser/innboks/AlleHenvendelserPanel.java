package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.HenvendelseService;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class AlleHenvendelserPanel extends Panel {

    public AlleHenvendelserPanel(String id, final InnboksModell innboksModell, final HenvendelseService service) {
        super(id);
        setOutputMarkupId(true);

        add(new PropertyListView<HenvendelseVM>("nyesteHenvendelseITraad") {
            @Override
            protected void populateItem(final ListItem<HenvendelseVM> item) {
                item.add(new Label("henvendelse.overskrift"));
                item.add(new Label("tema", new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return new StringResourceModel(item.getModelObject().henvendelse.tema, AlleHenvendelserPanel.this, null).getString();
                    }
                }));
                item.add(new Label("opprettet", item.getModelObject().formatertDato(item.getModelObject().henvendelse.opprettet, "dd.MM.yyyy")));
                item.add(new Label("henvendelse.fritekst"));
                item.add(hasCssClassIf("valgt", innboksModell.erValgtHenvendelse(item.getModelObject())));
                item.add(hasCssClassIf("lest", item.getModelObject().erLest()));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        // Merk meldingen som valgt
                        innboksModell.getInnboksVM().setValgtHenvendelse(item.getModelObject());
                        send(getPage(), Broadcast.DEPTH, Innboks.VALGT_HENVENDELSE);
                        // Merk meldingen som lest
                        if (!item.getModelObject().erLest().getObject()) {
                            service.merkHenvendelseSomLest(item.getModelObject().henvendelse.id);
                            item.getModelObject().henvendelse.markerSomLest();
                        }
                        // Oppdater visningen
                        innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm.setObject(true);
                        target.add(AlleHenvendelserPanel.this);
                    }
                });
            }
        });
    }
}
