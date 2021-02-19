package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import java.time.OffsetDateTime
import java.util.ArrayList

internal class SakDataGenerator {
    companion object {

        fun earlierDateTimeWithOffSet(offset: Long): OffsetDateTime = OffsetDateTime.now().minusDays(offset)


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

        const val FNR = "fnr"
        const val SakId_1 = "1"
        const val FagsystemSakId_1 = "11"
        const val SakId_2 = "2"
        const val FagsystemSakId_2 = "22"
        const val SakId_3 = "3"
        const val FagsystemSakId_3 = "33"
        const val SakId_4 = "4"

    }

}

