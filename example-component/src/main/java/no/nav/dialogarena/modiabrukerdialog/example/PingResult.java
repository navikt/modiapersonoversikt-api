package no.nav.dialogarena.modiabrukerdialog.example;

public interface PingResult {
    String SERVICE_OK = "service_ok";
    String SERVICE_FAIL = "service_fail";
    String getServiceName();
    long getElapsedTime();
    String getServiceStatus();
}
