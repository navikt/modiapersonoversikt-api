package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.ConfigUtil.transformUrlStringToBoolean;


public class MockSetupSingleton {

    private static MockSetupSingleton instance;
    private Boolean tillatMock;

    private MockSetupSingleton() {
    }

    public static MockSetupSingleton mockSetup() {
        instance = (instance == null) ? new MockSetupSingleton() : instance;
        return instance;
    }

    public boolean isTillat() {
        if (tillatMock == null) {
            String tillatStr = getProperty("tillatmocksetup.url", "nei");
            tillatMock = transformUrlStringToBoolean(tillatStr);
        }
        return tillatMock;
    }
}
