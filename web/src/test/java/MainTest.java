import no.nav.apiapp.ApiApp;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ModiaApplicationContext;
import no.nav.sbl.dialogarena.test.SystemProperties;
import no.nav.testconfig.ApiAppTest;

import static no.nav.testconfig.ApiAppTest.setupTestContext;

public class MainTest {
    public static void main(String[] args) {
        SystemProperties.setFrom("vault.properties");
        SystemProperties.setFrom("configurations/q0.properties");

        setupTestContext(ApiAppTest.Config.builder().applicationName("modiabrukerdialog").build());

        ApiApp.runApp(ModiaApplicationContext.class, new String[]{"8085", "8086"});
    }
}
