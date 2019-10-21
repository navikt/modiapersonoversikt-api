package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.TilgangskontrollMock;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class MeldingerControllerTest {

    static {
//        System.setProperty(SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
    }

    private HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
    private AnsattService ansattServiceMock = mock(AnsattService.class);
    private MeldingerController meldingerController = new MeldingerController();
    private String cookieNavn = "saksbehandlerinnstillinger-" + "Z999999"; // getSubjectHandler().getUid();

    @Before
    public void setup() {
        final HenvendelseBehandlingService henvendelseBehandlingServiceMock = mock(HenvendelseBehandlingService.class);

        when(ansattServiceMock.hentEnhetsliste()).thenReturn(Collections.singletonList(new AnsattEnhet("0", "")));
        when(henvendelseBehandlingServiceMock.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(Collections.emptyList()));

        setField(meldingerController, "ansattService", ansattServiceMock);
        setField(meldingerController, "henvendelse", henvendelseBehandlingServiceMock);
        setField(meldingerController, "searcher", mock(MeldingerSok.class));
        setField(meldingerController, "tilgangskontroll", TilgangskontrollMock.get());
    }

    @Test
    public void indekseringSkalReturnere200DersomIdentHarTilgangTilEnhet() throws Exception {
        when(httpServletRequestMock.getCookies()).thenReturn(new Cookie[]{new Cookie(cookieNavn, "0")});
        final Response response = meldingerController.indekser("10108000398", httpServletRequestMock);
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void indekseringSkalReturnere401DersomIdentIkkeHarTilgangTilEnhet() throws Exception {
        when(httpServletRequestMock.getCookies()).thenReturn(new Cookie[]{new Cookie(cookieNavn, "1")});
        final Response response = meldingerController.indekser("10108000398", httpServletRequestMock);
        assertThat(response.getStatus(), is(Response.Status.UNAUTHORIZED.getStatusCode()));
    }
}