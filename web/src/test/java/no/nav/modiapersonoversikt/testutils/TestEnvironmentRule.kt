package no.nav.modiapersonoversikt.testutils

import no.nav.modiapersonoversikt.testutils.TestEnvironmentExtension.Companion.setAsEnvironment
import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement

class TestEnvironmentRule(val testEnvironment: Map<String, String?>) : MethodRule {
    val originalEnvironment: Map<String, String?> = testEnvironment.keys.associateWith(System::getProperty)

    override fun apply(statement: Statement, method: FrameworkMethod, target: Any): Statement {
        return object : Statement() {
            override fun evaluate() {
                testEnvironment.setAsEnvironment()
                statement.evaluate()
                originalEnvironment.setAsEnvironment()
            }
        }
    }
}
