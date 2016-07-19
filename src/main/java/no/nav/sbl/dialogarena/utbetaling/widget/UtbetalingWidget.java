package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.TIL_HOVEDYTELSEVM;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.UtbetalingVMComparator;

public class UtbetalingWidget extends AsyncWidget<HovedytelseVM> {

    public static final int NUMBER_OF_DAYS_TO_SHOW = 30;

    private final String fnr;
    @Inject
    private UtbetalingService utbetalingService;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial, new PropertyKeys().withErrorKey("utbetalinger.feilet").withOverflowKey("widget.utbetalingWidget.flereUtbetalinger").withEmptyKey("ingen.utbetalinger"));
        this.fnr = fnr;
    }

    protected static List<HovedytelseVM> transformUtbetalingToVM(List<Hovedytelse> utbetalinger) {
        return utbetalinger.stream()
                .filter(betweenNowAndDaysBefore(NUMBER_OF_DAYS_TO_SHOW))
                .map(TIL_HOVEDYTELSEVM)
                .sorted(new UtbetalingVMComparator())
                .collect(toList());
    }

    @Override
    public List<HovedytelseVM> getFeedItems() {
        List<Hovedytelse> hovedytelser = utbetalingService.hentUtbetalinger(fnr, defaultStartDato(), defaultSluttDato());
        return transformUtbetalingToVM(hovedytelser);
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<HovedytelseVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
