package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseResponse;
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
                xmlHenvendelse.setBehandlingskjedeId(behandlingsId);
                XMLMelding xmlMelding = (XMLMelding) xmlHenvendelse.getMetadataListe().getMetadata().get(0);
                xmlHenvendelse.setGjeldendeTemagruppe(xmlMelding.getTemagruppe());
                HENVENDELSER.add(xmlHenvendelse);
                return new WSSendUtHenvendelseResponse().withBehandlingsId(behandlingsId);
            }

            @Override
            public void avbrytHenvendelse(String behandlingsId) {

            }

            @Override
            public String opprettHenvendelse(String type, String fodselsnummer, String behandlingskjedeId) {
                return UUID.randomUUID().toString();
            }

            @Override
            public void ping() {
            }

            @Override
            public WSFerdigstillHenvendelseResponse ferdigstillHenvendelse(WSFerdigstillHenvendelseRequest parameters) {
                XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) parameters.getAny();
                String behandlingsId = parameters.getBehandlingsId();
                xmlHenvendelse.setBehandlingsId(behandlingsId);
                XMLMelding xmlMelding = (XMLMelding) xmlHenvendelse.getMetadataListe().getMetadata().get(0);
                xmlHenvendelse.setGjeldendeTemagruppe(xmlMelding.getTemagruppe());
                HENVENDELSER.add(xmlHenvendelse);
                return new WSFerdigstillHenvendelseResponse();
            }
        };
    }
}
