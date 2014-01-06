package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());

        add(
                new Label("mottakernavn", utbetalingVM.getMottakerNavn()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
//                createYtelsesrader(utbetalingVM),
                new Label("bilagsmelding", utbetalingVM.getMelding())
        );
    }

//    private ListView createYtelsesrader(final UtbetalingVM utbetalingVM) {
//        List<Underytelse> underytelser = utbetalingVM.getUnderytelser(); //TODO lag wicketmodell, smell inn i listview
//        return new ListView<Underytelse>("ytelsesrader") {
//            @Override
//            protected void populateItem(ListItem<Underytelse> item) {
//                item.add(
//                        new Label("ytelse", ""), //??
//                        new Label("antall", item.getModelObject().getAntall()),
//                        new Label("sats", item.getModelObject().getSats()),
//                        new Label("brutto", ""), //??
//                        new Label("trekk", ""), //??
//                        new Label("tilutbetaling", "") //??
//                );
//            }
//        };
//    }

}
