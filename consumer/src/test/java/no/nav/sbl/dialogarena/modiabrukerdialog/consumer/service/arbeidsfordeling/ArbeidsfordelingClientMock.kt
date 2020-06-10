package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
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
