package no.nav.sbl.dialogarena.sak.widget;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class SaksoversiktWidgetPanelTest extends AbstractWicketTest {

    @Override
    protected void setup() {
        TemaVM temaVM = new TemaVM().withSistOppdaterteBehandling(new GenerellBehandling().withBehandlingsDato(new DateTime())).withTemaKode("temakode");
        SaksoversiktWidgetPanel widgetPanel = new SaksoversiktWidgetPanel("panel", new Model<>(temaVM));
        wicketTester.goToPageWith(widgetPanel);
    }

    @Test
    public void skalÅpneWidgetUtenFeil() {

    }

}
