package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.dkif.consumer.DkifServiceBi;

import static org.mockito.Mockito.mock;

public class DkifServiceBiMock {

    public static DkifServiceBi getDkifServiceBiMock() {
        return mock(DkifServiceBi.class);
    }

}
