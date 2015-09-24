package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.metrics.TimingMetricsBehaviour;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sak.widget.SaksoversiktWidget;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.isA;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.PropertyUtils.visUtbetalinger;

public class OversiktLerret extends Lerret {

    @Inject
    private VarslerService varselService;

    private List<AsyncWidget> asyncWidgets;

    public OversiktLerret(String id, String fnr) {
        super(id);
        add(new TimingMetricsBehaviour("oversikt").withPrefix("lerret."));

        List<Widget<?>> widgets = new ArrayList<>(asList(
                new LenkeWidget("lenker", "E", new ListModel<>(asList("kontrakter"))),
                new SykepengerWidget("sykepenger", "Y", new Model<>(fnr)),
                new MeldingerWidget("meldinger", "M", fnr),
                new SaksoversiktWidget("saksoversikt", "S", fnr)));

        add(new VarslerAjaxLink("varsling-lenke", fnr));

        if (visUtbetalinger()) {
            widgets.add(new UtbetalingWidget("utbetalinger", "U", fnr));
        } else {
            add(new WebMarkupContainer("utbetalinger").setVisibilityAllowed(false));
        }

        asyncWidgets = on(widgets).filter(isA(AsyncWidget.class)).map(castTo(AsyncWidget.class)).collect();

        for (Component widget : widgets) {
            add(widget);
        }
    }

    @Override
    protected void onRender() {
        super.onRender();
        for (AsyncWidget widget : asyncWidgets) {
            widget.startLoading();
        }
    }

    public class VarslerAjaxLink extends AjaxLink<String> {
        private static final int EI_UKE = 7;

        public VarslerAjaxLink(String id, String fnr) {
            super(id);

            add(new Label("varsling-tekst", nyeVarslerDenSisteUken(fnr)));
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            send(OversiktLerret.this, Broadcast.BUBBLE, new NamedEventPayload(WIDGET_HEADER_CLICKED, new WidgetHeaderPayload("varsling")));
        }

        private IModel<String> nyeVarslerDenSisteUken(String fnr) {
            List<Varsel> varsler = varselService.hentAlleVarsler(fnr);

            if (varsler.isEmpty()) {
                return new ResourceModel("varsler.oversikt.lenke.ingen.varsler");
            }

            int antallNyeVarsler = 0;
            LocalDate iDag = LocalDate.now();

            for (Varsel varsel : varsler) {
                int dagerSidenVarsel = Days.daysBetween(varsel.mottattTidspunkt.toLocalDate(), iDag).getDays();
                if (dagerSidenVarsel <= EI_UKE) {
                    antallNyeVarsler++;
                }
            }

            return (antallNyeVarsler > 0) ?
                    new StringResourceModel("varsler.oversikt.lenke.nye.varsler", Model.of(antallNyeVarsler)) :
                    new ResourceModel("varsler.oversikt.lenke");
        }
    }
}