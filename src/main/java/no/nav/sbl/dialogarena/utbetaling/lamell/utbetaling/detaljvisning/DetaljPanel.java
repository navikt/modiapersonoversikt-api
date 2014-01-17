package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseComparator;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());

        List<Underytelse> underytelser = utbetalingVM.getUnderytelser();
        sort(underytelser, UnderytelseComparator.BELOP_SORT);
        sort(underytelser, UnderytelseComparator.SKATT_NEDERST_SORT);

        add(
                new Label("mottakernavn", utbetalingVM.getMottakerNavn()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
                createUnderytelsesrader(underytelser),
                new Label("bilagsmelding", utbetalingVM.getMelding())
        );
    }

    private ListView createUnderytelsesrader(List<Underytelse> underytelser) {
        return new ListView<Underytelse>("underytelser", underytelser) {
            @Override
            protected void populateItem(ListItem<Underytelse> item) {
                item.add(
                    new Label("underytelse", item.getModelObject().getTittel()),
                    new Label("sats", item.getModelObject().getSats()),
                    new Label("antall", item.getModelObject().getAntall()),
                    new Label("belop", getBelopString(item.getModelObject().getBelop()))
                );
            }
        };
    }

}
