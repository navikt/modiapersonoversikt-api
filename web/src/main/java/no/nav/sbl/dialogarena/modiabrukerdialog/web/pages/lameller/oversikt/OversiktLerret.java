package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.sbl.dialogarena.sak.widget.SaksoversiktWidget;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.commons.collections15.Closure;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.isA;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.PropertyUtils.visUtbetalinger;

public class OversiktLerret extends Lerret {

    private List<AsyncWidget> asyncWidgets;

    public OversiktLerret(String id, String fnr) {
        super(id);
        List<Component> widgets = asList(
                new LenkeWidget("lenker", "E", new ListModel<>(asList("kontrakter"))),
                new SykepengerWidget("sykepenger", "Y", new Model<>(fnr)),
                new UtbetalingWidget("utbetalinger", "U", fnr).add(visibleIf(Model.of(visUtbetalinger()))),
                new MeldingerWidget("meldinger", "M", fnr),
                new SaksoversiktWidget("saksoversikt", "S", fnr));

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