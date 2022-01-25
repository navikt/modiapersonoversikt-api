package no.nav.modiapersonoversikt.service.enhetligkodeverk.lokaltkodeverk

import kotlin.properties.Delegates

private typealias Prioriteter = List<String>
private typealias Frist = Int

object Domener {

    private lateinit var overstyrtKodeverk: Kodeverk

    data class Kodeverk(val frist: Frist, val prioriteter: Prioriteter, val tema: Map<String, Tema>)

    data class Tema(val prioriteter: Prioriteter, val oppgavetyper: Map<String, Oppgavetype>)

    data class Oppgavetype(val frist: Frist)

    fun lagKodeverk(block: KodeverkBuilder.() -> Unit): Kodeverk {
        overstyrtKodeverk = KodeverkBuilder().apply(block).build()
        return overstyrtKodeverk
    }

    fun hentOverstyrtKodeverk() = overstyrtKodeverk

    class KodeverkBuilder {
        var frist by Delegates.notNull<Int>()
        lateinit var prioriteter: Prioriteter
        var temaMap: MutableMap<String, Tema> = mutableMapOf()

        fun tema(navn: String, block: TemaBuilder.() -> Unit): Tema {
            val tema = TemaBuilder(navn).apply(block).build()
            temaMap[navn] = tema
            return tema
        }

        fun build() = Kodeverk(
            frist = frist,
            prioriteter = prioriteter,
            tema = temaMap
        )
    }

    class TemaBuilder(val navn: String) {
        lateinit var prioriteter: Prioriteter
        var oppgavetyperMap: MutableMap<String, Oppgavetype> = mutableMapOf()

        fun oppgavetype(navn: String, block: OppgavetypeBuilder.() -> Unit): Oppgavetype {
            val oppgavetyper = OppgavetypeBuilder(navn).apply(block).build()
            oppgavetyperMap[navn] = oppgavetyper
            return oppgavetyper
        }

        fun build() = Tema(
            prioriteter = prioriteter,
            oppgavetyper = oppgavetyperMap
        )
    }

    class OppgavetypeBuilder(val navn: String) {
        var frist by Delegates.notNull<Int>()

        fun build() = Oppgavetype(
            frist = frist
        )
    }

    init {
        lagKodeverk {
            prioriteter = listOf("HOY", "NORM", "LAV")
            frist = 2
            tema("AAR") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("AGR") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("AAP") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("BAR") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("BID") {
                prioriteter = listOf("HOY", "LAV")
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("BIL") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("DAG") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("ENF") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("ERS") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("FEI") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("FOR") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("FOS") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("FUL") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("GEN") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("GRA") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("GRU") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("HEL") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("HJE") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("IND") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("KON") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("KTR") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("LGA") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("MED") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("MOB") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("OMS") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("OPP") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("PEN") {
                prioriteter = listOf("HOY", "LAV")
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("STO") {
                oppgavetype("Vurder henvendelse") {
                    frist = 1
                }
            }
            tema("REH") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("RVA") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("RPO") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("SAK") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("SAP") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("SER") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("SIK") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("SUP") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("TSO") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("TSR") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("TRK") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("TRY") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("UFO") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("UFM") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("YRA") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
            tema("YRK") {
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
                oppgavetype("Vurder konsekvens for ytelse") {
                    frist = 0
                }
            }
            tema("OKO") {
                prioriteter = listOf("HOY", "LAV")
                oppgavetype("Vurder henvendelse") {
                    frist = 0
                }
            }
        }
    }
}

fun main() {
    println(Domener.hentOverstyrtKodeverk())
}
