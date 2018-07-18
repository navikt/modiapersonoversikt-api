package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HenvendelseServiceTest {

    @Mock
    private HenvendelseSoknaderPortType henvendelseSoknaderPortType;

    @InjectMocks
    private HenvendelseService henvendelseService;

    @Test
    public void hentPaabegynteSoknader_henterSoknader_medStatusUnderArbeid() {
        when(henvendelseSoknaderPortType.hentSoknadListe("12345678901")).thenReturn(asList(
                new WSSoknad()
                        .withHenvendelseStatus("UNDER_ARBEID")
                        .withOpprettetDato(new DateTime())
                        .withHenvendelseType("SOKNADSINNSENDING")
                        .withSistEndretDato(new DateTime()),
                new WSSoknad()
                        .withHenvendelseStatus("UNDER_ARBEID")
                        .withOpprettetDato(new DateTime())
                        .withHenvendelseType("SOKNADSINNSENDING")
                        .withSistEndretDato(new DateTime())
        ));
        assertThat(henvendelseService.hentPaabegynteSoknader("12345678901").size(), equalTo(2));
    }

    @Test
    public void gamlePaabegynteSoknaderFjernes() {
        System.setProperty("fjern.soknader.for.dato", "2015-01-05");
        when(henvendelseSoknaderPortType.hentSoknadListe("12345678901")).thenReturn(asList(
                new WSSoknad()
                        .withHenvendelseStatus("UNDER_ARBEID")
                        .withOpprettetDato(new DateTime())
                        .withHenvendelseType("SOKNADSINNSENDING")
                        .withSistEndretDato(new DateTime("2015-01-01")),
                new WSSoknad()
                        .withHenvendelseStatus("UNDER_ARBEID")
                        .withOpprettetDato(new DateTime())
                        .withHenvendelseType("SOKNADSINNSENDING")
                        .withSistEndretDato(new DateTime("2015-01-06"))
        ));
        assertThat(henvendelseService.hentPaabegynteSoknader("12345678901").size(), equalTo(1));
    }

    @Test
    public void sokanderPaaGrenseverdiFjernes() {
        final String grense = "2015-01-01";
        System.setProperty("fjern.soknader.for.dato", grense);
        when(henvendelseSoknaderPortType.hentSoknadListe("123")).thenReturn(asList(
                new WSSoknad()
                        .withHenvendelseStatus("UNDER_ARBEID")
                        .withOpprettetDato(new DateTime())
                        .withHenvendelseType("SOKNADSINNSENDING")
                        .withSistEndretDato(new DateTime(grense)),
                new WSSoknad()
                        .withHenvendelseStatus("UNDER_ARBEID")
                        .withOpprettetDato(new DateTime())
                        .withHenvendelseType("SOKNADSINNSENDING")
                        .withSistEndretDato(new DateTime("2015-01-06"))
        ));
        assertThat(henvendelseService.hentPaabegynteSoknader("123").size(), equalTo(1));
    }

    @Test
    public void gamleInnsendteSoknaderFjernes() {
        System.setProperty("fjern.soknader.for.dato", "2015-01-05");
        when(henvendelseSoknaderPortType.hentSoknadListe("12345678901")).thenReturn(asList(
                new WSSoknad()
                        .withHenvendelseStatus("FERDIG")
                        .withOpprettetDato(new DateTime())
                        .withHenvendelseType("SOKNADSINNSENDING")
                        .withSistEndretDato(new DateTime("2015-01-01")),
                new WSSoknad()
                        .withHenvendelseStatus("FERDIG")
                        .withOpprettetDato(new DateTime())
                        .withHenvendelseType("SOKNADSINNSENDING")
                        .withSistEndretDato(new DateTime("2015-01-06"))
        ));
        assertThat(henvendelseService.hentInnsendteSoknader("12345678901").size(), equalTo(1));
    }
}
