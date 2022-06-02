package no.nav.modiapersonoversikt.testutils

import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class AuthContextExtension(var authcontext: AuthContext? = null) :
    BeforeTestExecutionCallback, AfterTestExecutionCallback {

    var original: AuthContext? = null
    val threadlocal = AuthContextHolderThreadLocal.instance()

    fun setContext(context: AuthContext) {
        threadlocal.setContext(context)
    }

    override fun beforeTestExecution(context: ExtensionContext) {
        original = threadlocal.context.orElse(null)
        threadlocal.setContext(authcontext)
    }

    override fun afterTestExecution(context: ExtensionContext) {
        threadlocal.setContext(original)
        original = null
    }
}
