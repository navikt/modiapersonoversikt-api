package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.OppgavebehandlingConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.BesvareHenvendelseTjenesteConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.HenvendelseTjenesteConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.SakOgBehandlingTjenesteConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * DirtiesContext er nødvendig her for at InternTest skal kjøre
 * på Jenkins.
 */
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        HenvendelseTjenesteConfig.Test.class,
        SykepengerWidgetMockContext.class,
        SakOgBehandlingTjenesteConfig.Test.class,
        OppgavebehandlingConfig.Test.class,
        BesvareHenvendelseTjenesteConfig.Test.class
})
public class SideBarTest extends WicketPageTest {

    @Test
    public void skalOppretteSidebarUtenOppgaveId() {
        wicket.goTo(Intern.class, with().param("fnr", "03054549872"))
                .should().containComponent(both(ofType(SideBar.class)).and(thatIsVisible()));
    }

}
