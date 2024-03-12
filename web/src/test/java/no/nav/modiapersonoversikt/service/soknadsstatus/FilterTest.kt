package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Behandling
import no.nav.modiapersonoversikt.service.soknadsstatus.BehandlingMockUtils.createBehandling
import no.nav.modiapersonoversikt.service.soknadsstatus.Filter.erKvitteringstype
import no.nav.modiapersonoversikt.service.soknadsstatus.Filter.lovligMenUtgaattStatusEllerUnderBehandling
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertFalse

class FilterTest {
    @Test
    fun `sjekker at det er lovlig status på behandling`() {
        var behandling = createBehandling()
        assertTrue(Filter.harLovligStatusPaBehandling(behandling))

        behandling = createBehandling().copy(behandlingsType = "ae0002")
        assertFalse(Filter.harLovligStatusPaBehandling(behandling), "Skal filtrere bort SEND_SOKNAD_KVITTERINGSTYPE")

        behandling = createBehandling().copy(status = Behandling.Status.AVBRUTT)
        assertFalse(Filter.harLovligStatusPaBehandling(behandling), "Skal filtrere bort behandlinger som er avbrutt")
    }

    @Test
    fun `sjekker at det er kvitteringstype`() {
        assertTrue(erKvitteringstype("ae0002"))
        assertTrue(erKvitteringstype("ae0001"))
        assertFalse(erKvitteringstype("jkwej"))
    }

    @Test
    fun `sjekker at det er lovlig behandlingstype eller avsluttet kvittering`() {
        var behandling = createBehandling()
        assertTrue(Filter.harLovligBehandlingstypeEllerAvsluttetKvittering(behandling))
    }

    @Test
    fun `sjekker at saken er ferdig for under 1 måned siden eller at det er innsendt søknad`() {
        var behandling =
            createBehandling().copy(behandlingsType = "ae0002", status = Behandling.Status.UNDER_BEHANDLING)
        assertFalse(
            Filter.erFerdigUnder1MndSidenEllerInnsendtSoknad(
                type = behandling.behandlingsType,
                behandling = behandling,
            ),
            "Skal filtrere bort behandling om behandlingstypen er kvittering men behandlingen er fortsatt under behandling",
        )
        assertTrue(
            Filter.erFerdigUnder1MndSidenEllerInnsendtSoknad(
                type = behandling.behandlingsType,
                behandling.copy(status = Behandling.Status.FERDIG_BEHANDLET),
            ),
            "Skal ikke filtrere bort behandlinger med behandlingstype kvittering om behandlingen er ferdig",
        )

        behandling =
            createBehandling().copy(
                status = Behandling.Status.FERDIG_BEHANDLET,
                sluttTidspunkt = LocalDateTime.now().minusDays(4),
                behandlingsType = "asdsad",
            )
        assertFalse(
            Filter.erFerdigUnder1MndSidenEllerInnsendtSoknad(type = behandling.behandlingsType, behandling),
            "Skal filtrere bort behandlinger om behandlingen er avsluttet for mindre enn måned siden, men behandlingstypen er ugyldig",
        )

        behandling = behandling.copy(behandlingsType = "ae0047")
        assertTrue(
            Filter.erFerdigUnder1MndSidenEllerInnsendtSoknad(type = behandling.behandlingsType, behandling),
            """
            Skal ikke filtrere bort behandlinger om behandlingen er avsluttet for mindre enn en måned siden og den har lovlig behandlingstype
            """,
        )
    }

    @Test
    fun `sjekker om saken har utgått status eller er under behandling`() {
        var behandling =
            createBehandling().copy(
                sluttTidspunkt = LocalDateTime.now().minusMonths(2),
                status = Behandling.Status.FERDIG_BEHANDLET,
            )
        assertFalse(
            lovligMenUtgaattStatusEllerUnderBehandling(behandling.behandlingsType, behandling),
            "Om saken er lukket og det er mer enn 1 måned siden skal den filtreres bort",
        )

        behandling = createBehandling().copy(status = Behandling.Status.UNDER_BEHANDLING, behandlingsType = "ae0047")
        assertTrue(
            lovligMenUtgaattStatusEllerUnderBehandling(behandling.behandlingsType, behandling),
            "Skal ikke filtrere bort behandlinger som er under behandling med en gyldig behandlingstype",
        )
    }
}
