package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.mock.BesvareHenvendelsePortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({BesvareHenvendelseConfig.Default.class, BesvareHenvendelseConfig.DefaultWithoutCNCheck.class, BesvareHenvendelseConfig.Test.class})
public class BesvareHenvendelseConfig {

    @Profile({"default", "brukerhenvendelserDefault"})
    @Configuration
    @Import({BesvareHenvendelseTjenester.class, JaxWsFeatures.Integration.class})
    public static class Default { }

    @Profile({"brukerhenvendelserDefaultWithoutCNCheck"})
    @Configuration
    @Import({BesvareHenvendelseTjenester.class, JaxWsFeatures.Mock.class})
    public static class DefaultWithoutCNCheck { }

    @Profile({"test", "brukerhenvendelserTest"})
    @Configuration
    public static class Test {
        @Bean
        public BesvareHenvendelsePortType besvareSso() {
            return new BesvareHenvendelsePortTypeMock();
        }
    }
}