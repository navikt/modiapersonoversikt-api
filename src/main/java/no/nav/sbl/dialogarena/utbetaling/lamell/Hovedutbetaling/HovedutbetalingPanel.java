package no.nav.sbl.dialogarena.utbetaling.lamell.Hovedutbetaling;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
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
import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collectors.summingDouble;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static org.apache.wicket.AttributeModifier.append;

public class HovedutbetalingPanel extends Panel {

    private static Collector<Double, ?, Double> sumDouble = summingDouble((d) -> d);

    public HovedutbetalingPanel(String id, Hovedutbetaling hovedutbetaling) {
        super(id);

        setMarkupId("hovedutbetaling-" + hovedutbetaling.getId());
        List<Hovedytelse> synligeHovedytelser = hovedutbetaling.getSynligeHovedytelser();
        IModel<Boolean> skalVises = Model.of(hovedutbetaling.skalViseHovedutbetaling());

        if (hovedutbetaling.skalViseHovedutbetaling()) {
            add(new AttributeModifier("tabindex", 0));
            add(new Label("hovedutbetalingSkjermleserOverskrift", createHovedutbetalingSkjermleserOverskrift(hovedutbetaling)));
        } else {
            add(new Label("hovedutbetalingSkjermleserOverskrift", ""));
        }

        add(hasCssClassIf("hovedutbetaling-synlig", skalVises));
        add(
                createHovedutbetalingDetaljPanel(synligeHovedytelser, hovedutbetaling),
                createUtbetalingListView(synligeHovedytelser)
        );
    }

    private String createHovedutbetalingSkjermleserOverskrift(Hovedutbetaling hovedutbetaling) {
        return lagVisningsdato(hovedutbetaling.getHovedytelsesdato()) + " : Diverse ytelser";
    }

    private WebMarkupContainer createHovedutbetalingDetaljPanel(List<Hovedytelse> synligeHovedytelser, Hovedutbetaling hovedutbetaling) {

        WebMarkupContainer hovedutbetalingDetaljPanel = new WebMarkupContainer("hovedutbetalingDetaljPanel");
        hovedutbetalingDetaljPanel.add(
                new Label("utbetalingDato", lagVisningsdato(hovedutbetaling.getHovedytelsesdato())),
                createStatusLabel(hovedutbetaling),
                new Label("ytelse", "Diverse ytelser"),
                new Label("belop", finnSumAvHovedytelser(synligeHovedytelser))
        );

        hovedutbetalingDetaljPanel.setVisibilityAllowed(hovedutbetaling.skalViseHovedutbetaling());
        return hovedutbetalingDetaljPanel;
    }

    private String lagVisningsdato(DateTime visningsdato) {
        if(visningsdato == null) {
            return "Ingen utbetalingsdato";
        }

        return WidgetDateFormatter.date(visningsdato);
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

    private String getHovedutbetalingPanelMarkupId() {
        return getMarkupId();
    }

    private ListView<Hovedytelse> createUtbetalingListView(List<Hovedytelse> utbetalingsliste) {
        return new ListView<Hovedytelse>("hovedytelser", utbetalingsliste) {
            @Override
            protected void populateItem(ListItem<Hovedytelse> item) {

                UtbetalingPanel utbetalingPanel = new UtbetalingPanel("ytelseUtbetaling", new UtbetalingVM(item.getModelObject()));
                utbetalingPanel.add(append("aria-labelledby", getHovedutbetalingPanelMarkupId()));

                item.add(utbetalingPanel);
            }
        };
    }

}
