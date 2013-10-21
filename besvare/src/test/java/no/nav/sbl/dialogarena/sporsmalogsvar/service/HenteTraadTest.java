package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.TjenesterMock;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
        when(besvareHenvendelsePortType.hentSporsmalOgSvar(anyString())).thenReturn(sporsmalOgKlargjortSvar("OST", "svar-id"));
        Traad traad = service.hentTraad("12345612345", "1").get();
        assertThat(traad.getTema(), is("OST"));
    }

    @Test
    public void svarFaarIdFraTomtSvarFraMottaksbehandling() {
        when(besvareHenvendelsePortType.hentSporsmalOgSvar(anyString())).thenReturn(sporsmalOgKlargjortSvar("OST", "svar-id"));
        Traad traad = service.hentTraad("12345612345", "1").get();
        assertThat(traad.getSvar().behandlingId, is("svar-id"));
    }

    @Test
    public void hentTraadFinnerAlleMeldinger() {
        when(besvareHenvendelsePortType.hentSporsmalOgSvar(anyString())).thenReturn(sporsmalOgKlargjortSvar("OST", "svar-id"));
        List<WSHenvendelse> henvendelser = TjenesterMock.HENVENDELSER.collect();
        when(henvendelsePortType.hentHenvendelseListe(anyString(), anyListOf(String.class))).thenReturn(henvendelser);
        Traad traad = service.hentTraad("12345612345", "1").get();

        assertTrue(traad.erSensitiv);
        assertThat(traad.getDialog(), hasSize(henvendelser.size()));
        assertThat(traad.getTidligereDialog(), hasSize(henvendelser.size() - 1));
        assertThat(traad.getSisteMelding().fritekst, startsWith("Nei det kan du si."));
        assertThat(traad.getDialog().get(0), is(traad.getSisteMelding()));
    }



    private WSSporsmalOgSvar sporsmalOgKlargjortSvar(String tema, String svarBehandlingId) {
        return new WSSporsmalOgSvar()
            .withSporsmal(new WSSporsmal().withTema(tema).withTraad(TjenesterMock.TRAAD))
            .withSvar(new WSSvar().withBehandlingsId(svarBehandlingId));
    }

}
