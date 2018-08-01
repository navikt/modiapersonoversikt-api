package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import no.nav.sbl.dialogarena.utbetaling.util.AjaxIndicator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.*;

public class FilterForm extends Panel {

    private FilterParametere filterParametere;
    private MarkupContainer ytelsesContainer;
    private IModel<Boolean> visAlleYtelser;
    private AjaxCheckBox visAlleYtelserCheckbox;

    public FilterForm(String id, final FilterParametere filterParametere) {
        super(id);
        this.filterParametere = filterParametere;

        this.visAlleYtelser = new Model<Boolean>() {
            @Override
            public Boolean getObject() {
                return filterParametere.isAlleYtelserValgt();
            }

            @Override
            public void setObject(Boolean visAlle) {
                filterParametere.toggleAlleYtelser(visAlle);
            }
        };
        this.ytelsesContainer = createYtelser();


        add(createForm("filterForm"));

    }

    private Form createForm(String id) {
        Form filterForm = new AjaxIndicator.SnurrepippFilterForm(id);

        filterForm.add(
                createMottakerButton("visBruker", Mottakertype.BRUKER),
                createMottakerButton("visAnnenMottaker", Mottakertype.ANNEN_MOTTAKER),
                ytelsesContainer
        );

        filterForm.setOutputMarkupId(true);
        return filterForm;
    }

    private AjaxCheckBox createMottakerButton(final String id, final Mottakertype mottaker) {
        return new AjaxCheckBox(id, new Model<>(filterParametere.viseMottaker(mottaker))) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                filterParametere.toggleMottaker(mottaker);
                sendFilterEndretEvent();
            }
        };
    }

    private MarkupContainer createYtelser() {
        IModel<List<String>> alleYtelserModel = new AbstractReadOnlyModel<List<String>>() {
            @Override
            public List<String> getObject() {
                ArrayList<String> ytelser = new ArrayList<>(filterParametere.getAlleYtelser());
                sort(ytelser);
                return ytelser;
            }
        };
        ListView<String> listView = new ListView<String>("ytelseFilter", alleYtelserModel) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                boolean erValgt = filterParametere.erYtelseOnsket(item.getModelObject());
                AjaxCheckBox checkbox = new AjaxCheckBox("visYtelse", new Model<>(erValgt)) {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        String ytelse = item.getModelObject();
                        if (this.getModelObject()) {
                            filterParametere.leggTilOnsketYtelse(ytelse);
                        } else {
                            filterParametere.fjernOnsketYtelse(ytelse);
                        }

                        sendYtelsesfilterCheckedEvent();
                    }
                };
                item.add(checkbox);
                item.add(new Label("ytelseLabel", item.getModel()).add(new AttributeModifier("for", checkbox.getMarkupId())));
            }
        };
        visAlleYtelserCheckbox = createAlleYtelserCheckbox();
        return (MarkupContainer) new WebMarkupContainer("ytelseContainer")
                .add(visAlleYtelserCheckbox)
                .add(listView)
                .setOutputMarkupId(true);
    }

    private AjaxCheckBox createAlleYtelserCheckbox() {
        return new AjaxCheckBox("visAlleYtelser", visAlleYtelser) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                filterParametere.toggleAlleYtelser(this.getModelObject());
                sendFilterEndretEvent();
            }
        };
    }

    private void sendFilterEndretEvent() {
        send(getPage(), Broadcast.DEPTH, FILTER_ENDRET);
    }

    private void sendYtelsesfilterCheckedEvent() {
        send(getPage(), Broadcast.DEPTH, YTELSE_FILTER_KLIKKET);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(PERIODEVALG)
    private void oppdaterPeriodevelger(AjaxRequestTarget target) {
        target.add(visAlleYtelserCheckbox);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(HOVEDYTELSER_ENDRET)
    private void oppdaterYtelsesKnapper(AjaxRequestTarget target) {
        target.add(ytelsesContainer);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(YTELSE_FILTER_KLIKKET)
    private void oppdaterVelgAlleCheckbox(AjaxRequestTarget target) {
        target.add(visAlleYtelserCheckbox);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FILTER_ENDRET)
    private void oppdaterYtelsesContainer(AjaxRequestTarget target) {
        target.add(ytelsesContainer);
    }
}
