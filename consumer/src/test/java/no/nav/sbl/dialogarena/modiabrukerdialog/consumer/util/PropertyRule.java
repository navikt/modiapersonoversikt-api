package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class PropertyRule implements BeforeAllCallback, AfterAllCallback {
    private final String propertyName;
    private final String propertyValue;
    private final String originalValue;

    public PropertyRule(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.originalValue = System.getProperty(propertyName);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        System.setProperty(propertyName, propertyValue);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (originalValue == null) {
            System.clearProperty(propertyName);
        } else {
            System.setProperty(propertyName, originalValue);
        }
    }

}
