package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.modig.core.exception.AuthorizationException
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerServiceImpl
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"

internal class YtelseControllerTest {

    private val sykepengerService: SykepengerServiceBi = mock()
    private val forelderpengerService: ForeldrepengerServiceBi = mock()
    private val pleiepengev1: PleiepengerV1 = mock()
    private val pleiepengerService: PleiepengerService = PleiepengerServiceImpl(pleiepengev1)
    private val unleashService: UnleashService = mock()

    private val controller: YtelseController = YtelseController(sykepengerService, forelderpengerService, pleiepengerService, unleashService)

    @BeforeEach
    fun before() {
        whenever(unleashService.isEnabled(Feature.NYTT_VISITTKORT)).thenReturn(true)
    }

    @Test
    fun `Kaster AuthorizedException hvis ikke tilgang`() {
        whenever(pleiepengev1.hentPleiepengerettighet(any())).thenThrow(HentPleiepengerettighetSikkerhetsbegrensning())
        assertFailsWith<AuthorizationException> { controller.hent(FNR) }
    }

}