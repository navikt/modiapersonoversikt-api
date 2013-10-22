package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.Mottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.config.TjenesterMock;
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
    private HenvendelsePortType henvendelsePortType;

    @Mock
    private Mottaksbehandling mottaksbehandling;

    private Traader service;

    @Before
    public void wireUpService() {
        service = new Traader(mottaksbehandling, henvendelsePortType);
    }

    @Test
    public void hentTraadGirNoneNaarBesvareHenvendelseIkkeReturnererNoeSporsmalOgSvar() {
        when(mottaksbehandling.hentSporsmalOgSvar(anyString())).thenReturn(Optional.<Record<SporsmalOgSvar>>none());
        assertThat(service.hentTraad("12345612345", "1"), is(Optional.<Traad>none()));
    }

    @Test
    public void hentTraadFinnerTema() {
        when(mottaksbehandling.hentSporsmalOgSvar(anyString())).thenReturn(sporsmalOgKlargjortSvar(Tema.HJELPEMIDLER, "svar-id"));
        Traad traad = service.hentTraad("12345612345", "1").get();
        assertThat(traad.getTema(), is(Tema.HJELPEMIDLER));
    }

    @Test
    public void svarFaarIdFraTomtSvarFraMottaksbehandling() {
        when(mottaksbehandling.hentSporsmalOgSvar(anyString())).thenReturn(sporsmalOgKlargjortSvar(Tema.HJELPEMIDLER, "svar-id"));
        Traad traad = service.hentTraad("12345612345", "1").get();
        assertThat(traad.getSvar().behandlingId, is("svar-id"));
    }

    @Test
    public void hentTraadFinnerAlleMeldinger() {
        when(mottaksbehandling.hentSporsmalOgSvar(anyString())).thenReturn(sporsmalOgKlargjortSvar(Tema.HJELPEMIDLER, "svar-id"));
        List<WSHenvendelse> henvendelser = TjenesterMock.HENVENDELSER.collect();
        when(henvendelsePortType.hentHenvendelseListe(anyString(), anyListOf(String.class))).thenReturn(henvendelser);
        Traad traad = service.hentTraad("12345612345", "1").get();

        assertTrue(traad.erSensitiv);
        assertThat(traad.getDialog(), hasSize(henvendelser.size()));
        assertThat(traad.getTidligereDialog(), hasSize(henvendelser.size() - 1));
        assertThat(traad.getSisteMelding().fritekst, startsWith("Nei det kan du si."));
        assertThat(traad.getDialog().get(0), is(traad.getSisteMelding()));
    }



    private Optional<Record<SporsmalOgSvar>> sporsmalOgKlargjortSvar(Tema tema, String svarBehandlingId) {
        return Optional.optional(new Record<SporsmalOgSvar>()
                .with(SporsmalOgSvar.tema, tema)
                .with(SporsmalOgSvar.traad, TjenesterMock.TRAAD)
                .with(SporsmalOgSvar.behandlingsid, svarBehandlingId));

    }

}
