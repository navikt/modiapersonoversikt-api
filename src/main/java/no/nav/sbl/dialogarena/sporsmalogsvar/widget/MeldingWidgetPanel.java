package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.getStatusKlasse;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.LEST_AV_BRUKER;

public class MeldingWidgetPanel extends GenericPanel<MeldingVM> {

    public MeldingWidgetPanel(String id, IModel<MeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        MeldingVM meldingVM = model.getObject();

        WebMarkupContainer statusContainer = new WebMarkupContainer("status-container");

        WebMarkupContainer status = new WebMarkupContainer("statusIndikator");
        status.add(new AttributeModifier("class", getStatusKlasse(meldingVM.status)));

        Label lestDato = new Label("lestDato", meldingVM.lestDato != null ? Datoformat.ultrakort(meldingVM.lestDato) : "");
        lestDato.add(visibleIf(Model.of(meldingVM.status == LEST_AV_BRUKER)));

        statusContainer.add(
                status,
                lestDato,
                new Label("status",  new StringResourceModel("widget.${status}", getModel())));

        add(
                new Label("opprettetDato", Datoformat.langMedTid(meldingVM.opprettetDato)),
                new Label("avsender"),
                new Label("tema", new StringResourceModel("${tema}", getModel())),
                statusContainer);
    }
}
