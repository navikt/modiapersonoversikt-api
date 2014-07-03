package no.nav.sbl.dialogarena.sak.widget;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.junit.Test;

public class SaksWidgetPanelTest extends AbstractWicketTest {

    @Override
    protected void setup() {
        TemaVM temaVM = new TemaVM().withSistOppdaterteBehandling(new GenerellBehandling().withBehandlingsDato(new DateTime())).withTemaKode("temakode");
        SaksWidgetPanel widgetPanel = new SaksWidgetPanel("panel", new Model<>(temaVM));
        wicketTester.goToPageWith(widgetPanel);
    }

    @Test
    public void skalÅpneWidgetUtenFeil() {

    }

}
