package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class HenvendelseSoknaderPortTypeMock {

    @Bean
    public HenvendelseSoknaderPortType getHenvendelseSoknaderPortTypeMock() {
        HenvendelseSoknaderPortType mock = mock(HenvendelseSoknaderPortType.class);
        return mock;
    }

}
