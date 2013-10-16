package no.nav.sbl.dialogarena.soknader.domain;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

import static javax.xml.datatype.DatatypeFactory.newInstance;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class SoknadTest {

    private Behandlingskjede behandlingskjede;
    private DateTime startDate;
    private DateTime sluttDate;

    @Before
    public void setUp() {
        behandlingskjede = new Behandlingskjede()
                .withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"));
        startDate = now().minusDays(10);
        sluttDate = now().minusDays(4);
    }

    @Test
    public void testThatTransformWorks() throws Exception {
        DateTime sluttNavDate = now().minusDays(5);
        behandlingskjede.withStart(createXmlGregorianCalander(startDate))
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
        DateTime sluttNavDate = now().minusDays(5);
        behandlingskjede.withStart(createXmlGregorianCalander(startDate))
                .withSluttNAVtid(createXmlGregorianCalander(sluttNavDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getFerdigDato(), is(equalTo(sluttNavDate)));
    }

    @Test
    public void ferdigDatoIsTakenFromSluttTidIfNoNAVSluttTid() throws Exception {
        behandlingskjede.withStart(createXmlGregorianCalander(startDate))
                .withSlutt(createXmlGregorianCalander(sluttDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getFerdigDato(), is(equalTo(sluttDate)));
    }

    @Test
    public void statusIsSetWhenFerdigDatoIsSet() throws Exception {
        startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 10);
        sluttDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1);
        behandlingskjede.withStart(createXmlGregorianCalander(startDate))
                .withSlutt(createXmlGregorianCalander(sluttDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.NYLIG_FERDIG)));
    }


    @Test
    public void statusIsGammelWhenFerdigDatoIsSetAndMoreThan28DaysSinceDone() throws Exception {
        startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 20);
        sluttDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 1);
        behandlingskjede.withStart(createXmlGregorianCalander(startDate))
                .withSlutt(createXmlGregorianCalander(sluttDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.GAMMEL_FERDIG)));
    }

    @Test
    public void statusIsSetToUnderBehanldingWhenUnderBehanldingDatoIsSet() throws Exception {
        startDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 20);
        DateTime startNavDate = now().minusDays(Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1);
        behandlingskjede.withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(startDate))
                .withStartNAVtid(createXmlGregorianCalander(startNavDate));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.UNDER_BEHANDLING)));
    }

    @Test
    public void statusIsSetToMottatWhenUnderBehanldingDatoIsNotSetAndFerdigDatoIsNotSet() throws Exception {
        startDate = now().minusDays(10);
        behandlingskjede.withStart(createXmlGregorianCalander(startDate));
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
