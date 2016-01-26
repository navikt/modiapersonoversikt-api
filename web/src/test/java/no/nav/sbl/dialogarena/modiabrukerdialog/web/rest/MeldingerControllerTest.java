package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MeldingerControllerTest {

    static {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    private HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
    private AnsattService ansattServiceMock = mock(AnsattService.class);
    private MeldingerController meldingerController = new MeldingerController();
    private String cookieNavn = "saksbehandlerinnstillinger-" + getSubjectHandler().getUid();;

    @Before
    public void setup() {
        final HenvendelseBehandlingService henvendelseBehandlingServiceMock = mock(HenvendelseBehandlingService.class);

        when(ansattServiceMock.hentEnhetsliste()).thenReturn(Collections.singletonList(new AnsattEnhet("0", "")));
        when(henvendelseBehandlingServiceMock.hentMeldinger(anyString(), anyString())).thenReturn(Collections.<Melding>emptyList());

        Whitebox.setInternalState(meldingerController, "ansattService", ansattServiceMock);
        Whitebox.setInternalState(meldingerController, "henvendelse", henvendelseBehandlingServiceMock);
        Whitebox.setInternalState(meldingerController, "searcher", mock(MeldingerSok.class));
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