import no.nav.apiapp.ApiApp;
import no.nav.common.nais.utils.NaisYamlUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ModiaApplicationContext;
import no.nav.sbl.dialogarena.test.SystemProperties;
import no.nav.testconfig.ApiAppTest;

import static no.nav.testconfig.ApiAppTest.setupTestContext;

public class MainTest {
    public static void main(String[] args) {
        System.setProperty("NAIS_APP_NAME", "modiapersonoversikt-api");
        SystemProperties.setFrom(".vault.properties");
        NaisYamlUtils.loadFromYaml(".nais/nais-q1.yml");

        setupTestContext(ApiAppTest.Config.builder().applicationName("modiabrukerdialog").build());

        ApiApp.runApp(ModiaApplicationContext.class, new String[]{"8085", "8086"});
    }
}
