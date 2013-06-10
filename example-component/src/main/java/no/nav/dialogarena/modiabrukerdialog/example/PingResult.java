package no.nav.dialogarena.modiabrukerdialog.example;

public class PingResult {
    public enum ServiceResult { SERVICE_OK, SERVICE_FAIL};
//    public static final String SERVICE_OK = "service_ok";
//    public static final String SERVICE_FAIL = "service_fail";
    private String serviceName;
    private long elapsedTime;
    private ServiceResult status;

    public PingResult(String name, ServiceResult status, long time) {
        this.serviceName = name;
        this.status = status;
        this.elapsedTime = time;
    }

    public String getServiceName() {
        return serviceName;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public ServiceResult getServiceStatus() {
        return status;
    }


}
