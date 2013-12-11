package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingVM.UTBETALING_UTBETALINGVM_TRANSFORMER;

public class UtbetalingWidget extends FeedWidget<UtbetalingVM> {

    public static final PackageResourceReference UTBETALING_WIDGET_LESS = new PackageResourceReference(UtbetalingWidget.class, "utbetaling.less");

    private static final int MAX_NUMBER_OF_UTBETALINGER = 6;

    @Inject
    private UtbetalingService utbetalingService;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial);
        setDefaultModel(new CompoundPropertyModel<Object>(transformUtbetalingToVM(utbetalingService.hentUtbetalinger(fnr, defaultStartDato(), defaultSluttDato()))));
    }

    private List<UtbetalingVM> transformUtbetalingToVM(List<Utbetaling> utbetalinger) {
        List<UtbetalingVM> utbetalingVMs = transformToVMs(utbetalinger);
        return on(utbetalingVMs).take(MAX_NUMBER_OF_UTBETALINGER).collect();
    }

    private List<UtbetalingVM> transformToVMs(List<Utbetaling> utbetalinger) {
        return on(utbetalinger).map(UTBETALING_UTBETALINGVM_TRANSFORMER).collect(new UtbetalingVMComparator());
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<UtbetalingVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
