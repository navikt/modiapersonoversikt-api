package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.OppgavebehandlingConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.HenvendelseinnsynConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.SoknaderConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.BesvareHenvendelseMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareServiceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.FluentWicketTester.with;
import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        KjerneinfoPepMockContext.class,
        WicketTesterConfig.class,
        HenvendelseinnsynConfig.Test.class,
        SykepengerWidgetMockContext.class,
        SoknaderConfig.Test.class,
        OppgavebehandlingConfig.Test.class,
        BesvareServiceConfig.Default.class,
        BesvareHenvendelseMockContext.class
})
public class SideBarTest {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    @Test
    public void skalOppretteSidebarUtenOppgaveId() {
        fluentWicketTester.goTo(Intern.class, with().param("fnr", "03054549872"))
                .should().containComponent(both(ofType(SideBar.class)).and(thatIsVisible()));
    }

}
