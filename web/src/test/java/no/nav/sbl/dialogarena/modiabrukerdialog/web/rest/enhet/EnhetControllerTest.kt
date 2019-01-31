package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.ws.rs.NotFoundException

class EnhetControllerTest {
    private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service = mock()
    private val unleashService: UnleashService = mock()
    private val controller = EnhetController(
            mock(),
            organisasjonEnhetV2Service,
            unleashService
    )

    @Test
    fun `Kaster 404 hvis enhet ikke ble funnet`() {
        whenever(organisasjonEnhetV2Service.finnNAVKontor(Mockito.any(), Mockito.any())).thenReturn(Optional.empty())
        Assertions.assertThrows(NotFoundException::class.java, { controller.finnEnhet("", "") })
    }

}