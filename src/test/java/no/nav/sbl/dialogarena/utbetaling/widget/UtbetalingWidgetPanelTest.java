package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.junit.Test;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static org.mockito.Mockito.mock;

public class UtbetalingWidgetPanelTest extends AbstractWicketTest{

    @Override
    protected void setup() {
        UtbetalingWidgetPanel utbetalingWidgetPanel = new UtbetalingWidgetPanel("panel", new Model<>(mock(UtbetalingVM.class)));
        wicketTester.goToPageWith(utbetalingWidgetPanel);
    }

    @Test
    public void shouldContainCorrectAmountOfComponents() {
        wicketTester.should().containComponents(5, ofType(Label.class));
    }
}
