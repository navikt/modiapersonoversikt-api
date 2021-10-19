package no.nav.modiapersonoversikt.service.sfhenvendelse

import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.config.endpoint.Utils.withProperty
import no.nav.modiapersonoversikt.legacy.api.domain.norg.EnhetsGeografiskeTilknytning
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.HenvendelseBehandlingApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.HenvendelseInfoApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.JournalApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.NyHenvendelseApi
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.*
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.testutils.SubjectExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class SfHenvendelseServiceImplTest {
    companion object {
        @JvmField
        @RegisterExtension
        val subject = SubjectExtension(Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>())))
    }
    private val henvendelseBehandlingApi: HenvendelseBehandlingApi = mockk()
    private val henvendelseInfoApi: HenvendelseInfoApi = mockk()
    private val henvendelseJournalApi: JournalApi = mockk()
    private val henvendelseOpprettApi: NyHenvendelseApi = mockk()
    private val pdlOppslagService: PdlOppslagService = mockk()
    private val arbeidsfordeling: ArbeidsfordelingV1Service = mockk()
    private val stsService: SystemUserTokenProvider = mockk()
    private val ansattService: AnsattService = mockk()
    private val sfHenvendelseServiceImpl = withProperty("SF_HENVENDELSE_URL", "http://dummy.io") {
        SfHenvendelseServiceImpl(
            henvendelseBehandlingApi,
            henvendelseInfoApi,
            henvendelseJournalApi,
            henvendelseOpprettApi,
            pdlOppslagService,
            arbeidsfordeling,
            ansattService,
            stsService
        )
    }

    @Test
    internal fun `skal fjerne kontorsperrede henvendelser`() {
        every { ansattService.hentAnsattFagomrader(any(), any()) } returns setOf("DAG", "OPP")
        every { arbeidsfordeling.hentGTnummerForEnhet(any()) } returns listOf(
            EnhetsGeografiskeTilknytning().also {
                it.enhetId = 5678
                it.geografiskOmraade = "005678"
            }
        )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any()) } returns listOf(
            dummyHenvendelse.medJournalpost("DAG"),
            dummyHenvendelse.medKontorsperre(enhet = "1234")
        )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        assertThat(henvendelser).hasSize(1)
        assertThat(henvendelser[0].kontorsperre).isFalse()
    }

    @Test
    internal fun `skal fjerne innhold om man ikke har tematilgang`() {
        every { ansattService.hentAnsattFagomrader(any(), any()) } returns setOf("DAG", "OPP")
        every { arbeidsfordeling.hentGTnummerForEnhet(any()) } returns listOf(
            EnhetsGeografiskeTilknytning().also {
                it.enhetId = 5678
                it.geografiskOmraade = "005678"
            }
        )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any()) } returns listOf(
            dummyHenvendelse.medJournalpost("DAG"),
            dummyHenvendelse.medJournalpost("SYK")
        )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        assertThat(henvendelser).hasSize(2)
        assertThat(henvendelser[0].meldinger?.get(0)?.fritekst).isEqualTo("Melding innhold")
        assertThat(henvendelser[1].meldinger?.get(0)?.fritekst).isEqualTo("Du kan ikke se innholdet i denne henvendelsen fordi tråden er journalført på et tema du ikke har tilgang til.")
    }

    @Test
    internal fun `skal fjerne lage dummy innhold om henvendelse er kassert`() {
        every { ansattService.hentAnsattFagomrader(any(), any()) } returns setOf("DAG", "OPP")
        every { arbeidsfordeling.hentGTnummerForEnhet(any()) } returns listOf(
            EnhetsGeografiskeTilknytning().also {
                it.enhetId = 5678
                it.geografiskOmraade = "005678"
            }
        )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any()) } returns listOf(
            dummyHenvendelse.somKassert()
        )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        assertThat(henvendelser).hasSize(1)
        assertThat(henvendelser[0].meldinger?.get(0)?.fritekst).isEqualTo("Innholdet i denne henvendelsen er slettet av NAV.")
    }

    private val dummyHenvendelse = HenvendelseDTO(
        henvendelseType = HenvendelseDTO.HenvendelseType.MELDINGSKJEDE,
        fnr = "12345678910",
        aktorId = "00012345678910",
        opprinneligGT = "010101",
        opprettetDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
        kontorsperre = false,
        feilsendt = false,
        kjedeId = "ABBA12341010101",
        gjeldendeTemagruppe = "ARBD",
        avsluttetDato = null,
        kasseringsDato = null,
        gjeldendeTema = null,
        journalposter = null,
        meldinger = listOf(
            MeldingDTO(
                fritekst = "Melding innhold",
                sendtDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
                fra = MeldingFraDTO(
                    identType = MeldingFraDTO.IdentType.NAVIDENT,
                    ident = "Z123456"
                )
            )
        ),
        markeringer = null
    )

    private fun HenvendelseDTO.medJournalpost(tema: String): HenvendelseDTO {
        val journalpost = JournalpostDTO(
            journalforerNavIdent = "Z123456",
            journalforendeEnhet = "1234",
            journalfortDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
            journalfortTema = tema,
            journalpostId = "1a2sd5a4sd"
        )
        return dummyHenvendelse.copy(
            journalposter = if (this.journalposter == null) listOf(journalpost) else this.journalposter!!.plus(journalpost)
        )
    }

    private fun HenvendelseDTO.medKontorsperre(enhet: String? = null, gt: String? = null): HenvendelseDTO {
        val markering = MarkeringDTO(
            markeringstype = MarkeringDTO.Markeringstype.KONTORSPERRE,
            markertDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
            markertAv = "Z123456",
            kontorsperreEnhet = enhet,
            kontorsperreGT = gt
        )
        return this.copy(
            kontorsperre = true,
            markeringer = if (this.markeringer == null) listOf(markering) else this.markeringer!!.plus(markering)
        )
    }

    private fun HenvendelseDTO.somKassert(): HenvendelseDTO {
        return this.copy(
            kasseringsDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
            meldinger = this.meldinger?.map { it.copy(fritekst = "") }
        )
    }
}
