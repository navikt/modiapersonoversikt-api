package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.YtelseVM.DESC_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.util.VMUtils.*;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        if (utbetalingVM.isErHovedUtbetaling()) {
            setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());
        }
        IModel<String> melding = Model.of(utbetalingVM.getMelding());
        List<YtelseVM> ytelseVMer = createYtelseVMerList(utbetalingVM);

        add(
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("brutto", utbetalingVM.getBruttoBelop()),
                new Label("trekk", utbetalingVM.getTrekk()),
                new Label("utbetalt", utbetalingVM.getUtbetalt()),
                createYtelserader(ytelseVMer),
                new Label("utbetalingsmelding", melding).add(visibleIf(not(isEmptyString(melding)))));
    }

    protected List<YtelseVM> createYtelseVMerList(UtbetalingVM utbetalingVM) {
        List<YtelseVM> ytelseVMer = new ArrayList<>();
        appendUnderytelser(utbetalingVM, ytelseVMer);
        appendSkatteTrekk(utbetalingVM, ytelseVMer);
        appendTrekk(utbetalingVM, ytelseVMer);
        return ytelseVMer;
    }

    protected void appendSkatteTrekk(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if (utbetalingVM.getSkatteTrekk() != null) {
            ytelseVMer.addAll(utbetalingVM.getSkatteTrekk().stream()
                    .map(skattTilYtelseVM(DetaljPanel.this))
                    .sorted(DESC_BELOP)
                    .collect(toList()));
        }
    }

    protected void appendTrekk(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if (utbetalingVM.getTrekkListe() != null) {
            ytelseVMer.addAll(
                    utbetalingVM.getTrekkListe().stream()
                            .map(TREKK_TIL_YTELSE_VM)
                            .sorted(DESC_BELOP)
                            .collect(toList()));
        }
    }

    protected void appendUnderytelser(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if (utbetalingVM.getUnderytelser() != null) {
            ytelseVMer.addAll(utbetalingVM.getUnderytelser().stream()
                    .map(UNDERYTELSE_TIL_YTELSE_VM)
                    .sorted(DESC_BELOP)
                    .collect(toList()));
        }
    }

    protected ListView createYtelserader(List<YtelseVM> underytelser) {
        return new ListView<YtelseVM>("underytelser", underytelser) {
            @Override
            protected void populateItem(ListItem<YtelseVM> item) {
                item.add(new Label("underytelse", item.getModelObject().getYtelse()),
                        new Label("sats", item.getModelObject().getSats()),
                        new Label("antall", item.getModelObject().getAntall()),
                        new Label("belop", item.getModelObject().getBelop()));
            }
        };
    }

}
