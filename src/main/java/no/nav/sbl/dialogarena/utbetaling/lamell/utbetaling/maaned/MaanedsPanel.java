package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned;

import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.SammenlagtUtbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.SammenlagtUtbetaling.SammenlagtUtbetalingPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.MaanedOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringVM;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.hentAlleSynligeHovedytelser;
import static org.joda.time.LocalDate.now;

public class MaanedsPanel extends Panel {

    public MaanedsPanel(String id, List<SammenlagtUtbetaling> sammenlagtUtbetalingList) {
        super(id);
        List<Hovedytelse> utbetalingsliste = hentAlleSynligeHovedytelser(sammenlagtUtbetalingList);
        add(
                createOppsummeringsPanel(utbetalingsliste),
                createSammenlagtUtbetalinger(sammenlagtUtbetalingList)
        );
    }

    private MaanedOppsummeringPanel createOppsummeringsPanel(List<Hovedytelse> utbetalingsliste) {
        return new MaanedOppsummeringPanel("oppsummeringsPanel",
                createOppsummeringVM(utbetalingsliste));
    }

    private ListView<SammenlagtUtbetaling> createSammenlagtUtbetalinger(List<SammenlagtUtbetaling> sammenlagtUtbetalingList) {
        return new ListView<SammenlagtUtbetaling>("sammenlagtUtbetalinger", sammenlagtUtbetalingList) {
            @Override
            protected void populateItem(ListItem<SammenlagtUtbetaling> item) {
                item.add(new SammenlagtUtbetalingPanel("sammenlagtUtbetaling", item.getModelObject()));
            }
        };
    }

    private OppsummeringVM createOppsummeringVM(List<Hovedytelse> liste) {
        if (liste.isEmpty()) {
            return new OppsummeringVM(new ArrayList<>(), now(), now());
        }

        LocalDate startDato = getStartDato(liste);
        LocalDate sluttDato = getSluttDato(liste);

        return new OppsummeringVM(liste, startDato, sluttDato);
    }

    private LocalDate getSluttDato(List<Hovedytelse> liste) {
        return liste.get(0).getHovedytelsedato().dayOfMonth().withMaximumValue().toLocalDate();
    }

    private LocalDate getStartDato(List<Hovedytelse> liste) {
        return liste.get(liste.size() - 1).getHovedytelsedato().dayOfMonth().withMinimumValue().toLocalDate();
    }

}
