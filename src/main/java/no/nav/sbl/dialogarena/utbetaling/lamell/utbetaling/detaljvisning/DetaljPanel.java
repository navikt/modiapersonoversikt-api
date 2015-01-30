package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());
        List<Record<Underytelse>> underytelser = utbetalingVM.getUnderytelser();
        IModel<String> melding = Model.of(utbetalingVM.getMelding());
        add(
                new Label("mottakernavn", utbetalingVM.getMottakerNavn()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
                createUnderytelsesrader(underytelser),
                new MultiLineLabel("bilagsmelding", melding).add(visibleIf(not(isEmptyString(melding))))
        );
    }

    private ListView createUnderytelsesrader(List<Record<Underytelse>> underytelser) {
        return new ListView<Record<Underytelse>>("underytelser", underytelser) {
            @Override
            protected void populateItem(ListItem<Record<Underytelse>> item) {
                Double sats = item.getModelObject().get(Underytelse.satsAntall);
                Double antall = item.getModelObject().get(Underytelse.satsAntall);
                item.add(
                    new Label("underytelse", item.getModelObject().get(Underytelse.ytelsesType)),
                    new Label("sats", sats != null ? sats : ""),
                    new Label("antall", antall != null ? antall : ""),
                    new Label("belop", getBelopString(item.getModelObject().get(Underytelse.ytelseBeloep)))
                );
            }
        };
    }
}
