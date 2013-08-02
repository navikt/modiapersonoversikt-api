package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.mock.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.sporsmalogsvar.mock.SporsmalOgSvarPortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({BesvareSporsmalConfig.Default.class, BesvareSporsmalConfig.DefaultWithoutCNCheck.class, BesvareSporsmalConfig.Test.class})
public class BesvareSporsmalConfig {

    @Profile({"default", "brukerhenvendelserDefault"})
    @Configuration
    @Import({
            JaxWsFeatures.Integration.class,
            ServicesConfig.class
    })
    public static class Default { }

    @Profile({"default", "brukerhenvendelserDefaultWithoutCNCheck"})
    @Configuration
    @Import({
            JaxWsFeatures.Mock.class,
            StsConfig.class,
            ServicesConfig.class
    })
    public static class DefaultWithoutCNCheck { }

    @Profile({"test", "brukerhenvendelserTest"})
    @Configuration
    public static class Test {
        @Bean
        public SporsmalOgSvarPortType sporsmalOgSvarPortType() {
            return new SporsmalOgSvarPortTypeMock();
        }

        @Bean
        public HenvendelsePortTypeMock henvendelsePortType() {
            return new HenvendelsePortTypeMock();
        }

        @Bean
        public MeldingService meldingService() {
            return new MeldingService();
        }
    }
}