package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
public class SideBarTest extends TestSecurityBaseClass {

    @Test
    public void canCreateSideBar() {
        SideBar s = new SideBar("id", "fnr");
        Assert.assertThat(s, org.hamcrest.Matchers.notNullValue());
    }

}
