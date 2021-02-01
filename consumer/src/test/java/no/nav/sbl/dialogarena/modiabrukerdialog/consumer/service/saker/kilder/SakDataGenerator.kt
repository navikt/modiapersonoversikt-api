package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.util.ArrayList

internal class SakDataGenerator {
    companion object {

        fun earlierDateTimeWithOffSet(offset: Long): LocalDateTime = LocalDateTime.now().minusDays(offset)


        fun createSaksliste(): List<SakDto> {
            return ArrayList(listOf(
                    SakDto(id = SakId_1,
                            tema = "AAP",
                            applikasjon = "IT01",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = FagsystemSakId_1,
                            opprettetAv = null,
                            opprettetTidspunkt = earlierDateTimeWithOffSet(4)),

                    SakDto(id = SakId_2,
                            tema = "AGR",
                            applikasjon = "IT01",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = FagsystemSakId_2,
                            opprettetAv = null,
                            opprettetTidspunkt = earlierDateTimeWithOffSet(3)),

                    SakDto(id = SakId_3,
                            tema = "AAP",
                            applikasjon = "IT01",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = FagsystemSakId_3,
                            opprettetAv = null,
                            opprettetTidspunkt = earlierDateTimeWithOffSet(5)),

                    SakDto(id = SakId_4,
                            tema = "STO",
                            applikasjon = "",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = null,
                            opprettetAv = null,
                            opprettetTidspunkt = earlierDateTimeWithOffSet(5))))
        }

        fun lagSak(): Sak {
            val sak = Sak()
            sak.temaKode = "GEN"
            sak.finnesIGsak = false
            sak.fagsystemKode = Sak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
            sak.sakstype = Sak.SAKSTYPE_GENERELL
            sak.opprettetDato = DateTime.now()
            return sak
        }

        fun lagSakUtenFagsystemId(): Sak {
            val sak = Sak()
            sak.temaKode = "STO"
            sak.finnesIGsak = false
            sak.fagsystemKode = ""
            sak.sakstype = Sak.SAKSTYPE_GENERELL
            sak.opprettetDato = DateTime.now()
            return sak
        }


        fun createOppfolgingSaksliste(): MutableList<SakDto> {

            return ArrayList(listOf(
                    SakDto(id = "4",
                            tema = "OPP",
                            applikasjon = "AO01",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = "44",
                            opprettetAv = null,
                            opprettetTidspunkt = earlierDateTimeWithOffSet(0)),

                    SakDto(id = "5",
                            tema = "OPP",
                            applikasjon = "FS22",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = null,
                            opprettetAv = null,
                            opprettetTidspunkt = earlierDateTimeWithOffSet(3))))

        }

        const val VEDTAKSLOSNINGEN = "FS36"
        val FIRE_DAGER_SIDEN = DateTime.now().minusDays(4) //joda.DateTime
        const val FNR = "fnr"
        const val BEHANDLINGSKJEDEID = "behandlingsKjedeId"
        const val SAKS_ID = "123"
        const val SakId_1 = "1"
        const val FagsystemSakId_1 = "11"
        const val SakId_2 = "2"
        const val FagsystemSakId_2 = "22"
        const val SakId_3 = "3"
        const val FagsystemSakId_3 = "33"
        const val SakId_4 = "4"
        const val FagsystemSakId_4 = "44"

    }

}
