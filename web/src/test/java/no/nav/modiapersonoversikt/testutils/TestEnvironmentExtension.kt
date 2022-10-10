package no.nav.modiapersonoversikt.testutils

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class TestEnvironmentExtension(private val testEnvironment: Map<String, String?>) :
    BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private val originalEnvironment: Map<String, String?> = testEnvironment.keys.associateWith(System::getProperty)

    override fun beforeTestExecution(context: ExtensionContext) {
        testEnvironment.setAsEnvironment()
    }

    override fun afterTestExecution(context: ExtensionContext) {
        originalEnvironment.setAsEnvironment()
    }

    companion object {
        fun Map<String, String?>.setAsEnvironment() {
            this.forEach { (key, value) ->
                if (value == null) {
                    System.clearProperty(key)
                } else {
                    System.setProperty(key, value)
                }
            }
        }
    }
}
