package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.List;

public class UtbetalingLamell extends Lerret {

    @Inject
    private UtbetalingService utbetalingService;


    public UtbetalingLamell(String id, String fnr) {
        super(id);
        List<Utbetaling> utbetalinger = utbetalingService.hentUtbetalinger(fnr);


        ListView<Utbetaling> utbetalingListView = new ListView<Utbetaling>("utbetalinger", Model.ofList(utbetalinger)) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                item.add(new Label("beskrivelse", item.getModelObject().getBeskrivelse()));
            }
        };
        add(utbetalingListView);

    }
}
