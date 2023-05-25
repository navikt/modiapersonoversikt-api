package no.nav.modiapersonoversikt.kafka

import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.kafka.dto.HenvendelseKafkaDTO
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.junit.jupiter.api.*
import java.time.OffsetDateTime
import kotlin.test.assertEquals

private const val TOPIC = "test_topic"
private val TEMA_SOM_SKAL_PUBLISERES = listOf("SYK")

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HenvendelseProducerTest {
    var mockProducer: MockProducer<String, String>? = null
    var henvendelseProducer: HenvendelseProducerImpl? = null

    @BeforeEach
    fun setUp() {
        mockProducer = MockProducer(
            true,
            StringSerde().serializer(),
            StringSerde().serializer()
        )

        val personoversiktProducer = KafkaPersonoversiktProducerImpl(
            producer = mockProducer!!,
            topic = TOPIC,
            messageSerializer = HenvendelseKafkaDTO.serializer()
        )
        henvendelseProducer = HenvendelseProducerImpl(
            temaSomSkalPubliseres = TEMA_SOM_SKAL_PUBLISERES,
            producer = personoversiktProducer
        )
    }

    @AfterEach
    fun tearDown() {
        mockProducer = null
        henvendelseProducer = null
    }

    @Test
    fun `henvendelser blir sendt til kafka`() {
        val henvendelse = HenvendelseDTO(
            henvendelseType = HenvendelseDTO.HenvendelseType.CHAT,
            fnr = "10108000398",
            aktorId = "10108000398",
            opprettetDato = OffsetDateTime.parse("2023-05-25T07:57:05.884615Z"),
            kontorsperre = false,
            feilsendt = false,
            kjedeId = "",
            gjeldendeTema = "SYK"
        )

        henvendelseProducer!!.sendHenvendelseUpdate(henvendelse = henvendelse)

        val message = mockProducer!!.history().get(0)

        assertEquals(mockProducer!!.history().size, 1)
        assertEquals<String>(
            "{\"fnr\":\"10108000398\",\"tema\":\"SYK\",\"tidspunkt\":\"2023-05-25T07:57:05.884615Z\"}",
            message.value()
        )
    }

    @Test
    fun `henvendelser som ikke har riktig tema blir ikke sendt til kafka`() {
        val henvendelse = HenvendelseDTO(
            henvendelseType = HenvendelseDTO.HenvendelseType.CHAT,
            fnr = "10108000398",
            aktorId = "10108000398",
            opprettetDato = OffsetDateTime.now(),
            kontorsperre = false,
            feilsendt = false,
            kjedeId = "",
            gjeldendeTema = "AAP"
        )

        henvendelseProducer!!.sendHenvendelseUpdate(henvendelse = henvendelse)

        assertEquals(mockProducer!!.history().size, 0)
    }

    @Test
    fun `henvendelser som ikke har tema blir ikke sendt til kafka`() {
        val henvendelse = HenvendelseDTO(
            fnr = "10108000398",
            henvendelseType = HenvendelseDTO.HenvendelseType.CHAT,
            aktorId = "10108000398",
            opprettetDato = OffsetDateTime.now(),
            kontorsperre = false,
            feilsendt = false,
            kjedeId = "",
            gjeldendeTema = null
        )

        henvendelseProducer!!.sendHenvendelseUpdate(henvendelse = henvendelse)

        assertEquals(mockProducer!!.history().size, 0)
    }
}
