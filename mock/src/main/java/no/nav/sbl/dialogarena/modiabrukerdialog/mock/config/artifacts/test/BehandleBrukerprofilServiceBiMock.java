package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.test;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;

import static org.mockito.Mockito.mock;

public class BehandleBrukerprofilServiceBiMock {

    public static BehandleBrukerprofilServiceBi getBehandleBrukerprofilServiceBiMock() {
        return mock(BehandleBrukerprofilServiceBi.class);
    }

}
