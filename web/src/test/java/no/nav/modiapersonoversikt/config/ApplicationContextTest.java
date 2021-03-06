package no.nav.modiapersonoversikt.config;

import no.nav.common.nais.NaisYamlUtils;
import no.nav.modiapersonoversikt.config.ModiaApplicationContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static no.nav.common.test.ssl.SSLTestUtils.setupKeyAndTrustStore;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {ModiaApplicationContext.class})
public class ApplicationContextTest {

    @BeforeClass
    public static void setupStatic() {
        NaisYamlUtils.loadFromYaml("../.nais/nais-q0.yml");
        setupKeyAndTrustStore();
    }

    @Test
    public void shouldSetupAppContext() {
        System.out.println("Testing that spring-config works");
    }

}
