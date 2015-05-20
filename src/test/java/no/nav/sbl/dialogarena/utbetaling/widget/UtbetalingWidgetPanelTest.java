package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static org.mockito.Mockito.mock;

public class UtbetalingWidgetPanelTest extends AbstractWicketTest{

    @Override
    protected void setup() {
        UtbetalingWidgetPanel utbetalingWidgetPanel = new UtbetalingWidgetPanel("panel", new Model<>(mock(HovedytelseVM.class)));
        wicketTester.goToPageWith(utbetalingWidgetPanel);
    }

    @Test
    public void shouldContainCorrectAmountOfComponents() {
        wicketTester.should().containComponents(5, ofType(Label.class));
    }

    @Test
    public void overskriftViserDatoPaaKorrektFormat(){
        HovedytelseVM hovedytelseVM = lagHovedYtelse();
        UtbetalingWidgetPanel utbetalingWidgetPanel = new UtbetalingWidgetPanel("panel", new Model<>(hovedytelseVM));

        wicketTester.goToPageWith(utbetalingWidgetPanel);
        wicketTester.should().containPatterns("18. februar 2015");
    }

    private HovedytelseVM lagHovedYtelse(){
        return new HovedytelseVM(
                new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "123")
                    .with(Hovedytelse.hovedytelsedato, new DateTime().withDate(2015, 2, 18))
                    .with(Hovedytelse.ytelsesperiode, new Interval(
                            new DateTime().withDate(2015,2,1),
                            new DateTime().withDate(2015,3,1)))
                    .with(Hovedytelse.utbetaltTilKonto, "123")
                    .with(Hovedytelse.utbetaltTil, new Record<Aktoer>().with(Aktoer.aktoerId, "12345678910"))
        );
    }

}
