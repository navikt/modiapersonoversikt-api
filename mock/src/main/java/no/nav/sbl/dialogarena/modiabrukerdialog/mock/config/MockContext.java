package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.KjerneinfoMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.services.SoknaderServiceMock;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KjerneinfoMock.class,
        HenvendelsePortTypeMock.class,
        SoknaderServiceMock.class,
        SakOgBehandlingPortTypeMock.class
})
public class MockContext {

}
