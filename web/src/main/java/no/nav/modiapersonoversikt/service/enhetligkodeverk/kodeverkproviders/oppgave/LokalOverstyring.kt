package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave

import kotlin.properties.Delegates

private typealias Prioriteter = List<OppgaveKodeverk.Prioritet>
private typealias Frist = Int

object LokalOverstyring {
    data class Kodeverk(
        val frist: Frist,
        val prioriteter: Prioriteter,
        val tema: Map<String, Tema>,
    )

    data class Tema(
        val prioriteter: Prioriteter?,
        val oppgavetyper: Map<String, Oppgavetype>,
    )

    data class Oppgavetype(
        val frist: Frist,
    )

    fun lagKodeverk(block: KodeverkBuilder.() -> Unit): Kodeverk = KodeverkBuilder().apply(block).build()

    class KodeverkBuilder {
        var frist by Delegates.notNull<Int>()
        var prioriteter: Prioriteter = mutableListOf()
        private var temaMap: MutableMap<String, Tema> = mutableMapOf()

        fun tema(
            navn: String,
            block: TemaBuilder.() -> Unit,
        ) {
            temaMap[navn] = TemaBuilder(navn).apply(block).build()
        }

        fun build() =
            Kodeverk(
                frist = frist,
                prioriteter = prioriteter,
                tema = temaMap,
            )
    }

    class TemaBuilder(
        val navn: String,
    ) {
        var prioriteter: Prioriteter? = null
        private var oppgavetyperMap: MutableMap<String, Oppgavetype> = mutableMapOf()

        fun oppgavetype(
            navn: String,
            frist: Frist,
        ) {
            oppgavetyperMap[navn] = Oppgavetype(frist)
        }

        fun build() =
            Tema(
                prioriteter = prioriteter,
                oppgavetyper = oppgavetyperMap,
            )
    }
}
