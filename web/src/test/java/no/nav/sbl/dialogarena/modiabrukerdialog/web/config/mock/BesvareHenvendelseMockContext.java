package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.sporsmalogsvar.mock.BesvareHenvendelsePortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BesvareHenvendelseMockContext {

    @Bean
    public BesvareHenvendelsePortType besvareSso() {
        return new BesvareHenvendelsePortTypeMock();
    }

}
