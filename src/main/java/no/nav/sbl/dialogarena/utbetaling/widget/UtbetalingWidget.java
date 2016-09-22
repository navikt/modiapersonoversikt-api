package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedutbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.Transformers.SAMMENLAGT_UTBETALING_TRANSFORMER;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedutbetalingVM.TIL_HOVEDUTBETALINGVM;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedutbetalingVM.UtbetalingVMComparator;

public class UtbetalingWidget extends AsyncWidget<HovedutbetalingVM> {

    public static final int NUMBER_OF_DAYS_TO_SHOW = 30;

    private final String fnr;
    @Inject
    private UtbetalingService utbetalingService;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial, new PropertyKeys().withErrorKey("utbetalinger.feilet").withOverflowKey("widget.utbetalingWidget.flereUtbetalinger").withEmptyKey("ingen.utbetalinger"));
        this.fnr = fnr;
    }

    static List<HovedutbetalingVM> transformUtbetalingToVM(List<Hovedutbetaling> utbetalinger) {
        return utbetalinger.stream()
                .filter(betweenNowAndDaysBefore(NUMBER_OF_DAYS_TO_SHOW))
                .map(TIL_HOVEDUTBETALINGVM)
                .sorted(new UtbetalingVMComparator())
                .collect(toList());
    }

    @Override
    public List<HovedutbetalingVM> getFeedItems() {
        List<WSUtbetaling> utbetalingerIPerioden = utbetalingService.hentWSUtbetalinger(fnr, defaultStartDato(), defaultSluttDato());
        List<Hovedutbetaling> utbetalinger = getUtbetalingListe(utbetalingerIPerioden);
        return transformUtbetalingToVM(utbetalinger);
    }

    private List<Hovedutbetaling> getUtbetalingListe(List<WSUtbetaling> utbetalingerMedPosteringInnenPerioden) {
        return utbetalingerMedPosteringInnenPerioden.stream()
                .map(SAMMENLAGT_UTBETALING_TRANSFORMER)
                .sorted(SISTE_UTBETALING_FORST)
                .collect(toList());
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<HovedutbetalingVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
