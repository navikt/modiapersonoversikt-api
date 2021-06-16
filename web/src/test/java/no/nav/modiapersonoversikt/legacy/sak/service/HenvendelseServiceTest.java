package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HenvendelseServiceTest {

    @Mock
    private HenvendelseSoknaderPortType henvendelseSoknaderPortType;

    @InjectMocks
    private HenvendelseService henvendelseService;

    @Test
    public void hentPaabegynteSoknader_henterSoknader_medStatusUnderArbeid() {
        when(henvendelseSoknaderPortType.hentSoknadListe("11111111111")).thenReturn(asList(
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
        assertThat(henvendelseService.hentPaabegynteSoknader("11111111111").size(), equalTo(2));
    }

    @Test
    public void gamlePaabegynteSoknaderFjernes() {
        System.setProperty("FJERN_SOKNADER_FOR_DATO", "2015-01-05");
        when(henvendelseSoknaderPortType.hentSoknadListe("11111111111")).thenReturn(asList(
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
        assertThat(henvendelseService.hentPaabegynteSoknader("11111111111").size(), equalTo(1));
    }

    @Test
    public void sokanderPaaGrenseverdiFjernes() {
        final String grense = "2015-01-01";
        System.setProperty("FJERN_SOKNADER_FOR_DATO", grense);
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
        System.setProperty("FJERN_SOKNADER_FOR_DATO", "2015-01-05");
        when(henvendelseSoknaderPortType.hentSoknadListe("11111111111")).thenReturn(asList(
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
        assertThat(henvendelseService.hentInnsendteSoknader("11111111111").size(), equalTo(1));
    }
}
