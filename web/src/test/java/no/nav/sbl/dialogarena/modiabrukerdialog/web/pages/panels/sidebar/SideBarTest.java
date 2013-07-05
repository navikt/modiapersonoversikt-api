package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.common.MDCOperations.MDC_CALL_ID;
import static no.nav.modig.common.MDCOperations.generateCallId;
import static no.nav.modig.common.MDCOperations.putToMDC;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
public class SideBarTest extends TestSecurityBaseClass {

    @Before
    public void setupMDC() {
        putToMDC(MDC_CALL_ID, generateCallId());
    }

    @Test
    public void canCreateSideBar() {
        assertThat(new SideBar("id", "03054549872"), notNullValue());
    }

}
