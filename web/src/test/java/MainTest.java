import no.nav.common.nais.NaisYamlUtils;
import no.nav.common.test.SystemProperties;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Main;
import org.springframework.boot.SpringApplication;

public class MainTest {
    public static void main(String[] args) {
        System.setProperty("NAIS_APP_NAME", "modiapersonoversikt-api");
        SystemProperties.setFrom(".vault.properties");
        NaisYamlUtils.loadFromYaml(".nais/nais-q0.yml");

        SpringApplication application = new SpringApplication(Main.class);
        application.setAdditionalProfiles("local");
        application.run(args);

//        setupTestContext(ApiAppTest.Config.builder().environment("q0").applicationName("modiabrukerdialog").build());
//
//        ApiApp.runApp(ModiaApplicationContext.class, new String[]{"8085", "8086"});
    }
}
