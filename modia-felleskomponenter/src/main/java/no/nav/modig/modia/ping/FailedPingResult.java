package no.nav.modig.modia.ping;

public class FailedPingResult extends PingResult{


    private final Throwable throwable;

    public FailedPingResult(Throwable throwable, long time) {
        super(ServiceResult.SERVICE_FAIL, time);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
