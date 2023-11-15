package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.common.types.identer.Fnr

class KontonummerServiceImpl(
    private val kontonummerRegisterService: KontonummerRegisterService,
) : KontonummerService {
    override fun hentKontonummer(fnr: Fnr): KontonummerService.Konto? = kontonummerRegisterService.hentKontonummer(fnr)
}
