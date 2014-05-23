package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelseaktivitet;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.HenvendelseAktivitetV2PortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HenvendelseAktivitetV2EndpointConfig {
    @Bean
    public HenvendelseAktivitetV2PortType henvendelseAktivitetV2PortType() {
        return new HenvendelseAktivitetV2PortTypeImpl();
    }
}
