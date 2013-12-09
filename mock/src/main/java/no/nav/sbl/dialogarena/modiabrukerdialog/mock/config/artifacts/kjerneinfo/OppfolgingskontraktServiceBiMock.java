package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;

import static org.mockito.Mockito.mock;

public class OppfolgingskontraktServiceBiMock {

    public static OppfolgingskontraktServiceBi getOppfolgingskontraktServiceBiMock() {
        return mock(OppfolgingskontraktServiceBi.class);
    }

}
