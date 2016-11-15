package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedutbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.Collections;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static org.mockito.Mockito.mock;

public class UtbetalingWidgetPanelTest extends AbstractWicketTest{

    @Override
    protected void setup() {
        UtbetalingWidgetPanel utbetalingWidgetPanel = new UtbetalingWidgetPanel("panel", new Model<>(mock(HovedutbetalingVM.class)));
        wicketTester.goToPageWith(utbetalingWidgetPanel);
    }

    @Test
    public void shouldContainCorrectAmountOfComponents() {
        wicketTester.should().containComponents(5, ofType(Label.class));
    }

    @Test
    public void overskriftViserDatoPaaKorrektFormat(){
        HovedutbetalingVM hovedutbetalingVM = lagHovedutbetaling();
        UtbetalingWidgetPanel utbetalingWidgetPanel = new UtbetalingWidgetPanel("panel", new Model<>(hovedutbetalingVM));

        wicketTester.goToPageWith(utbetalingWidgetPanel);
        wicketTester.should().containPatterns("18. februar 2015");
    }

    private HovedutbetalingVM lagHovedutbetaling(){
        return new HovedutbetalingVM(
                new Hovedutbetaling()
                        .withId("123")
                        .withHovedytelsesdato(new DateTime().withDate(2015, 2, 18))
                        .withHovedytelser(Collections.singletonList(lagHovedytelse()))
                        .withIsUtbetalt(true)
                        .withUtbetalingStatus("Utbetalt")
                        .settUtbetaltSum()
        );
    }

    private Hovedytelse lagHovedytelse() {
        return new Hovedytelse()
                .withId("123")
                .withHovedytelsedato(new DateTime().withDate(2015, 2, 18))
                .withYtelsesperiode(new Interval(
                        new DateTime().withDate(2015,2,1),
                        new DateTime().withDate(2015,3,1)))
                .withUtbetaltTilKonto("123")
                .withUtbetaltTil(new Aktoer().withAktoerId("***REMOVED***"));
    }

}
