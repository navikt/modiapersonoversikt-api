package no.nav.modiapersonoversikt

import io.getunleash.DefaultUnleash
import io.mockk.every
import io.mockk.mockk
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.modiapersonoversikt.consumer.kontoregister.generated.apis.KontoregisterV1Api
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.spokelse.SpokelseClient
import no.nav.modiapersonoversikt.consumer.spokelse.SpokelseClientMock
import no.nav.modiapersonoversikt.consumer.tiltakspenger.TiltakspengerService
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.VeilarbvedtaksstotteService
import org.springframework.context.support.beans

val localBeans =
    beans {
        profile("local") {
            bean<KontoregisterV1Api>(isPrimary = true) { mockk() }
            bean<NorgApi>(isPrimary = true) { mockk() }

            bean<MachineToMachineTokenClient>(isPrimary = true) { mockk() }
            bean<OnBehalfOfTokenClient>(isPrimary = true) { mockk() }
            bean<SpokelseClient>(isPrimary = true) { SpokelseClientMock() }
            bean<DefaultUnleash>(isPrimary = true) {
                mockk {
                    every { isEnabled(any()) } returns true
                }
            }
            bean<VeilarbvedtaksstotteService>(isPrimary = true) { mockk() }
            bean<TiltakspengerService>(isPrimary = true) { mockk() }
        }
    }
