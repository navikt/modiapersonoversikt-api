package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationTestContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.CacheConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.common.MDCOperations.MDC_CALL_ID;
import static no.nav.modig.common.MDCOperations.generateCallId;
import static no.nav.modig.common.MDCOperations.putToMDC;
import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class, CacheConfig.class, WicketTesterConfig.class})
public class SideBarTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    @Before
    public void setupMDC() {
        putToMDC(MDC_CALL_ID, generateCallId());
    }

    @Test
    public void skalOppretteSidebarUtenOppgaveId() {
        Parameters param = new Parameters();
        param.pageParameters.set("fnr", "03054549872");
        fluentWicketTester.goTo(Intern.class, param)
                .should().containComponent(both(ofType(SideBar.class)).and(thatIsVisible()));
    }

}
