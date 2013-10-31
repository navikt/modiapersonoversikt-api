package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;


public class MockPingable implements Pingable {

    private String navn;

    public MockPingable(String navn) {
        this.navn = navn;
    }

    @Override
    public List<PingResult> ping() {
        long start = currentTimeMillis();
        return asList(new PingResult(navn, PingResult.ServiceResult.SERVICE_FAIL, currentTimeMillis() - start));
    }
}
