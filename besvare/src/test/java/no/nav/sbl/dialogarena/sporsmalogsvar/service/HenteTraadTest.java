package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.TjenesterMock;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HenteTraadTest {

    @Mock
    private BesvareHenvendelsePortType besvareHenvendelsePortType;

    @Mock
    private HenvendelsePortType henvendelsePortType;

    private BesvareService service;

    @Before
    public void wireUpService() {
        service = new BesvareService(besvareHenvendelsePortType, henvendelsePortType);
    }

    @Test
    public void hentTraadGirNoneNaarBesvareHenvendelseIkkeReturnererNoeSporsmalOgSvar() {
        assertThat(service.hentTraad("12345612345", "1"), is(Optional.<Traad>none()));
    }

    @Test
    public void hentTraadFinnerTema() {
        when(besvareHenvendelsePortType.hentSporsmalOgSvar(anyString())).thenReturn(sporsmal("OST"));
        Traad traad = service.hentTraad("12345612345", "1").get();
        assertThat(traad.getTema(), is("OST"));
    }

    @Test
    public void hentTraadFinnerAlleMeldinger() {
        when(besvareHenvendelsePortType.hentSporsmalOgSvar(anyString())).thenReturn(sporsmal("OST"));
        when(henvendelsePortType.hentHenvendelseListe(anyString(), anyListOf(String.class))).thenReturn(TjenesterMock.HENVENDELSER.collect());
        Traad traad = service.hentTraad("12345612345", "1").get();

        assertThat(traad.getTema(), is("OST"));
        assertThat(traad.getDialog(), hasSize(5));
        assertThat(traad.getTidligereDialog(), hasSize(4));
        assertThat(traad.getSisteMelding().fritekst, startsWith("Nei det kan du si."));
        assertThat(traad.getDialog().get(0), is(traad.getSisteMelding()));
    }



    private WSSporsmalOgSvar sporsmal(String tema) {
        return new WSSporsmalOgSvar().withSporsmal(new WSSporsmal().withTema(tema).withTraad(TjenesterMock.TRAAD));
    }

}
