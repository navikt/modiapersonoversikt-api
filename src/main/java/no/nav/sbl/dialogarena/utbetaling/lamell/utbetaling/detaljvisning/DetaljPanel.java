package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());
        List<Underytelse> underytelser = utbetalingVM.getUnderytelser();
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
                Optional<Double> sats = item.getModelObject().getSats();
                Optional<Integer> antall = item.getModelObject().getAntall();
                item.add(
                    new Label("underytelse", item.getModelObject().getTittel()),
                    new Label("sats", sats.isSome() ? sats.get() : ""),
                    new Label("antall", antall.isSome() ? antall.get() : ""),
                    new Label("belop", getBelopString(item.getModelObject().getBelop()))
                );
            }
        };
    }
}
