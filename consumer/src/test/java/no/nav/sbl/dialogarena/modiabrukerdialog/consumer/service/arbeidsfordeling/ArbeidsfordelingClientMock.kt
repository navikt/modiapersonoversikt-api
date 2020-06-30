package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet

class ArbeidsfordelingClientMock {
    companion object {
        @JvmStatic
        fun get(value: List<AnsattEnhet>): ArbeidsfordelingClient {
            val client : ArbeidsfordelingClient = mock()
            try {
                whenever(client.hentArbeidsfordeling(any(), any(), any(), any(), any())).thenReturn(value)
            } catch (e: Exception) {
                println(e)
            }
            return client
        }
    }
}