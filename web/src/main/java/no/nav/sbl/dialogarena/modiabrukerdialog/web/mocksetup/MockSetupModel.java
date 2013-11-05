package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import java.io.Serializable;

public class MockSetupModel implements Serializable {

    private String modelId;
    private String serviceName;
    private String useMock;
    private String key;

    public MockSetupModel(String modelId, String serviceName, String key) {
        this.modelId = modelId;
        this.serviceName = serviceName;
        this.key = key;
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

    public String getUseMock() {
        if (useMock == null) {
            useMock = "yes".equalsIgnoreCase(System.getProperty(key)) ? "Ja" : "Nei";
        }
        return useMock;
    }

    public void setUseMock(String useMock) {
        this.useMock = useMock;
    }

    public String getMockProperty() {
        return (useMock != null && useMock.equalsIgnoreCase("Ja")) ? "yes" : "no";
    }
}
