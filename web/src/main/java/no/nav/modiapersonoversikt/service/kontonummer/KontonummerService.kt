package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.common.types.identer.Fnr
import java.time.LocalDateTime

interface KontonummerService {
    data class Konto(
        val kontonummer: String,
        val banknavn: String?,
        val sistEndret: SistEndret?,
        val swift: String? = null,
        val adresse: Adresse? = null,
        val bankkode: String? = null,
        val landkode: String? = null,
        val valutakode: String? = null,
    )
    data class SistEndret(
        val ident: String,
        val tidspunkt: LocalDateTime
    )
    data class Adresse(
        val linje1: String,
        val linje2: String? = null,
        val linje3: String? = null,
    )

    fun hentKontonummer(fnr: Fnr): Konto?
}
