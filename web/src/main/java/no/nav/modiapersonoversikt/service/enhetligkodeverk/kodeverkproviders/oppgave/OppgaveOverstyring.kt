package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave

import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk.Prioritet.*

object OppgaveOverstyring {

    private const val VURD_HENV = "VURD_HENV"
    private const val VUR_KONS_YTE = "VUR_KONS_YTE"

    val godkjenteOppgavetyper: List<String> = listOf("VURD_HENV", "KONT_BRUK", "VUR_KONS_YTE")

    val underkjenteTemaer: List<String> = listOf("OPA", "SAA", "IAR", "KNA", "OVR", "BII", "MOT", "TIL", "REK", "PER")

    val overstyrtKodeverk = LokalOverstyring.lagKodeverk {
        prioriteter = listOf(HOY, NORM, LAV)
        frist = 2
        tema("AAR") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("AGR") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("AAP") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("BAR") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("BID") {
            prioriteter = listOf(HOY, LAV)
            oppgavetype(VURD_HENV, 0)
        }
        tema("BIL") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("DAG") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("ENF") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("ERS") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("FEI") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("FOR") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("FOS") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("FUL") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("GEN") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("GRA") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("GRU") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("HEL") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("HJE") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("IND") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("KON") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("KTR") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("LGA") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("MED") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("MOB") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("OMS") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("OPP") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("PEN") {
            prioriteter = listOf(HOY, LAV)
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("STO") {
            oppgavetype(VURD_HENV, 1)
        }
        tema("REH") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("RVA") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("RPO") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("SAK") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("SAP") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("SER") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("SIK") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("SUP") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("TSO") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("TSR") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("TRK") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("TRY") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("UFO") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("UFM") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("YRA") {
            oppgavetype(VURD_HENV, 0)
        }
        tema("YRK") {
            oppgavetype(VURD_HENV, 0)
            oppgavetype(VUR_KONS_YTE, 0)
        }
        tema("OKO") {
            prioriteter = listOf(HOY, LAV)
            oppgavetype(VURD_HENV, 0)
        }
    }
}
