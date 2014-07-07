package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendUtHenvendelsePortTypeMock {

    @Bean
    public SendUtHenvendelsePortType sendUtHenvendelsePortType() {
        return createSendUtHenvendelsePortTypeMock();
    }

    public static SendUtHenvendelsePortType createSendUtHenvendelsePortTypeMock() {
        return new SendUtHenvendelsePortType() {

            @Override
            public WSSendUtHenvendelseResponse sendUtHenvendelse(WSSendUtHenvendelseRequest request) {
                return new WSSendUtHenvendelseResponse();
            }

            @Override
            public void ping() {
            }
        };
    }
}
