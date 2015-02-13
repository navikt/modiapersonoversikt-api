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
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
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

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial, true, "widget.utbetalingWidget.flereUtbetalinger");
        this.setDefaultModel(lagModell(fnr));

        setMaxNumberOfFeedItems(MAX_NUMBER_OF_UTBETALINGER+1);
    }

    protected static List<HovedytelseVM> transformUtbetalingToVM(List<Record<Hovedytelse>> utbetalinger) {
        return on(utbetalinger)
                .filter(betweenNowAndNumberOfMonthsBefore(NUMBER_OF_MONTHS_TO_SHOW))
                .map(TIL_HOVEDYTELSEVM)
                .collect(new UtbetalingVMComparator());
    }

    protected ListModel<?> lagModell(final String fnr) {
        List<?> listContent;
        try {
            List<Record<Hovedytelse>> hovedytelser = utbetalingService.hentUtbetalinger(fnr, defaultStartDato(), defaultSluttDato());
            if (hovedytelser.isEmpty()) {
                listContent = asList(new GenericListing(new StringResourceModel("ingen.utbetalinger", UtbetalingWidget.this, null).getString()));
            } else {
                listContent = transformUtbetalingToVM(hovedytelser);
            }
        } catch (ApplicationException | SystemException e) {
            LOG.warn("Feilet ved henting av utbetalingsinformasjon for fnr {}", fnr, e);
            listContent = asList(new ErrorListing(new StringResourceModel("utbetalinger.feilet", UtbetalingWidget.this, null).getString()));
        }
        return new ListModel<>(listContent);
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<HovedytelseVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
