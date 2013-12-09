package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;

import static org.mockito.Mockito.mock;

public class KontrakterMock {

    public static YtelseskontraktServiceBi getYtelseskontraktServiceBiMock() {
        return mock(YtelseskontraktServiceBi.class);
    }

    public static OppfolgingskontraktServiceBi getOppfolgingskontraktServiceBi() {
        return mock(OppfolgingskontraktServiceBi.class);
    }
}
