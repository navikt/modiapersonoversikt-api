package no.nav.modiapersonoversikt

import io.getunleash.DefaultUnleash
import io.mockk.every
import io.mockk.mockk
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.modiapersonoversikt.consumer.kontoregister.generated.apis.KontoregisterV1Api
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.http.maskinporten.MaskinportenClient
import org.springframework.context.support.beans

val localBeans =
    beans {
        profile("local") {
            bean<KontoregisterV1Api>(isPrimary = true) { mockk() }
            bean<NorgApi>(isPrimary = true) { mockk() }

            bean<MaskinportenClient>(isPrimary = true) { mockk() }
            bean<MachineToMachineTokenClient>(isPrimary = true) { mockk() }
            bean<OnBehalfOfTokenClient>(isPrimary = true) { mockk() }
            bean<DefaultUnleash>(isPrimary = true) {
                mockk {
                    every { isEnabled(any()) } returns true
                }
            }
        }
    }
