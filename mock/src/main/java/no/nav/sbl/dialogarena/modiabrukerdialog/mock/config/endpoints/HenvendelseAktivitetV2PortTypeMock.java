package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.HenvendelseAktivitetV2PortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSFerdigstillHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSSendHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSStartHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSStartHenvendelseResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HenvendelseAktivitetV2PortTypeMock {

    @Bean
    public HenvendelseAktivitetV2PortType henvendelseAktivitetV2PortType() {
        return createHenvendelseAktivitetV2PortTypeMock();
    }

    public static HenvendelseAktivitetV2PortType createHenvendelseAktivitetV2PortTypeMock() {
        return new HenvendelseAktivitetV2PortType() {
            @Override
            public WSStartHenvendelseResponse startHenvendelse(WSStartHenvendelseRequest request) {
                return new WSStartHenvendelseResponse();
            }

            @Override
            public void ping() {
            }

            @Override
            public WSFerdigstillHenvendelseResponse ferdigstillHenvendelse(WSFerdigstillHenvendelseRequest request) {
                return new WSFerdigstillHenvendelseResponse();
            }

            @Override
            public WSSendHenvendelseResponse sendHenvendelse(WSSendHenvendelseRequest request) {
                return new WSSendHenvendelseResponse();
            }
        };
    }
}
