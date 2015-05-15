package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.BrukerprofilServiceBiMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import({
        AktoerPortTypeMock.class,
        SakOgBehandlingPortTypeMock.class,
        HenvendelseSoknaderPortTypeMock.class,
        UtbetalingPortTypeMock.class,
        HenvendelsePortTypeMock.class,
        SendUtHenvendelsePortTypeMock.class,
        BehandleHenvendelsePortTypeMock.class,
        GsakSakV1PortTypeMock.class,
        GsakOppgaveV3PortTypeMock.class,
        GsakOppgavebehandlingV3PortTypeMock.class,
        GsakRutingPortTypeMock.class,
        GosysNavAnsattPortTypeMock.class,
        KodeverkV2PortTypeMock.class,
        JoarkPortTypeMock.class
})
public class EndpointMockContext {

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return BrukerprofilServiceBiMock.getBrukerprofilServiceBiMock();
    }

    @Bean
    public PropertyResolver propertyResolver() {
        return mock(PropertyResolver.class);
    }
}
