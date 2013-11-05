package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import java.io.Serializable;

public class MockSetupModel implements Serializable {

    private String modelId;
    private String serviceName;
    private String key;
    private Boolean useMock;

    public MockSetupModel(String modelId, String serviceName, String key) {
        this.modelId = modelId;
        this.serviceName = serviceName;
        this.key = key;
    }

    public Boolean getUseMock() {
        if (useMock == null) {
            useMock = "yes".equalsIgnoreCase(System.getProperty(key, "no")) ? true : false;
        }
        return useMock;
    }

    public void setUseMock(Boolean useMock) {
        this.useMock = useMock;
    }

    public String getKey() {
        return key;
    }

    public String getModelId() {
        return modelId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMockProperty() {
        return useMock ? "yes" : "no";
    }
}
