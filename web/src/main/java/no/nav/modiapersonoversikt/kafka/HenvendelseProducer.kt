package no.nav.modiapersonoversikt.kafka

import kotlinx.datetime.toKotlinInstant
import no.nav.modiapersonoversikt.kafka.dto.HenvendelseKafkaDTO
import no.nav.modiapersonoversikt.rest.dialog.salesforce.SfLegacyDialogController
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.personoversikt.common.logging.Logging
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import java.util.*

interface HenvendelseProducer {
    fun sendHenvendelseUpdate(
        fnr: String,
        tema: String?,
        temagruppe: String,
        traadId: String,
        tidspunkt: OffsetDateTime
    )
}

class HenvendelseProducerImpl(
    private val unleashService: UnleashService,
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
        tidspunkt: OffsetDateTime
    ) {
        if (!unleashService.isEnabled(Feature.SEND_HENVENDELSE_TO_KAFKA)) {
            return
        }
        val message = HenvendelseKafkaDTO(
            fnr = fnr,
            tema = tema,
            temagruppe = temagruppe,
            traadId = traadId,
            tidspunkt = tidspunkt.toInstant().toKotlinInstant()
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
}
