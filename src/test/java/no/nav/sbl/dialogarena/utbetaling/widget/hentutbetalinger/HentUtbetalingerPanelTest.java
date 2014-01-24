package no.nav.sbl.dialogarena.utbetaling.widget.hentutbetalinger;

import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.util.AjaxIndicator;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;

@RunWith(MockitoJUnitRunner.class)
public class HentUtbetalingerPanelTest extends AbstractWicketTest {

    @Mock
    UtbetalingService utbetalingService;

    @Override
    protected void setup() {
        applicationContext.putBean(utbetalingService);

        HentUtbetalingerPanel hentUtbetalingerPanel = new HentUtbetalingerPanel(new UtbetalingWidget("fnr", "", ""));
        wicketTester.goToPageWith(hentUtbetalingerPanel);
    }

    @Test
    public void shouldContainCorrectAjaxIndicator() {
        wicketTester.should().containComponent(ofType(AjaxIndicator.SnurrepippAjaxLink.class));
    }

    @Test
    public void shouldBeAbleToClickAjaxLink() {
        wicketTester.click().link(ofType(AjaxIndicator.SnurrepippAjaxLink.class));
    }
}
