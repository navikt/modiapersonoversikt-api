package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Trekk;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.YtelseVM.DESC_BELOP;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());
        IModel<String> melding = Model.of(utbetalingVM.getMelding());

        List<YtelseVM> ytelseVMer = createYtelseVMerList(utbetalingVM);
        add(
                new Label("mottakernavn", utbetalingVM.getMottakerNavn()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
                createYtelserader(ytelseVMer),
                new MultiLineLabel("bilagsmelding", melding).add(visibleIf(not(isEmptyString(melding))))
        );
    }

    protected List<YtelseVM> createYtelseVMerList(UtbetalingVM utbetalingVM) {
        List<YtelseVM> ytelseVMer = new ArrayList<>();
        appendUnderytelser(utbetalingVM, ytelseVMer);
        appendSkatteTrekk(utbetalingVM, ytelseVMer);
        appendTrekk(utbetalingVM, ytelseVMer);

        return ytelseVMer;
    }

    protected void appendSkatteTrekk(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getSkatteTrekk() == null) {
            return;
        }

        List<YtelseVM> tempList = new ArrayList<>();
        for (Double skatt : utbetalingVM.getSkatteTrekk()) {
            tempList.add(new YtelseVM(new StringResourceModel("ytelse.skatt.beskrivelse.tekst", DetaljPanel.this, null).getString(), skatt));
        }
        sort(tempList, DESC_BELOP);
        ytelseVMer.addAll(tempList);
    }

    protected void appendTrekk(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getTrekkListe() == null) {
            return;
        }

        List<YtelseVM> tempList = new ArrayList<>();
        for (Record<Trekk> trekk : utbetalingVM.getTrekkListe()) {
            tempList.add(new YtelseVM(
                    trekk.get(Trekk.trekksType),
                    trekk.get(Trekk.trekkBeloep)));
        }
        sort(tempList, DESC_BELOP);
        ytelseVMer.addAll(tempList);
    }

    protected void appendUnderytelser(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getUnderytelser() == null) {
            return;
        }

        List<YtelseVM> tempList = new ArrayList<>();
        for(Record<Underytelse> underytelse : utbetalingVM.getUnderytelser()) {
            tempList.add(new YtelseVM(
                    underytelse.get(Underytelse.ytelsesType),
                    underytelse.get(Underytelse.ytelseBeloep),
                    underytelse.get(Underytelse.satsAntall),
                    underytelse.get(Underytelse.satsBeloep)));
        }
        sort(tempList, DESC_BELOP);
        ytelseVMer.addAll(tempList);
    }

    protected ListView createYtelserader(List<YtelseVM> underytelser) {
        return new ListView<YtelseVM>("underytelser", underytelser) {
            @Override
            protected void populateItem(ListItem<YtelseVM> item) {
                item.add(
                        new Label("underytelse", item.getModelObject().getYtelse()),
                        new Label("sats", item.getModelObject().getSats()),
                        new Label("antall", item.getModelObject().getAntall()),
                        new Label("belop", item.getModelObject().getBelop())
                );
            }
        };
    }
}
