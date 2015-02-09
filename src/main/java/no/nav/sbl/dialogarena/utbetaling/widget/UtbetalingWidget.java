package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.modia.widget.panels.ErrorListing;
import no.nav.modig.modia.widget.panels.GenericListing;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.HovedytelseUtils.betweenNowAndNumberOfMonthsBefore;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.TIL_HOVEDYTELSEVM;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.UtbetalingVMComparator;

public class UtbetalingWidget extends FeedWidget<HovedytelseVM> {

    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingWidget.class);
    private static final int MAX_NUMBER_OF_UTBETALINGER = 4;
    public static final int NUMBER_OF_MONTHS_TO_SHOW = 3;

    @Inject
    private UtbetalingService utbetalingService;

    private String fnr;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial, true, "widget.utbetalingWidget.flereUtbetalinger");
        this.fnr = fnr;
        hentUtbetalingOgSettDefaultModel();
        setMaxNumberOfFeedItems(MAX_NUMBER_OF_UTBETALINGER+1);
    }

    protected static List<HovedytelseVM> transformUtbetalingToVM(List<Record<Hovedytelse>> utbetalinger) {
        return on(utbetalinger)
                .filter(betweenNowAndNumberOfMonthsBefore(NUMBER_OF_MONTHS_TO_SHOW))
                .map(TIL_HOVEDYTELSEVM)
                .collect(new UtbetalingVMComparator());
    }

    protected void hentUtbetalingOgSettDefaultModel() {
        try {
            List<Record<Hovedytelse>> hovedytelser = utbetalingService.hentUtbetalinger(fnr, defaultStartDato(), defaultSluttDato());
            if (hovedytelser.isEmpty()) {
                setDefaultModel(new ListModel<>(asList(new GenericListing(getString("ingen.utbetalinger")))));
            } else {
                setDefaultModel(new ListModel<>(transformUtbetalingToVM(hovedytelser)));
            }
        } catch (ApplicationException | SystemException e) {
            LOG.warn("Feilet ved henting av utbetalingsinformasjon for fnr {}", fnr, e);
            setDefaultModel(new ListModel<>(asList(new ErrorListing(getString("utbetalinger.feilet")))));
        }
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<HovedytelseVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
