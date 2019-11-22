package no.nav.modig.modia.ping;

import no.nav.sbl.dialogarena.types.Pingable;

public class UnpingableWebService implements Pingable {

    private final String name;
    private final String address;

    public UnpingableWebService(String name, String address) {
        assert name.length() > 0;
        this.name = name;
        this.address = address;
    }

    @Override
    public Ping ping() {
        return Ping.avskrudd(new Ping.PingMetadata(
                name,
                address,
                "",
                false
        ));
    }
}
