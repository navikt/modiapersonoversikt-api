package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.aaputsending

import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.leaderelection.LeaderElectionClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog.RequestContext
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog.getKanal
import java.net.InetAddress
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicReference

private const val RPA_ENHET = "2830" //4151
private const val MELDING_FRITEKST = """
Forsinket utbetaling

Det har dessverre skjedd en teknisk feil som medfører at utbetalingen din kan ha blitt forsinket.
Vi beklager dette, og vi jobber nå med å rette opp feilen. Pengene vil blir utbetalt i løpet av denne uken.
Hvis du allerede har mottatt utbetalingen din kan du se bort fra denne meldingen.
"""
private const val MELDING_TILKNYTTETANSATT = false
private const val MELDING_TEMAKODE = "AAP"
private const val MELDING_TEMAGRUPPE = "ARBD"

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
    private val processorReference: AtomicReference<Prosessor<String>?> = AtomicReference(null)

    data class Status(
            val isLeader: Boolean,
            val hostname: String,
            val prosessorStatus: Prosessor.Status<String>
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

    fun utsendingAAP(fnrs: List<String>): Status {
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
                    Prosessor(subject, fnrs) { fnr ->
                        sendHenvendelse(ident, fnr)
                        Thread.sleep(500)
                    }
            )
        }

        return status()
    }

    private fun sendHenvendelse(ident: String, fnr: String) {
        val saker: List<Sak> = sakerService.hentSammensatteSaker(fnr)
        val sak: Sak = saker.find { it.temaKode == MELDING_TEMAKODE }
                ?: throw IllegalStateException("Fant ikke $MELDING_TEMAKODE sak for $fnr")

        val requestContext = RequestContext(
                fnr = fnr,
                ident = ident,
                enhet = RPA_ENHET
        )

        val type = Meldingstype.INFOMELDING_MODIA_UTGAAENDE
        val melding = Melding().withFnr(requestContext.fnr)
                .withNavIdent(requestContext.ident)
                .withEksternAktor(requestContext.ident)
                .withKanal(getKanal(type))
                .withType(type)
                .withFritekst(Fritekst(MELDING_FRITEKST))
                .withTilknyttetEnhet(requestContext.enhet)
                .withErTilknyttetAnsatt(MELDING_TILKNYTTETANSATT)
                .withTemagruppe(MELDING_TEMAGRUPPE)

        henvendelseService.sendHenvendelse(melding, Optional.empty(), Optional.ofNullable(sak), RPA_ENHET)
    }
}
