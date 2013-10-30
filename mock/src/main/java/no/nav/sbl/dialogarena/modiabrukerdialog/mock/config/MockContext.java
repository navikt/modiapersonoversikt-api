package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.KjerneinfoMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BesvareHenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.OppgavebehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.sbl.dialogarena.soknader.liste.config.SoknaderConfig;
import no.nav.sbl.dialogarena.utbetaling.config.UtbetalingConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KjerneinfoMock.class,
        HenvendelsePortTypeMock.class,
        SoknaderConfig.class,
        SakOgBehandlingPortTypeMock.class,
        OppgavebehandlingPortTypeMock.class,
        BesvareHenvendelsePortTypeMock.class,
        UtbetalingPortTypeMock.class,
        UtbetalingConfig.class
})
public class MockContext {

    public static final String FODSELSNUMMER = "23067911223";

}
