package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class JournalforingsPanelVelgSakTest extends WicketPageTest {

    private final static String FODSELSNR = "52765236723";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;
    @Inject
    private SakerService sakerService;

    private InnboksVM innboksVM;

    @Before
    public void setUp() {
        innboksVM = new InnboksVM(FODSELSNR, henvendelseBehandlingService);
    }

    @Test
    public void skalStarteJournalforingsPanelVelgSakUtenFeil() {
        JournalforingsPanelVelgSak panel = new JournalforingsPanelVelgSak("panel", innboksVM);
        panel.oppdater();
        wicket.goToPageWith(panel);
    }

    @Test
    public void skalJournalforeVedSubmit() {
        JournalforingsPanelVelgSak panel = new JournalforingsPanelVelgSak("panel", innboksVM);
        panel.oppdater();
        wicket
                .goToPageWith(panel)
                .inForm("panel:plukkSakForm")
                .select("valgtTraad.journalfortSak", 0)
                .submitWithAjaxButton(withId("journalforTraad"));

        verify(sakerService).knyttBehandlingskjedeTilSak(anyString(), anyString(), any(Sak.class));
    }

    @Test
    public void skalKreveAtMinstEnSakErValgt() {
        JournalforingsPanelVelgSak journalforingsPanel = new JournalforingsPanelVelgSak("panel", innboksVM);
        journalforingsPanel.oppdater();

        wicket
                .goToPageWith(journalforingsPanel)
                .inForm("panel:plukkSakForm")
                .submitWithAjaxButton(withId("journalforTraad"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, contains(journalforingsPanel.get("plukkSakForm:valgtTraad.journalfortSak").getString("valgtTraad.journalfortSak.Required")));
    }

}
