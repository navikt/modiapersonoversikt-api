package no.nav.sbl.dialogarena

import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement

class SubjectRule(var subject: Subject? = null) : MethodRule {
    override fun apply(statement: Statement, p1: FrameworkMethod?, p2: Any?): Statement {
        return object : Statement() {
            override fun evaluate() {
                SubjectHandler.withSubjectProvider({ subject }, statement::evaluate)
            }
        }
    }
}
