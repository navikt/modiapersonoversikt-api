package no.nav.sbl.dialogarena.saksoversikt.service.service;

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
}
