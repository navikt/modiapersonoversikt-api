package no.nav.modiapersonoversikt.kafka

import kotlinx.datetime.Clock
import no.nav.modiapersonoversikt.kafka.dto.HenvendelseKafkaDTO
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDialogController
import no.nav.personoversikt.common.logging.Logging
import org.slf4j.LoggerFactory

interface HenvendelseProducer {
    fun sendHenvendelseUpdate(
        fnr: String,
        tema: String?,
        temagruppe: String,
        traadId: String,
    )

    fun close()
}

class HenvendelseProducerImpl(
    private val temaSomSkalPubliseres: List<String>,
    private val temagruppeSomSkalPubliseres: List<String>,
    private val producer: KafkaPersonoversiktProducer<HenvendelseKafkaDTO>
) : HenvendelseProducer {
    private val logger = LoggerFactory.getLogger(SfLegacyDialogController::class.java)

    override fun sendHenvendelseUpdate(
        fnr: String,
        tema: String?,
        temagruppe: String,
        traadId: String,
    ) {
        val message = HenvendelseKafkaDTO(
            fnr = fnr,
            tema = tema,
            temagruppe = temagruppe,
            traadId = traadId,
            tidspunkt = Clock.System.now()
        )

        if (temaSomSkalPubliseres.contains(message.tema) || temagruppeSomSkalPubliseres.contains(message.temagruppe)) {
            logger.info("Sender henvendelse update med tema $tema og temagruppe $temagruppe")
            try {
                producer.sendRecord(message = message)
            } catch (e: Exception) {
                Logging.secureLog.error("Klarte ikke å sende henvendelse melding: $message", e)
            }
        }
    }

    override fun close() {
        producer.close()
    }
}
