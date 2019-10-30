package no.nav.modig.modia.ping;

public class OkPingResult extends PingResult {

    public OkPingResult(long time) {
        super(ServiceResult.SERVICE_OK, time);
    }
}
