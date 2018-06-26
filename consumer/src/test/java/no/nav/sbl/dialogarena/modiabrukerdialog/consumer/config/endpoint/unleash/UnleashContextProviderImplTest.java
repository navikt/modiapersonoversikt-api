package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash;

import no.finn.unleash.UnleashContext;
import no.finn.unleash.UnleashContextProvider;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UnleashContextProviderImplTest {

    public static final String SESSION_ID = "1234abc";
    public static final String IDENT = "Z999999";
    public static final String REMOTE_ADDR = "123.123.123.123";
    public static final String VALGT_ENHET = "0118";

    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    private SubjectHandler subjectHandler;
    private ServletRequestAttributes requestAttributes;
    private MockHttpServletRequest request;
    private UnleashContextProvider contextProvider;
    private HttpSession session;

    @BeforeEach
    void init() {
        saksbehandlerInnstillingerService = mock(SaksbehandlerInnstillingerService.class);
        subjectHandler = mock(SubjectHandler.class);

        request = new MockHttpServletRequest();
        session = mock(HttpSession.class);
        request.setSession(session);
        request.setRemoteAddr(REMOTE_ADDR);
        requestAttributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(requestAttributes);

        contextProvider = new UnleashContextProviderImpl(saksbehandlerInnstillingerService, subjectHandler);
    }

    @Test
    void getContextPopulatesAllFieldsCorrectly() {
        when(subjectHandler.getUid()).thenReturn(IDENT);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);
        when(session.getId()).thenReturn(SESSION_ID);

        UnleashContext context = contextProvider.getContext();

        assertThat(context.getUserId().get(), is(IDENT));
        assertThat(context.getSessionId().get(), is(SESSION_ID));
        assertThat(context.getRemoteAddress().get(), is(REMOTE_ADDR));
        assertThat(context.getProperties().size(), is(1));
        assertThat(context.getProperties().get("valgtEnhet"), is(VALGT_ENHET));
    }

}