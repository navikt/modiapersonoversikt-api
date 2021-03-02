package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.aaputsending

import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.leaderelection.LeaderElectionClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saker.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog.getKanal
import java.net.InetAddress
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicReference

private const val MELDING_FRITEKST = """
Vi skriver til deg fordi du tidligere har mottatt arbeidsavklaringspenger (AAP) som arbeidssøker. 
Det er nå gjort en endring i det midlertidige regelverket som gjelder under koronapandemien. 
Dersom du fortsatt oppfyller vilkårene for å få AAP som arbeidssøker, 
kan du å få innvilget en fornyet periode med AAP som arbeidssøker fra 1. mars og til og med 30. juni. 
Du må søke om dette senest 1. april for å få ytelsen fra 1. mars. 
Hvis du søker etter 1. april, får du fra den dagen du søker og til og med 30. juni. 
Du må være registrert som arbeidssøker og sende meldekort som vanlig.  

Hvis du ønsker å søke om en ny periode med AAP som arbeidssøker: 
Svar med denne teksten: Jeg søker om AAP som arbeidssøker fra 1. mars (evt. annen dato). 

Hvis du ikke ønsker å søke: 
Svar: Nei
"""
private const val MELDING_TILKNYTTETANSATT = false
private const val MELDING_TEMAKODE = "AAP"
private const val MELDING_TEMAGRUPPE = "ARBD"

class FnrEnhet(val fnr: String, val enhet: String)

class Prosessor<S>(
        private val subject: Subject,
        private val list: Collection<S>,
        private val block: (s: S) -> Unit
) {
    private val executor = Executors.newSingleThreadExecutor()
    private var job: Future<*>? = null
    private val errors: MutableList<Pair<S, Throwable>> = mutableListOf()
    private val success: MutableList<S> = mutableListOf()

    data class Status<S>(
            val isRunning: Boolean,
            val isDone: Boolean,
            val processed: Int,
            val total: Int,
            val success: List<S>,
            val errors: List<Pair<S, Throwable>>
    )

    init {
       job = executor.submit {
            SubjectHandler.withSubject(subject) {
                list.forEach { element ->
                    try {
                        block(element)
                        success.add(element)
                    } catch (throwable: Throwable) {
                        errors.add(element to throwable)
                    }
                }
            }
        }
    }

    fun getStatus() = Status(
            isRunning = job != null,
            isDone = job?.isDone ?: false,
            processed = success.size + errors.size,
            total = list.size,
            success = success,
            errors = errors
    )
}

class AAPUtsendingService(
        private val sakerService: SakerService,
        private val henvendelseService: HenvendelseUtsendingService,
        private val leaderElection: LeaderElectionClient
) {
    private val processorReference: AtomicReference<Prosessor<FnrEnhet>?> = AtomicReference(null)

    data class Status(
            val isLeader: Boolean,
            val hostname: String,
            val prosessorStatus: Prosessor.Status<FnrEnhet>
    )

    fun status(): Status {
        val processor = processorReference.get()
        if (processor != null) {
            return Status(
                    isLeader = leaderElection.isLeader,
                    hostname = InetAddress.getLocalHost().hostName,
                    prosessorStatus = processor.getStatus()
            )
        }

        return Status(
                isLeader = leaderElection.isLeader,
                hostname = InetAddress.getLocalHost().hostName,
                prosessorStatus = Prosessor.Status(
                        isRunning = false,
                        isDone = false,
                        processed = -1,
                        total = -1,
                        success = emptyList(),
                        errors = emptyList()
                )
        )
    }

    fun reset(): Status {
        synchronized(processorReference) {
            if (processorReference.get()?.getStatus()?.isDone == true) {
                processorReference.set(null)
            }
        }
        return status()
    }

    fun utsendingAAP(data: List<FnrEnhet>): Status {
        if (!leaderElection.isLeader) {
            return status()
        }

        synchronized(processorReference) {
            if (processorReference.get() != null) {
                return status()
            }
            val subject = SubjectHandler.getSubject().orElseThrow { IllegalStateException("Fant ikke subject") }
            val ident = subject.uid

            processorReference.set(
                    Prosessor(subject, data) { element ->
                        sendHenvendelse(ident, element)
                        Thread.sleep(500)
                    }
            )
        }

        return status()
    }

    private fun sendHenvendelse(ident: String, data: FnrEnhet) {
        val fnr = data.fnr
        val enhet = data.enhet
        val saker: List<Sak> = sakerService.hentSammensatteSaker(fnr)
        val sak: Sak = saker.find { it.temaKode == MELDING_TEMAKODE }
                ?: throw IllegalStateException("Fant ikke $MELDING_TEMAKODE sak for $fnr")

        val type = Meldingstype.SPORSMAL_MODIA_UTGAAENDE
        val melding = Melding().withFnr(fnr)
                .withNavIdent(ident)
                .withEksternAktor(ident)
                .withKanal(getKanal(type))
                .withType(type)
                .withFritekst(Fritekst(MELDING_FRITEKST))
                .withTilknyttetEnhet(enhet)
                .withErTilknyttetAnsatt(MELDING_TILKNYTTETANSATT)
                .withTemagruppe(MELDING_TEMAGRUPPE)

        henvendelseService.sendHenvendelse(melding, Optional.empty(), Optional.ofNullable(sak), enhet)
    }
}
