package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock.HENVENDELSER;

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
                XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) request.getAny();
                String behandlingsId = UUID.randomUUID().toString();
                xmlHenvendelse.setBehandlingsId(behandlingsId);
                HENVENDELSER.add(xmlHenvendelse);
                return new WSSendUtHenvendelseResponse().withBehandlingsId(behandlingsId);
            }

            @Override
            public void ping() {
            }
        };
    }
}
