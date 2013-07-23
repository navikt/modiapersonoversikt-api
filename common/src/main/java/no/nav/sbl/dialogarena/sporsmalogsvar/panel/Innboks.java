package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding.harTraadId;

public class Innboks extends Panel {

    public static final String VALGT_MELDING_EVENT = "hendelser.valgte_melding";
    public static final String MELDINGER_OPPDATERT = "hendelser.meldinger_oppdatert";

    @Inject
    MeldingService service;

    private final String fodselsnr;
    private final AlleMeldingerModel alleMeldingerModel;
    private final IModel<Melding> valgtMeldingModel;

    public Innboks(String id, String fodselsnr) {
        super(id);
        alleMeldingerModel = new AlleMeldingerModel(fodselsnr, service);
        setDefaultModel(alleMeldingerModel);
        valgtMeldingModel = new Model<>();
        setOutputMarkupId(true);
        this.fodselsnr = fodselsnr;

        add(new AlleMeldingerPanel("meldinger", valgtMeldingModel, alleMeldingerModel));
        add(new MeldingstraadPanel("traad", valgtMeldingModel,  new MeldingstraadModel(valgtMeldingModel, alleMeldingerModel)));

        add(new AttributeAppender("class", " innboks clearfix"));
    }

    @RunOnEvents(MELDINGER_OPPDATERT)
    public void messagesUpdated(AjaxRequestTarget target) {
        target.add(this);
    }
}
