package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.KjerneinfoMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BesvareHenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OppgavebehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.services.SoknaderServiceMock;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KjerneinfoMock.class,
        HenvendelsePortTypeMock.class,
        SoknaderServiceMock.class,
        SakOgBehandlingPortTypeMock.class,
        OppgavebehandlingPortTypeMock.class,
        BesvareHenvendelsePortTypeMock.class
})
public class MockContext {

    public static final String FODSELSNUMMER = "23067911223";

}
