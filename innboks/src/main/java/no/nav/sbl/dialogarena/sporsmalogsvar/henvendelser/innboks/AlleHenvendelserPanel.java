package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

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

    public AlleHenvendelserPanel(String id, final InnboksModell innboksModell) {
        super(id);
        setOutputMarkupId(true);

        add(new PropertyListView<HenvendelseVM>("nyesteHenvendelseITraad") {
            @Override
            protected void populateItem(final ListItem<HenvendelseVM> item) {

                final HenvendelseVM itemModelObject = item.getModelObject();

                item.add(new Label("antall-henvendelser", new AbstractReadOnlyModel<Integer>() {
                    @Override
                    public Integer getObject() {
                        return innboksModell.getInnboksVM().getTraadLengde(item.getModelObject().henvendelse.traadId);
                    }
                }));

                item.add(new Label("indikator-dot", "")
                        .add(hasCssClassIf("lest", itemModelObject.erLest()))
                        .add(hasCssClassIf("ikke-besvart", itemModelObject.erIkkeBesvart())));
                item.add(new Label("indikator-tekst",
                        itemModelObject.erIkkeBesvart().getObject() ?
                                "Ikke besvart" :
                                (itemModelObject.erLest().getObject() ?
                                        "Lest av bruker " + itemModelObject.formatertDato(itemModelObject.henvendelse.lestDato, "dd.MM.yyyy 'kl' hh:mm").getObject() :
                                        "Ikke lest av bruker")));

                item.add(new Label("opprettet",
                        itemModelObject.formatertDato(itemModelObject.henvendelse.opprettet, "dd.MM.yyyy 'kl' hh:mm")));
                item.add(new Label("henvendelse.overskrift"));
                item.add(new Label("tema", new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return new StringResourceModel(itemModelObject.henvendelse.tema, AlleHenvendelserPanel.this, null).getString();
                    }
                }));

                item.add(new Label("henvendelse.fritekst"));
                item.add(hasCssClassIf("valgt", innboksModell.erValgtHenvendelse(itemModelObject)));
                item.add(hasCssClassIf("lest", itemModelObject.erLest()));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        // Merk meldingen som valgt
                        innboksModell.getInnboksVM().setValgtHenvendelse(itemModelObject);
                        send(getPage(), Broadcast.DEPTH, Innboks.VALGT_HENVENDELSE);
                        // Oppdater visningen
                        innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm.setObject(true);
                        target.add(AlleHenvendelserPanel.this);
                    }
                });
            }
        });
    }
}
