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
import static org.junit.Assert.assertThat;

public class SoknadTest {

    @Test
    public void testThatTransformWorks() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(new DateTime(2013, 10, 1, 12, 0)))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSluttNAVtid(createXmlGregorianCalander(new DateTime(2013, 10, 5, 12, 0)))
                .withSlutt(createXmlGregorianCalander(new DateTime(2013, 10, 6, 12, 0)));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getTittel(), is(equalTo("tittel")));
        assertThat(soknad.getInnsendtDato(), is(equalTo(new DateTime(2013, 10, 1, 12, 0))));
        assertThat(soknad.getNormertBehandlingsTid(), is(equalTo("10 dager")));
        assertThat(soknad.getFerdigDato(), is(equalTo(new DateTime(2013, 10, 5, 12, 0))));
    }

    @Test
    public void ferdigDatoIsTakenFromNAVSluttTid() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(new DateTime(2013, 10, 1, 12, 0)))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSluttNAVtid(createXmlGregorianCalander(new DateTime(2013, 10, 5, 12, 0)));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getFerdigDato(), is(equalTo(new DateTime(2013, 10, 5, 12, 0))));
    }

    @Test
    public void ferdigDatoIsTakenFromSluttTidIfNoNAVSluttTid() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(new DateTime(2013, 10, 1, 12, 0)))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSlutt(createXmlGregorianCalander(new DateTime(2013, 10, 6, 12, 0)));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getFerdigDato(), is(equalTo(new DateTime(2013, 10, 6, 12, 0))));
    }

    @Test
    public void statusIsSetWhenFerdigDatoIsSet() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(new DateTime(2013, 10, 1, 12, 0)))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"))
                .withSlutt(createXmlGregorianCalander(new DateTime(2013, 10, 6, 12, 0)));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.NYLIG_FERDIG)));
    }

    @Test
    public void statusIsSetToUnderBehanldingWhenUnderBehanldingDatoIsSet() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(new DateTime(2013, 10, 1, 12, 0)))
                .withStartNAVtid(createXmlGregorianCalander(new DateTime(2013, 10, 1, 12, 0)))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"));
        Soknad soknad = Soknad.transformToSoknad(behandlingskjede);
        assertThat(soknad.getSoknadStatus(), is(equalTo(Soknad.SoknadStatus.UNDER_BEHANDLING)));
    }

    @Test
    public void statusIsSetToMottatWhenUnderBehanldingDatoIsNotSetAndFerdigDatoIsNotSet() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid(10))
                .withStart(createXmlGregorianCalander(new DateTime(2013, 10, 1, 12, 0)))
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
