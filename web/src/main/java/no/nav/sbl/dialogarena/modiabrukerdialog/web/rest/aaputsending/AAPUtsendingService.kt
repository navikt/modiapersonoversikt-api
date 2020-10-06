package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.aaputsending

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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

const val RPA_IDENT = "Z999999"
const val RPA_ENHET = "2830" //4151

const val MELDING_FRITEKST = """
Det har dessverre skjedd en teknisk feil som medfører at utbetalingen din bli forsinket.
Vi beklager dette, og vi jobber nå med å rette opp feilen.
Pengene vil være inne på din konto i løpet av denne uken.
"""
const val MELDING_TILKNYTTETANSATT = false
const val MELDING_TEMAKODE = "AAP"
const val MELDING_TEMAGRUPPE = "ARBD"

class AAPUtsendingService(
        private val sakerService: SakerService,
        private val henvendelseService: HenvendelseUtsendingService,
        private val leaderElection: LeaderElectionClient
) {
    private val hasStarted = AtomicBoolean(false)
    private val total = AtomicInteger(0)
    private val prosessCount = AtomicInteger(0)
    private val executor = Executors.newSingleThreadExecutor()
    private var job: Future<*>? = null

    data class Status(
            val hasStarted: Boolean,
            val total: Int,
            val processed: Int,
            val isDone: Boolean,
            val isCancelled: Boolean,
            val isLeader: Boolean,
            val hostname: String
    )

    fun status() = Status(
            hasStarted = hasStarted.get(),
            total = total.get(),
            processed = prosessCount.get(),
            isDone = job?.isDone ?: false,
            isCancelled = job?.isCancelled ?: false,
            isLeader = leaderElection.isLeader,
            hostname = InetAddress.getLocalHost().hostName
    )

    fun utsendingAAP(fnrs: List<String>): Status {
        if (!leaderElection.isLeader) {
            return status()
        }
        if (hasStarted.getAndSet(true)) {
            return status()
        }

        total.set(fnrs.size)
        job = executor.submit {
            fnrs.forEach { fnr ->
                sendSingle(fnr)
                prosessCount.incrementAndGet()
                Thread.sleep(500)
            }
        }

        return status()
    }

    private fun sendSingle(fnr: String) {
        val saker: List<Sak> = sakerService.hentSammensatteSaker(fnr)
        val sak: Sak = saker.find { it.temaKode == MELDING_TEMAKODE }
                ?: throw IllegalStateException("Fant ikke $MELDING_TEMAKODE sak for $fnr")

        val requestContext = RequestContext(
                fnr = fnr,
                ident = RPA_IDENT,
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
