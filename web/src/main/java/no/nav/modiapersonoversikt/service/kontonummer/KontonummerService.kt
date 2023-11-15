package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.common.types.identer.Fnr
import java.time.LocalDateTime

interface KontonummerService {
    data class Konto(
        val kontonummer: String,
        val banknavn: String?,
        val sistEndret: LocalDateTime?,
        val swift: String? = null,
        val adresse: Adresse? = null,
        val bankkode: String? = null,
        val landkode: String? = null,
        val valutakode: String? = null,
        val kilde: String? = null,
        val opprettetAv: String,
    )

    data class Adresse(
        val linje1: String,
        val linje2: String? = null,
        val linje3: String? = null,
    )

    fun hentKontonummer(fnr: Fnr): Konto?
}
