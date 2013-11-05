package no.nav.sbl.dialogarena.modiabrukerdialog.web.mocksetup;


import java.io.Serializable;

public class MockSetupModel implements Serializable {

    private String modelId;
    private String serviceName;
    private String useMock;

    public MockSetupModel(String modelId, String serviceName, String useMock) {
        this.modelId = modelId;
        this.serviceName = serviceName;
        this.useMock = useMock;
    }

    public String getModelId() {
        return modelId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUseMock() {
        return useMock;
    }

    public void setUseMock(String useMock) {
        this.useMock = useMock;
    }
}
