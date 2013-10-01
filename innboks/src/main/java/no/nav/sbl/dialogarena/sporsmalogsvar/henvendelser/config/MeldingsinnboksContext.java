package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class MeldingsinnboksContext {

        @Inject
        HenvendelsePortType henvendelseWS;

        @Bean
        public HenvendelseService meldingService() {
            return new HenvendelseService.Default(henvendelseWS);
        }
}
