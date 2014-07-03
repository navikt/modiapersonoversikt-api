package no.nav.sbl.dialogarena.sak.config;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Denne konfigen emulerer MODIA sin og tilbyr
 * stubber av de avhengighetene som kommer derfra
 */
public class ModiaStubConfig {

    @Bean
    public AktoerPortType fodselnummerAktorService() {
        return mock(AktoerPortType.class);
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return mock(SakOgBehandlingPortType.class);
    }

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        return mock(HenvendelseSoknaderPortType.class);
    }

}
