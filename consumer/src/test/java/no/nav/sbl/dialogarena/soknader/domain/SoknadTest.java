package no.nav.sbl.dialogarena.soknader.domain;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

import static javax.xml.datatype.DatatypeFactory.newInstance;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class SoknadTest {

    @Test
    public void testThatTransformWorks() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        DateTime startDate = now().minusDays(10);
        DateTime sluttDate = now().minusDays(4);
        DateTime sluttNavDate = now().minusDays(5);
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(startDate))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSluttNAVtid(createXmlGregorianCalander(sluttNavDate))
                .withSlutt(createXmlGregorianCalander(sluttDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getTittel(), is(equalTo("tittel")));
        assertThat(soknad.getInnsendtDato(), is(equalTo(startDate)));
        assertThat(soknad.getNormertBehandlingsTid(), is(equalTo("10 dager")));
        assertThat(soknad.getFerdigDato(), is(equalTo(sluttNavDate)));
    }

    @Test
    public void ferdigDatoIsTakenFromNAVSluttTid() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        DateTime startDate = now().minusDays(10);
        DateTime sluttNavDate = now().minusDays(5);
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(startDate))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSluttNAVtid(createXmlGregorianCalander(sluttNavDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getFerdigDato(), is(equalTo(sluttNavDate)));
    }

    @Test
    public void ferdigDatoIsTakenFromSluttTidIfNoNAVSluttTid() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        DateTime startDate = now().minusDays(10);
        DateTime sluttDate = now().minusDays(5);
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(startDate))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSlutt(createXmlGregorianCalander(sluttDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getFerdigDato(), is(equalTo(sluttDate)));
    }

    @Test
    public void statusIsSetWhenFerdigDatoIsSet() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        DateTime startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 10);
        DateTime sluttDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1);
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(startDate))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSlutt(createXmlGregorianCalander(sluttDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.NYLIG_FERDIG)));
    }


    @Test
    public void statusIsGammelWhenFerdigDatoIsSetAndMoreThan28DaysSinceDone() throws Exception {
        DateTime startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 20);
        DateTime sluttDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 1);
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(startDate))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSlutt(createXmlGregorianCalander(sluttDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.GAMMEL_FERDIG)));
    }

    @Test
    public void statusIsSetToUnderBehanldingWhenUnderBehanldingDatoIsSet() throws Exception {
        DateTime startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 20);
        DateTime startNavDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1);
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(startDate))
                .withStartNAVtid(createXmlGregorianCalander(startNavDate))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.UNDER_BEHANDLING)));
    }

    @Test
    public void statusIsSetToMottatWhenUnderBehanldingDatoIsNotSetAndFerdigDatoIsNotSet() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        DateTime startDate = now().minusDays(10);
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(startDate))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.MOTTATT)));
    }

    private XMLGregorianCalendar createXmlGregorianCalander(DateTime date) throws Exception {
        return newInstance().newXMLGregorianCalendar(date.toGregorianCalendar());
    }

    private Behandlingstid createNormertBehandlingstid(int days) {
        Behandlingstidtyper behandlingstidtyper = new Behandlingstidtyper();
        behandlingstidtyper.setValue("dager");
        return new Behandlingstid().withTid(BigInteger.valueOf(days)).withType(behandlingstidtyper);
    }

}
