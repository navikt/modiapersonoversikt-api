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

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());
        List<Record<Underytelse>> underytelser = utbetalingVM.getUnderytelser();
        IModel<String> melding = Model.of(utbetalingVM.getMelding());

        List<YtelseVM> ytelseVMer = new ArrayList<>();
        appendUnderytelser(utbetalingVM, ytelseVMer);
        appendTrekk(utbetalingVM, ytelseVMer);
        appendSkatteTrekk(utbetalingVM, ytelseVMer);

        add(
                new Label("mottakernavn", utbetalingVM.getMottakerNavn()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
                createYtelserader(ytelseVMer),
                new MultiLineLabel("bilagsmelding", melding).add(visibleIf(not(isEmptyString(melding))))
        );
    }

    private void appendSkatteTrekk(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getSkatteTrekk() == null) {
            return;
        }

        for (Double skatt : utbetalingVM.getSkatteTrekk()) {
            ytelseVMer.add(new YtelseVM(getString("ytelse.skatt.beskrivelse.tekst"), skatt));
        }

    }

    private void appendTrekk(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getTrekkListe() == null) {
            return;
        }

        for (Record<Trekk> trekk : utbetalingVM.getTrekkListe()) {
            ytelseVMer.add(new YtelseVM(
                    trekk.get(Trekk.trekksType),
                    trekk.get(Trekk.trekkBeloep)));
        }

    }

    private void appendUnderytelser(UtbetalingVM utbetalingVM, List<YtelseVM> ytelseVMer) {
        if(utbetalingVM.getUnderytelser() == null) {
            return;
        }

        for(Record<Underytelse> underytelse : utbetalingVM.getUnderytelser()) {
            ytelseVMer.add(new YtelseVM(
                    underytelse.get(Underytelse.ytelsesType),
                    underytelse.get(Underytelse.satsBeloep),
                    underytelse.get(Underytelse.satsAntall),
                    underytelse.get(Underytelse.ytelseBeloep)));
        }
    }

//    private ListView createUnderytelsesrader(List<Record<Underytelse>> underytelser) {
//        return new ListView<Record<Underytelse>>("underytelser", underytelser) {
//            @Override
//            protected void populateItem(ListItem<Record<Underytelse>> item) {
//                Double sats = item.getModelObject().get(Underytelse.satsAntall);
//                Double antall = item.getModelObject().get(Underytelse.satsAntall);
//                item.add(
//                    new Label("underytelse", item.getModelObject().get(Underytelse.ytelsesType)),
//                    new Label("sats", sats != null ? sats : ""),
//                    new Label("antall", antall != null ? antall : ""),
//                    new Label("belop", getBelopString(item.getModelObject().get(Underytelse.ytelseBeloep)))
//                );
//            }
//        };
//    }

    private ListView createYtelserader(List<YtelseVM> underytelser) {
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
