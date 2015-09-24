package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.metrics.TimingMetricsBehaviour;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.sbl.dialogarena.sak.widget.SaksoversiktWidget;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import no.nav.sbl.dialogarena.varsel.lamell.VarslerOversiktLink;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.isA;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.PropertyUtils.visUtbetalinger;

public class OversiktLerret extends Lerret {

    private List<AsyncWidget> asyncWidgets;

    public OversiktLerret(String id, String fnr) {
        super(id);
        add(new TimingMetricsBehaviour("oversikt").withPrefix("lerret."));

        List<Widget<?>> widgets = new ArrayList<>(asList(
                new LenkeWidget("lenker", "E", new ListModel<>(asList("kontrakter"))),
                new SykepengerWidget("sykepenger", "Y", new Model<>(fnr)),
                new MeldingerWidget("meldinger", "M", fnr),
                new SaksoversiktWidget("saksoversikt", "S", fnr)));


        add(new VarslerOversiktLink("varsling-lenke", fnr));

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
}