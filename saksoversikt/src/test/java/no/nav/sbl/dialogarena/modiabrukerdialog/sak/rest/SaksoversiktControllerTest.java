package no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SaksService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.ws.rs.core.Response;
import java.util.List;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksoversiktControllerTest {

    @Mock
    private TilgangskontrollService tilgangskontrollService;

    @Mock
    private SaksoversiktService saksoversiktService;

    @Mock
    private SaksService saksService;


    @InjectMocks
    private SaksoversiktController saksoversiktController;

    @Before
    public void before() {
//        setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

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

    @Test
    public void blirBlokkertOmManipulertCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(tilgangskontrollService.harGodkjentEnhet(request)).thenReturn(false);

        Response response = saksoversiktController.hentSakstema("11111111111", request);

        assertThat(response.getStatus(), is(403));
    }

    private List<Sakstema> lagSakstemaListe() {
        return asList(
                new Sakstema().withTemakode("PEN"),
                new Sakstema().withTemakode("TEST")
        );
    }

}
