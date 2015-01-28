package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.utbetaling.domain.UnderytelseGammel;
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
        List<UnderytelseGammel> underytelser = utbetalingVM.getUnderytelser();
        IModel<String> melding = Model.of(utbetalingVM.getMelding());
        add(
                new Label("mottakernavn", utbetalingVM.getMottakerNavn()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
                createUnderytelsesrader(underytelser),
                createSpesifikasjoner(underytelser),
                new MultiLineLabel("bilagsmelding", melding).add(visibleIf(not(isEmptyString(melding))))
        );
    }

    private ListView createSpesifikasjoner(List<UnderytelseGammel> underytelser) {
        return new ListView<UnderytelseGammel>("spesifikasjoner", underytelser) {
            @Override
            protected void populateItem(ListItem<UnderytelseGammel> item) {
                Model<String> spesifikasjon = Model.of(item.getModelObject().getSpesifikasjon());
                item.add(new Label("spesifikasjon", spesifikasjon).add(visibleIf(not(isEmptyString(spesifikasjon)))));
            }
        };
    }

    private ListView createUnderytelsesrader(List<UnderytelseGammel> underytelser) {
        return new ListView<UnderytelseGammel>("underytelser", underytelser) {
            @Override
            protected void populateItem(ListItem<UnderytelseGammel> item) {
                Optional<Double> sats = item.getModelObject().getSats();
                Optional<Double> antall = item.getModelObject().getAntall();
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
