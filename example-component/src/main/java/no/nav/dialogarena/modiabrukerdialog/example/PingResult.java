package no.nav.dialogarena.modiabrukerdialog.example;

public class PingResult {
    public final static String SERVICE_OK = "service_ok";
    public final static String SERVICE_FAIL = "service_fail";
    private String serviceName;
    private long elapsedTime;
    private String status;

    public PingResult(String name, String status, long time){
        this.serviceName = name;
        this.status = status;
        this.elapsedTime = time;
    }

    public String getServiceName(){
        return serviceName;
    }

    public long getElapsedTime(){
        return elapsedTime;
    }

    public String getServiceStatus(){
        return status;
    }


}
