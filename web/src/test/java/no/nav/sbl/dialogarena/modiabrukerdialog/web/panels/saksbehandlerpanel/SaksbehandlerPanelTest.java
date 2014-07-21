package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {HentPersonPanelMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SaksbehandlerPanelTest extends WicketPageTest {

    @Test
    public void skalStarteSaksbehandlerPanelUtenFeil() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        wicket.goToPageWith(new SaksbehandlerPanel("saksbehandlerPanel"));
    }

}