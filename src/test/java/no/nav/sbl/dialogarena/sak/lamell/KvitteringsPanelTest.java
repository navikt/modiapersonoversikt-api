package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Dokument;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.HenvendelseType.DOKUMENTINNSENDING;
import static org.apache.wicket.model.Model.of;

@RunWith(SpringJUnit4ClassRunner.class)
public class KvitteringsPanelTest extends AbstractWicketTest {

    private KvitteringsPanel kvitteringsPanel;

    @Override
    protected void setup() {
        Kvittering kvittering = (Kvittering) new Kvittering()
                .withBehandlingsId("1")
                .withBehandlingskjedeId("2")
                .withSkjemanummerRef("ref")
                .withManglendeDokumenter(createDokumenter())
                .withInnsendteDokumenter(createDokumenter())
                .withBehandlingsTema("tema")
                .withBehandlingStatus(AVSLUTTET)
                .withHenvendelseType(DOKUMENTINNSENDING)
                .withBehandlingsDato(new DateTime());
        kvitteringsPanel = new KvitteringsPanel("id", of(kvittering), "12");
        wicketTester.goToPageWith(kvitteringsPanel);
    }

    private List<Dokument> createDokumenter() {
        return new ArrayList<>();
    }

    @Test
    public void shouldOpen_pageWithComponent() {

    }

}

