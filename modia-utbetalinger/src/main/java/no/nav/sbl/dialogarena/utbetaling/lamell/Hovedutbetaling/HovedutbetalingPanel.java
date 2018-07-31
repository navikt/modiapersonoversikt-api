package no.nav.sbl.dialogarena.utbetaling.lamell.Hovedutbetaling;

import no.nav.sbl.dialogarena.utbetaling.domain.Hovedutbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collectors.summingDouble;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.either;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.lagVisningUtbetalingsdato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static org.apache.wicket.AttributeModifier.append;

public class HovedutbetalingPanel extends Panel {

    private static Collector<Double, ?, Double> sumDouble = summingDouble((d) -> d);

    public HovedutbetalingPanel(String id, Hovedutbetaling hovedutbetaling) {
        super(id);

        setMarkupId("utbetaling-" + hovedutbetaling.getId());
        List<Hovedytelse> synligeHovedytelser = hovedutbetaling.getSynligeHovedytelser();
        IModel<Boolean> skalVises = Model.of(hovedutbetaling.skalViseHovedutbetaling());
        IModel<Boolean> harSynligeHovedytelser = Model.of(synligeHovedytelser.size() != 0);

        if (hovedutbetaling.skalViseHovedutbetaling()) {
            add(new AttributeModifier("tabindex", 0));
        }
        add(append("aria-describedBy", "skjermleser-hovedutbetaling-" + hovedutbetaling.getId()));
        add(
                hasCssClassIf("hovedutbetaling-synlig", skalVises),
                hasCssClassIf("navigerbar-utbetaling", skalVises),
                hasCssClassIf("hovedutbetaling-skillestrek", either(harSynligeHovedytelser).or(skalVises))
        );
        add(
                createHovedutbetalingDetaljPanel(synligeHovedytelser, hovedutbetaling),
                createUtbetalingListView(hovedutbetaling, synligeHovedytelser)
        );
    }

    private WebMarkupContainer createHovedutbetalingDetaljPanel(List<Hovedytelse> synligeHovedytelser, Hovedutbetaling hovedutbetaling) {

        WebMarkupContainer hovedutbetalingDetaljPanel = new WebMarkupContainer("hovedutbetalingDetaljPanel");
        hovedutbetalingDetaljPanel.add(
                new Label("utbetalingDato", lagVisningUtbetalingsdato(hovedutbetaling.getHovedytelsesdato())),
                createStatusLabel(hovedutbetaling),
                new Label("ytelse", "Diverse ytelser"),
                new Label("belop", finnSumAvHovedytelser(synligeHovedytelser))
        );

        hovedutbetalingDetaljPanel.setVisibilityAllowed(hovedutbetaling.skalViseHovedutbetaling());
        return hovedutbetalingDetaljPanel;
    }


    private Label createStatusLabel(Hovedutbetaling hovedutbetaling) {
        Label statusLabel = new Label("status", hovedutbetaling.getStatus());
        if (hovedutbetaling.isUtbetalt()) {
            statusLabel.add(new AttributeAppender("class", "utbetalt").setSeparator(" "));
        }
        return statusLabel;
    }

    private String finnSumAvHovedytelser(List<Hovedytelse> hovedytelser) {
        return getBelopString(hovedytelser.stream()
                .map(Hovedytelse::getNettoUtbetalt)
                .collect(sumDouble));
    }

    private String lagYtelseSkjermleserOverskrift(Hovedytelse hovedytelse) {
        if (!hovedytelse.isErHovedUtbetaling()) {
            return lagVisningUtbetalingsdato(hovedytelse.getHovedytelsedato()) + " : " + hovedytelse.getYtelse();
        }
        return "";
    }

    private Label createHovedtytelseSkjermleserOverskrift(Hovedytelse hovedytelse) {
        Label skjermleserLabel = new Label("utbetalingSkjermleserOverskrift", lagYtelseSkjermleserOverskrift(hovedytelse));
        skjermleserLabel.setMarkupId("skjermleser-hovedytelse-" + hovedytelse.getId());
        return skjermleserLabel;
    }

    private ListView<Hovedytelse> createUtbetalingListView(Hovedutbetaling hovedutbetaling, List<Hovedytelse> utbetalingsliste) {
        return new ListView<Hovedytelse>("hovedytelser", utbetalingsliste) {
            @Override
            protected void populateItem(ListItem<Hovedytelse> item) {

                UtbetalingPanel utbetalingPanel = new UtbetalingPanel("ytelseUtbetaling", new UtbetalingVM(item.getModelObject()));
                if (hovedutbetaling.skalViseHovedutbetaling()) {
                    utbetalingPanel.add(append("aria-describedby", "skjermleser-hovedytelse-" + hovedutbetaling.getId()));
                } else {
                    utbetalingPanel.add(append("aria-describedby", "skjermleser-hovedutbetaling-" + hovedutbetaling.getId()));
                }
                item.add(createHovedtytelseSkjermleserOverskrift(item.getModelObject()));
                item.add(utbetalingPanel);
            }
        };
    }

}
