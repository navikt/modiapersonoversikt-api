package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.createWSSak;
import static no.nav.sbl.dialogarena.sak.transformers.TemaTransformer.hentSistOppdaterteLovligeBehandling;
import static no.nav.sbl.dialogarena.sak.transformers.TemaTransformer.tilTema;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemaTransformerTest {

    @Mock
    private BulletproofKodeverkService bulletproofKodeverkService;
    @Mock
    private Filter filter;

    @Before
    public void setup() {
        when(bulletproofKodeverkService.getTemanavnForTemakode(anyString(), anyString())).thenReturn(new ResultatWrapper("Dagpenger"));
    }

    @Test
    public void mapperTilTema() {
        when(filter.filtrerBehandlinger(anyList(), anyObject())).thenReturn(getBehandlinger());

        WSSak sak = createWSSak()
                .withSakstema(new WSSakstemaer().withValue("DAG"));
        Tema tema = tilTema(sak, bulletproofKodeverkService, filter);

        assertThat(tema.temakode, is("DAG"));
        assertThat(tema.temanavn, is("Dagpenger"));
    }

    @Test
    public void skalReturnereDato1970HvisBehandlingsdatoIkkeFinnes() {
        when(filter.filtrerBehandlinger(anyList(), anyObject())).thenReturn(getBehandlingUtenBehandlingsdato());

        WSSak sak = createWSSak()
                .withSakstema(new WSSakstemaer().withValue("DAG"));
        org.joda.time.DateTime dato = hentSistOppdaterteLovligeBehandling(sak, filter);

        assertThat(dato.getYear(), is(1970));
    }

    private List<Behandling> getBehandlingUtenBehandlingsdato() {
        return asList(
                new Behandling().withBehandlingskjedeId("1").withBehandlingsDato(null));
    }

    private List<Behandling> getBehandlinger() {
        return asList(
                new Behandling().withBehandlingskjedeId("1").withBehandlingsDato(now()),
                new Behandling().withBehandlingskjedeId("2").withBehandlingsDato(now()),
                new Behandling().withBehandlingskjedeId("3").withBehandlingsDato(now())
        );
    }
}
