package no.nav.modig.modia.ping;

public class UnpingableWebService implements Pingable {

    private final String name;
    private final String address;

    public UnpingableWebService(String name, String address) {
        assert name.length() > 0;
        this.name = name;
        this.address = address;
    }

    @Override
    public PingResult ping() {
        return new PingResult(PingResult.ServiceResult.UNPINGABLE, 0);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String method() {
        return "-";
    }

    @Override
    public String endpoint() {
        return address;
    }
}
