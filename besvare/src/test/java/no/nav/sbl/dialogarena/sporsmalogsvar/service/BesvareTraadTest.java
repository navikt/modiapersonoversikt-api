package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BesvareTraadTest {

    @Mock
    private BesvareHenvendelsePortType besvareHenvendelsePortType;

    private BesvareService service;

    @Before
    public void wireUpService() {
        service = new BesvareService(besvareHenvendelsePortType, null);
    }


    @Test
    public void leggerInnSvarIEnTraad() {
        Traad traad = new Traad("OST", "svar-42");
        traad.getSvar().fritekst = "Denne osten stinker";
        traad.erSensitiv = true;
        traad.getSvar().tema = "FRANSK OST";


        ArgumentCaptor<WSSvar> wsSvar = ArgumentCaptor.forClass(WSSvar.class);
        service.besvareSporsmal(traad);
        verify(besvareHenvendelsePortType).besvarSporsmal(wsSvar.capture());

        assertThat(wsSvar.getValue().getBehandlingsId(), is("svar-42"));
        assertThat(wsSvar.getValue().getFritekst(), is("Denne osten stinker"));
        assertThat(wsSvar.getValue().getTema(), is("FRANSK OST"));
        assertTrue(wsSvar.getValue().isSensitiv());


    }
}
