package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.common.nais.NaisYamlUtils;
import no.nav.common.test.SystemProperties;
import org.springframework.boot.SpringApplication;

public class MainTest {
    public static void main(String[] args) {
        System.setProperty("NAIS_APP_NAME", "modiapersonoversikt-api");
        SystemProperties.setFrom(".vault.properties");
        NaisYamlUtils.loadFromYaml(".nais/nais-q0.yml");

        SpringApplication application = new SpringApplication(Main.class);
        application.setAdditionalProfiles("local");
        application.run(args);
    }
}
