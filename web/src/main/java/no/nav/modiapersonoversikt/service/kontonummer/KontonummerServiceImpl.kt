package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService

class KontonummerServiceImpl(
    private val kontonummerRegisterService: KontonummerRegisterService,
    private val tpsKontonummerService: TpsKontonummerService,
    private val unleash: UnleashService,
) : KontonummerService {
    override fun hentKontonummer(fnr: Fnr): KontonummerService.Konto? {
        return if (unleash.isEnabled(Feature.USE_REST_KONTOREGISTER.propertyKey)) {
            kontonummerRegisterService.hentKontonummer(fnr)
        } else {
            tpsKontonummerService.hentKontonummer(fnr)
        }
    }
}
