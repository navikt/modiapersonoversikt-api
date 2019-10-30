package no.nav.modig.modia.ping;

public class PingResult {
    public enum ServiceResult { SERVICE_OK, SERVICE_FAIL, UNPINGABLE}
    
    private long elapsedTime;
    private ServiceResult status;

    public PingResult(ServiceResult status, long time) {
        this.status = status;
        this.elapsedTime = time;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public ServiceResult getServiceStatus() {
        return status;
    }


}
