package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import java.io.Serializable;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;

public class MockSetupModel implements Serializable {

    private String serviceName;
    private String key;
    private Boolean useMock;
    private Boolean throwException;

    public MockSetupModel(String serviceName, String key) {
        this.serviceName = serviceName;
        this.useMock = valueOf(getProperty(key, "false"));
        this.key = key;
        this.throwException = valueOf(getProperty(key + ".simulate.error", "false"));
    }

    public String getKey() {
        return key;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMockProperty() {
        return useMock ? "true" : "false";
    }

    public String getThrowException() {
        return throwException ? "true" : "false";
    }
}
