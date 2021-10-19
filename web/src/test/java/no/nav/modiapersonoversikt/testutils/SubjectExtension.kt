package no.nav.modiapersonoversikt.testutils

import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.auth.subject.SubjectHandler.setSupplier
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.function.Supplier

class SubjectExtension(
    var subject: Subject? = null
) : BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    SubjectHandler() {

    var original: Supplier<Subject>? = null

    override fun beforeTestExecution(context: ExtensionContext) {
        original = getSupplier()
        setSupplier { subject }
    }

    override fun afterTestExecution(context: ExtensionContext) {
        setSupplier(original)
        original = null
    }
}
