package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@ContextConfiguration(classes = {ServiceTestContext.class})
@DirtiesContext(classMode = AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
public class JournalforingsPanelTest extends WicketPageTest {

    @Inject
    private SakerService sakerService;
    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Test
    public void viserTekniskFeilHvisSakerServiceTryner() throws Exception {
        doThrow(Exception.class).when(sakerService).hentListeAvSaker(anyString());
        doThrow(Exception.class).when(sakerService).hentSaker(anyString());

        JournalforingsPanel journalforingsPanel = new JournalforingsPanel("id", new InnboksVM("", henvendelseBehandlingService));
        journalforingsPanel.oppdatereJournalforingssaker();

        wicket.goToPageWith(journalforingsPanel)
                .should().containComponent(thatIsVisible().and(withId("tekniskFeilContainer")))
                .should().containComponent(thatIsInvisible().and(withId("journalforingsPanelEnkeltSak")))
                .should().containComponent(thatIsInvisible().and(withId("journalforingsPanelVelgSak")));
    }
}