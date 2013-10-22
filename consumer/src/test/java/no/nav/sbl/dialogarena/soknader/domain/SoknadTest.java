package no.nav.sbl.dialogarena.soknader.domain;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.GAMMEL_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.MOTTATT;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.NYLIG_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.UNDER_BEHANDLING;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class SoknadTest {

    private WSBehandlingskjede behandlingskjede;
    private DateTime startDate;
    private DateTime sluttDate;

    @Before
    public void setUp() {
        behandlingskjede = new WSBehandlingskjede()
                .withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withKodeverksRef("tittel"));
        startDate = now().minusDays(10);
        sluttDate = now().minusDays(4);
    }

    @Test
    public void testThatTransformWorks() throws Exception {
        DateTime sluttNavDate = now().minusDays(5);
        behandlingskjede.withStart(startDate)
                .withSluttNAVtid(sluttNavDate)
                .withSlutt(sluttDate);
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getTittelKodeverk(), is(equalTo("tittel")));
        assertThat(soknad.getInnsendtDato(), is(equalTo(startDate)));
        assertThat(soknad.getNormertBehandlingsTid(), is(equalTo("10 dager")));
        assertThat(soknad.getFerdigDato(), is(equalTo(sluttNavDate)));
    }

    @Test
    public void ferdigDatoIsTakenFromNAVSluttTid() throws Exception {
        DateTime sluttNavDate = now().minusDays(5);
        behandlingskjede.withStart(startDate)
                .withSluttNAVtid(sluttNavDate);
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getFerdigDato(), is(equalTo(sluttNavDate)));
    }

    @Test
    public void ferdigDatoIsTakenFromSluttTidIfNoNAVSluttTid() throws Exception {
        behandlingskjede.withStart(startDate)
                .withSlutt(sluttDate);
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getFerdigDato(), is(equalTo(sluttDate)));
    }

    @Test
    public void status_WhenFerdigDatoIsSet_StatusIsNyligFerdig() throws Exception {
        startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 10);
        sluttDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1);
        behandlingskjede.withStart(startDate)
                .withSlutt(sluttDate);
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(NYLIG_FERDIG)));
    }

    @Test
    public void status_WhenFerdigDatoIsSetAndMoreThan28DaysSinceDone_StatusIsGammelFerdig() throws Exception {
        startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 20);
        sluttDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 1);
        behandlingskjede.withStart(startDate)
                .withSlutt(sluttDate);
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(GAMMEL_FERDIG)));
    }

    @Test
    public void status_WhenUnderBehandlingDatoIsSet_StatusIsUnderBehandling() throws Exception {
        startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 20);
        DateTime startNavDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1);
        behandlingskjede.withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(startDate)
                .withStartNAVtid(startNavDate);
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(UNDER_BEHANDLING)));
    }

    @Test
    public void status_WhenOnlyStartIsSet_UseDefault_StatusIsMottatt() throws Exception {
        startDate = now().minusDays(10);
        behandlingskjede.withStart(startDate);
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(MOTTATT)));
    }

    private WSBehandlingstid createNormertBehandlingstid(int days) {
        WSBehandlingstidtyper behandlingstidtyper = new WSBehandlingstidtyper();
        behandlingstidtyper.setValue("dager");
        return new WSBehandlingstid().withTid(BigInteger.valueOf(days)).withType(behandlingstidtyper);
    }

}
