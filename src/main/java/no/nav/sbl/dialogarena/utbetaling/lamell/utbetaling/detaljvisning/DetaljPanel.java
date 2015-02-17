package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.YtelseVM.DESC_BELOP;
import static no.nav.sbl.dialogarena.utbetaling.util.VMUtils.*;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());
        IModel<String> melding = Model.of(utbetalingVM.getMelding());
        List<YtelseVM> ytelseVMer = createYtelseVMerList(utbetalingVM);

        add(new Label("mottakernavn", utbetalingVM.getMottakerNavn()),
            new Label("konto", utbetalingVM.getKontonr()),
            new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
            createYtelserader(ytelseVMer),
            new MultiLineLabel("bilagsmelding", melding).add(visibleIf(not(isEmptyString(melding)))));
    }

    protected List<YtelseVM> createYtelseVMerList(UtbetalingVM utbetalingVM) {
        List<YtelseVM> ytelseVMer = new ArrayList<>();
        appendUnderytelser(utbetalingVM, ytelseVMer);
        appendSkatteTrekk(utbetalingVM, ytelseVMer);
        appendTrekk(utbetalingVM, ytelseVMer);
        return ytelseVMer;
    }

    protected void appendSkatteTrekk(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getSkatteTrekk() != null) {
            ytelseVMer.addAll(on(utbetalingVM.getSkatteTrekk())
                    .map(skattTilYtelseVM(DetaljPanel.this))
                    .collect(DESC_BELOP));
        }
    }

    protected void appendTrekk(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getTrekkListe() != null) {
            ytelseVMer.addAll(on(utbetalingVM.getTrekkListe())
                    .map(TREKK_TIL_YTELSE_VM)
                    .collect(DESC_BELOP));
        }
    }

    protected void appendUnderytelser(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getUnderytelser() != null) {
            ytelseVMer.addAll(on(utbetalingVM.getUnderytelser())
                    .map(UNDERYTELSE_TIL_YTELSE_VM)
                    .collect(DESC_BELOP));
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
