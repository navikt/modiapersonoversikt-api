package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.BrukerprofilServiceBiMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AktoerPortTypeMock.class,
        SakOgBehandlingPortTypeMock.class,
        HenvendelseSoknaderPortTypeMock.class,
        UtbetalingPortTypeMock.class,
        HenvendelsePortTypeMock.class,
        SendUtHenvendelsePortTypeMock.class,
        BehandleHenvendelsePortTypeMock.class,
        GsakHentSakslistePortTypeMock.class,
        BehandleJournalV2PortTypeMock.class,
        GsakOppgaveV3PortTypeMock.class,
        GsakOppgavebehandlingV3PortTypeMock.class,
        GsakRutingPortTypeMock.class,
        GosysNavAnsattPortTypeMock.class,
        KodeverkV2PortTypeMock.class
})
public class EndpointMockContext {

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return BrukerprofilServiceBiMock.getBrukerprofilServiceBiMock();
    }
}
