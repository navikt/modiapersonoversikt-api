package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.TIL_HOVEDYTELSEVM;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.UtbetalingVMComparator;

public class UtbetalingWidget extends AsyncWidget<HovedytelseVM> {

    private static final int MAX_NUMBER_OF_UTBETALINGER = 5;
    public static final int NUMBER_OF_MONTHS_TO_SHOW = 3;

    private final String fnr;
    @Inject
    private UtbetalingService utbetalingService;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial, MAX_NUMBER_OF_UTBETALINGER,
                "utbetalinger.feilet", "widget.utbetalingWidget.flereUtbetalinger");
        this.fnr = fnr;
    }

    protected static List<HovedytelseVM> transformUtbetalingToVM(List<Record<Hovedytelse>> utbetalinger) {
        return on(utbetalinger)
                .filter(betweenNowAndMonthsBefore(NUMBER_OF_MONTHS_TO_SHOW))
                .map(TIL_HOVEDYTELSEVM)
                .collect(new UtbetalingVMComparator());
    }

    @Override
    public List<HovedytelseVM> getFeedItems() throws Exception {
        List<Record<Hovedytelse>> hovedytelser = utbetalingService.hentUtbetalinger(fnr, defaultStartDato(), defaultSluttDato());
        return transformUtbetalingToVM(hovedytelser);
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<HovedytelseVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
