package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.OPPRETTET;
import static org.apache.wicket.model.Model.of;


@RunWith(SpringJUnit4ClassRunner.class)
public class BehandlingsPanelTest extends AbstractWicketTest {

    private BehandlingsPanel behandlingsPanel;

    @Override
    protected void setup() {
        DateTime dato = new DateTime();
        behandlingsPanel = new BehandlingsPanel("id", of(new GenerellBehandling()
                .withOpprettetDato(dato)
                .withBehandlingsDato(dato)
                .withBehandlingStatus(OPPRETTET)
        ));
        wicketTester.goToPageWith(behandlingsPanel);
    }

    @Test
    public void shouldOpen_pageWithComponent() {

    }

}
