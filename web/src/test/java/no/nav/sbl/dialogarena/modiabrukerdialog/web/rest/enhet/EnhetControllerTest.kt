package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.finn.unleash.Unleash
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashServiceImpl
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.util.*
import javax.ws.rs.NotFoundException

class EnhetControllerTest {
    private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service = mock()
    private val unleash: Unleash = mock()
    private val api = "www.unleashurl.com"
    private val controller = EnhetController(
            mock(),
            organisasjonEnhetV2Service,
            UnleashServiceImpl(mock(), unleash, api)
    )

    @BeforeEach
    fun before() {
        whenever(unleash.isEnabled(Feature.NYTT_VISITTKORT_UNLEASH.propertyKey)).thenReturn(true)
    }

    @AfterEach
    fun after() {
        whenever(unleash.isEnabled(Feature.NYTT_VISITTKORT_UNLEASH.propertyKey)).thenReturn(false)
    }

    @Test
    fun `Kaster 404 hvis enhet ikke ble funnet`() {
        whenever(organisasjonEnhetV2Service.finnNAVKontor(Mockito.any(), Mockito.any())).thenReturn(Optional.empty())
        Assertions.assertThrows(NotFoundException::class.java, { controller.finnEnhet("", "") })
    }

}