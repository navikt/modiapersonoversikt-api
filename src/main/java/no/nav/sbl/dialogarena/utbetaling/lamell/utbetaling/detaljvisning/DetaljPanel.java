package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;


public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());

        add(
                new Label("mottakerid", utbetalingVM.getMottakerId()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
                createYtelsesrader(utbetalingVM),
                new Label("bilagsmelding", "TODO fiks bilagsmelding")
        );
    }

    private ListView createYtelsesrader(UtbetalingVM utbetalingVM) {
        List<Underytelse> underytelser = utbetalingVM.getUnderytelser(); //TODO lag wicketmodell, smell inn i listview
        return new ListView("ytelsesrader") {
            @Override
            protected void populateItem(ListItem item) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

}
