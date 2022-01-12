package no.nav.modiapersonoversikt.testutils

import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement

class AuthContextRule(var subject: AuthContext? = null) : MethodRule {
    override fun apply(statement: Statement, p1: FrameworkMethod?, p2: Any?): Statement {
        return object : Statement() {
            override fun evaluate() {
                AuthContextHolderThreadLocal.instance().withContext(subject, statement::evaluate)
            }
        }
    }
}
