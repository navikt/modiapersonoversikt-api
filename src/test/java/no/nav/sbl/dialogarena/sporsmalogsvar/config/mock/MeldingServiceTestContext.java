package no.nav.sbl.dialogarena.sporsmalogsvar.config.mock;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class MeldingServiceTestContext {

    @Bean
    public MeldingService meldingService() {
        return mock(MeldingService.class);
    }

    @Bean
    public HenvendelsePortType henvendelsePortType(){
        return mock(HenvendelsePortType.class);
    }

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType(){
        return mock(BehandleHenvendelsePortType.class);
    }

    @Bean
    public Sak sakWs(){
        return mock(Sak.class);
    }

}
