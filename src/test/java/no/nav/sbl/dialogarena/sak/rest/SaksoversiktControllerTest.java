package no.nav.sbl.dialogarena.sak.rest;

import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksoversiktControllerTest {

    @Mock
    private TilgangskontrollService tilgangskontrollService;

    @InjectMocks
    private SaksoversiktController saksoversiktController;

    @Test
    public void harTilgangTilAlleTema() {
        List<Sakstema> sakstemaList = lagSakstemaListe();
        when(tilgangskontrollService.harEnhetTilgangTilTema(anyString(), anyString())).thenReturn(true);


        List<ModiaSakstema> modiaSakstemaList = saksoversiktController.mapTilModiaSakstema(sakstemaList, "enhet");

        assertThat(modiaSakstemaList.stream().allMatch(modiaSakstema -> modiaSakstema.harTilgang), is(true));
    }


    @Test
    public void harIkkeTilgangTilNoenTema() {
        List<Sakstema> sakstemaList = lagSakstemaListe();
        when(tilgangskontrollService.harEnhetTilgangTilTema(anyString(), anyString())).thenReturn(false);

        List<ModiaSakstema> modiaSakstemaList = saksoversiktController.mapTilModiaSakstema(sakstemaList, "enhet");

        assertThat(modiaSakstemaList.stream().allMatch(modiaSakstema -> modiaSakstema.harTilgang), is(false));
    }

    private List<Sakstema> lagSakstemaListe() {
        return asList(
                new Sakstema().withTemakode("PEN"),
                new Sakstema().withTemakode("TEST")
        );
    }

}
