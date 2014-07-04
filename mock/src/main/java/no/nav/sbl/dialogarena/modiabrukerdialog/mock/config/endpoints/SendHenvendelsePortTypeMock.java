package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSOppdaterHenvendelseJournalforingsInformasjonRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSOppdaterHenvendelseJournalforingsInformasjonResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSOppdaterHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSOppdaterHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendHenvendelsePortTypeMock {

    @Bean
    public SendHenvendelsePortType sendHenvendelsePortType() {
        return createSendHenvendelsePortTypeMock();
    }

    public static SendHenvendelsePortType createSendHenvendelsePortTypeMock() {
        return new SendHenvendelsePortType() {

            @Override
            public WSOppdaterHenvendelseResponse oppdaterHenvendelse(WSOppdaterHenvendelseRequest wsOppdaterHenvendelseRequest) {
                return new WSOppdaterHenvendelseResponse();
            }

            @Override
            public WSSendHenvendelseResponse sendHenvendelse(WSSendHenvendelseRequest request) {
                return new WSSendHenvendelseResponse();
            }

            @Override
            public WSOppdaterHenvendelseJournalforingsInformasjonResponse oppdaterHenvendelseJournalforingsInformasjon(WSOppdaterHenvendelseJournalforingsInformasjonRequest parameters) {
                return null;
            }

            @Override
            public void ping() {
            }
        };
    }
}
